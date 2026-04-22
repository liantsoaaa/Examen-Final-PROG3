package com.hei.openapi_federation.repository;

import com.hei.openapi_federation.entity.*;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepository {

    private final DataSource dataSource;

    public PaymentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Long insertPayment(Long memberId, Long collectivityId,
                              Long membershipFeeId, Long accountId,
                              Double amount, String paymentMode,
                              Long recordedBy) {
        String paymentType = membershipFeeId != null ? "PERIODIC_DUES" : "REGISTRATION_FEE";
        String sql = """
                INSERT INTO payment
                    (id_member, id_collectivity, id_cotisation_plan, id_account,
                     amount, payment_date, payment_mode, payment_type, recorded_by)
                VALUES (?, ?, ?, ?, ?, ?, ?::payment_mode, ?::payment_type, ?)
                RETURNING id
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, memberId);
            ps.setLong(2, collectivityId);
            if (membershipFeeId != null) ps.setLong(3, membershipFeeId);
            else ps.setNull(3, Types.INTEGER);
            ps.setLong(4, accountId);
            ps.setDouble(5, amount);
            ps.setTimestamp(6, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(7, paymentMode);
            ps.setString(8, paymentType);
            ps.setLong(9, recordedBy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting payment: " + e.getMessage(), e);
        }
        throw new RuntimeException("Failed to insert payment");
    }

    public void insertAccountMovement(Long accountId, Double amount) {
        String sql = """
                INSERT INTO account_movement (id_account, type, amount, created_at)
                VALUES (?, 'IN'::movement_type, ?, NOW())
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ps.setDouble(2, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting account movement: " + e.getMessage(), e);
        }
    }

    public Optional<MemberPayment> findPaymentById(Long id) {
        String sql = """
                SELECT p.id, p.amount, p.payment_mode, p.payment_date, p.id_account
                FROM payment p
                WHERE p.id = ?
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MemberPayment mp = new MemberPayment();
                    mp.setId(String.valueOf(rs.getLong("id")));
                    mp.setAmount(rs.getDouble("amount"));
                    mp.setCreationDate(rs.getTimestamp("payment_date").toLocalDateTime().toLocalDate());
                    String mode = rs.getString("payment_mode");
                    mp.setPaymentMode(dbModeToPaymentMode(mode));
                    return Optional.of(mp);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payment: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<CollectivityTransaction> findTransactionsByCollectivityAndPeriod(
            Long collectivityId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT p.id, p.amount, p.payment_mode, p.payment_date,
                       p.id_account,
                       m.id AS m_id, m.first_name, m.last_name, m.birth_date,
                       m.address, m.email, m.phone, m.job, m.gender
                FROM payment p
                JOIN member m ON m.id = p.id_member
                WHERE p.id_collectivity = ?
                  AND DATE(p.payment_date) >= ?
                  AND DATE(p.payment_date) <= ?
                ORDER BY p.payment_date DESC
                """;
        List<CollectivityTransaction> transactions = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, collectivityId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CollectivityTransaction t = new CollectivityTransaction();
                    t.setId(String.valueOf(rs.getLong("id")));
                    t.setAmount(rs.getDouble("amount"));
                    t.setCreationDate(rs.getTimestamp("payment_date").toLocalDateTime().toLocalDate());
                    t.setPaymentMode(dbModeToPaymentMode(rs.getString("payment_mode")));

                    Member member = new Member();
                    member.setId(String.valueOf(rs.getLong("m_id")));
                    member.setFirstName(rs.getString("first_name"));
                    member.setLastName(rs.getString("last_name"));
                    member.setEmail(rs.getString("email"));
                    t.setMemberDebited(member);

                    transactions.add(t);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transactions: " + e.getMessage(), e);
        }
        return transactions;
    }

    public Optional<FinancialAccount> findAccountById(Long accountId) {
        String cashSql = """
                SELECT a.id,
                       COALESCE(SUM(CASE WHEN am.type='IN' THEN am.amount ELSE 0 END), 0)
                     - COALESCE(SUM(CASE WHEN am.type='OUT' THEN am.amount ELSE 0 END), 0) AS balance
                FROM account a
                JOIN cash_account ca ON ca.id_account = a.id
                LEFT JOIN account_movement am ON am.id_account = a.id
                WHERE a.id = ?
                GROUP BY a.id
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(cashSql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CashAccount ca = new CashAccount();
                    ca.setId(String.valueOf(rs.getLong("id")));
                    ca.setAmount(rs.getDouble("balance"));
                    return Optional.of(ca);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching cash account: " + e.getMessage(), e);
        }

        String mobileSql = """
                SELECT a.id, mma.holder_name, mma.service_name, mma.phone_number,
                       COALESCE(SUM(CASE WHEN am.type='IN' THEN am.amount ELSE 0 END), 0)
                     - COALESCE(SUM(CASE WHEN am.type='OUT' THEN am.amount ELSE 0 END), 0) AS balance
                FROM account a
                JOIN mobile_money_account mma ON mma.id_account = a.id
                LEFT JOIN account_movement am ON am.id_account = a.id
                WHERE a.id = ?
                GROUP BY a.id, mma.holder_name, mma.service_name, mma.phone_number
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(mobileSql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MobileBankingAccount mba = new MobileBankingAccount();
                    mba.setId(String.valueOf(rs.getLong("id")));
                    mba.setHolderName(rs.getString("holder_name"));
                    mba.setMobileNumber(rs.getString("phone_number"));
                    mba.setAmount(rs.getDouble("balance"));
                    String svc = rs.getString("service_name");
                    if (svc != null) mba.setMobileBankingService(MobileBankingService.valueOf(svc));
                    return Optional.of(mba);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching mobile account: " + e.getMessage(), e);
        }

        String bankSql = """
                SELECT a.id, ba.holder_name, ba.bank_name, ba.bank_code,
                       ba.branch_code, ba.account_number, ba.rib_key,
                       COALESCE(SUM(CASE WHEN am.type='IN' THEN am.amount ELSE 0 END), 0)
                     - COALESCE(SUM(CASE WHEN am.type='OUT' THEN am.amount ELSE 0 END), 0) AS balance
                FROM account a
                JOIN bank_account ba ON ba.id_account = a.id
                LEFT JOIN account_movement am ON am.id_account = a.id
                WHERE a.id = ?
                GROUP BY a.id, ba.holder_name, ba.bank_name, ba.bank_code,
                         ba.branch_code, ba.account_number, ba.rib_key
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(bankSql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BankAccount bka = new BankAccount();
                    bka.setId(String.valueOf(rs.getLong("id")));
                    bka.setHolderName(rs.getString("holder_name"));
                    bka.setBankCode(rs.getString("bank_code"));
                    bka.setBankBranchCode(rs.getString("branch_code"));
                    bka.setBankAccountNumber(rs.getString("account_number"));
                    bka.setBankAccountKey(rs.getString("rib_key"));
                    bka.setAmount(rs.getDouble("balance"));
                    String bank = rs.getString("bank_name");
                    if (bank != null) bka.setBankName(Bank.valueOf(bank));
                    return Optional.of(bka);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bank account: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public boolean accountBelongsToMemberCollectivity(Long accountId, Long memberId) {
        String sql = """
                SELECT 1 FROM account a
                JOIN member_collectivity mc ON mc.id_collectivity = a.id_collectivity
                WHERE a.id = ? AND mc.id_member = ? AND mc.end_date IS NULL
                LIMIT 1
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ps.setLong(2, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking account ownership: " + e.getMessage(), e);
        }
    }

    public Optional<Long> getCollectivityIdForMember(Long memberId) {
        String sql = """
                SELECT id_collectivity FROM member_collectivity
                WHERE id_member = ? AND end_date IS NULL
                LIMIT 1
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getLong("id_collectivity"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching member collectivity: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    private PaymentMode dbModeToPaymentMode(String dbMode) {
        if (dbMode == null) return PaymentMode.CASH;
        return switch (dbMode) {
            case "CASH"          -> PaymentMode.CASH;
            case "MOBILE_MONEY"  -> PaymentMode.MOBILE_BANKING;
            case "BANK_TRANSFER" -> PaymentMode.BANK_TRANSFER;
            default              -> PaymentMode.CASH;
        };
    }
}