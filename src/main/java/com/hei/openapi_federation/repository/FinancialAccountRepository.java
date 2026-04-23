package com.hei.openapi_federation.repository;

import com.hei.openapi_federation.entity.*;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FinancialAccountRepository {

    private final DataSource dataSource;

    public FinancialAccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<FinancialAccount> findByCollectivityId(Long collectivityId, LocalDate atDate) {
        LocalDate effectiveDate = atDate != null ? atDate : LocalDate.now();
        List<FinancialAccount> accounts = new ArrayList<>();

        accounts.addAll(findCashAccounts(collectivityId, effectiveDate));
        accounts.addAll(findMobileBankingAccounts(collectivityId, effectiveDate));
        accounts.addAll(findBankAccounts(collectivityId, effectiveDate));

        return accounts;
    }

    private List<CashAccount> findCashAccounts(Long collectivityId, LocalDate atDate) {
        String sql = """
                SELECT a.id,
                       COALESCE(SUM(CASE WHEN am.type = 'IN'  AND DATE(am.created_at) <= ? THEN am.amount ELSE 0 END), 0)
                     - COALESCE(SUM(CASE WHEN am.type = 'OUT' AND DATE(am.created_at) <= ? THEN am.amount ELSE 0 END), 0)
                       AS balance
                FROM account a
                JOIN cash_account ca ON ca.id_account = a.id
                LEFT JOIN account_movement am ON am.id_account = a.id
                WHERE a.id_collectivity = ?
                GROUP BY a.id
                """;
        List<CashAccount> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(atDate));
            ps.setDate(2, Date.valueOf(atDate));
            ps.setLong(3, collectivityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CashAccount ca = new CashAccount();
                    ca.setId(String.valueOf(rs.getLong("id")));
                    ca.setAmount(rs.getDouble("balance"));
                    result.add(ca);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching cash accounts: " + e.getMessage(), e);
        }
        return result;
    }

    private List<MobileBankingAccount> findMobileBankingAccounts(Long collectivityId,
                                                                 LocalDate atDate) {
        String sql = """
                SELECT a.id, mma.holder_name, mma.service_name, mma.phone_number,
                       COALESCE(SUM(CASE WHEN am.type = 'IN'  AND DATE(am.created_at) <= ? THEN am.amount ELSE 0 END), 0)
                     - COALESCE(SUM(CASE WHEN am.type = 'OUT' AND DATE(am.created_at) <= ? THEN am.amount ELSE 0 END), 0)
                       AS balance
                FROM account a
                JOIN mobile_money_account mma ON mma.id_account = a.id
                LEFT JOIN account_movement am ON am.id_account = a.id
                WHERE a.id_collectivity = ?
                GROUP BY a.id, mma.holder_name, mma.service_name, mma.phone_number
                """;
        List<MobileBankingAccount> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(atDate));
            ps.setDate(2, Date.valueOf(atDate));
            ps.setLong(3, collectivityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MobileBankingAccount mba = new MobileBankingAccount();
                    mba.setId(String.valueOf(rs.getLong("id")));
                    mba.setHolderName(rs.getString("holder_name"));
                    mba.setMobileNumber(rs.getString("phone_number"));
                    mba.setAmount(rs.getDouble("balance"));
                    String svc = rs.getString("service_name");
                    if (svc != null) mba.setMobileBankingService(MobileBankingService.valueOf(svc));
                    result.add(mba);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching mobile accounts: " + e.getMessage(), e);
        }
        return result;
    }

    private List<BankAccount> findBankAccounts(Long collectivityId, LocalDate atDate) {
        String sql = """
                SELECT a.id, ba.holder_name, ba.bank_name, ba.bank_code,
                       ba.branch_code, ba.account_number, ba.rib_key,
                       COALESCE(SUM(CASE WHEN am.type = 'IN'  AND DATE(am.created_at) <= ? THEN am.amount ELSE 0 END), 0)
                     - COALESCE(SUM(CASE WHEN am.type = 'OUT' AND DATE(am.created_at) <= ? THEN am.amount ELSE 0 END), 0)
                       AS balance
                FROM account a
                JOIN bank_account ba ON ba.id_account = a.id
                LEFT JOIN account_movement am ON am.id_account = a.id
                WHERE a.id_collectivity = ?
                GROUP BY a.id, ba.holder_name, ba.bank_name, ba.bank_code,
                         ba.branch_code, ba.account_number, ba.rib_key
                """;
        List<BankAccount> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(atDate));
            ps.setDate(2, Date.valueOf(atDate));
            ps.setLong(3, collectivityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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
                    result.add(bka);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bank accounts: " + e.getMessage(), e);
        }
        return result;
    }
}