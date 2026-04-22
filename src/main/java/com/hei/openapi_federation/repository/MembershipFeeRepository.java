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
public class MembershipFeeRepository {

    private final DataSource dataSource;

    public MembershipFeeRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<MembershipFee> findByCollectivityId(Long collectivityId) {
        String sql = """
                SELECT id, label, frequency, amount, year, is_active
                FROM cotisation_plan
                WHERE id_collectivity = ?
                ORDER BY id
                """;
        List<MembershipFee> fees = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, collectivityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) fees.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching membership fees: " + e.getMessage(), e);
        }
        return fees;
    }

    public Optional<MembershipFee> findById(Long id) {
        String sql = "SELECT id, label, frequency, amount, year, is_active FROM cotisation_plan WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching membership fee: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Long insert(Long collectivityId, String label, String frequency,
                       Double amount, LocalDate eligibleFrom) {
        String sql = """
                INSERT INTO cotisation_plan (id_collectivity, label, frequency, amount, year, is_active)
                VALUES (?, ?, ?::cotisation_frequency, ?, ?, true)
                RETURNING id
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, collectivityId);
            ps.setString(2, label);
            ps.setString(3, frequency);
            ps.setDouble(4, amount);
            ps.setInt(5, eligibleFrom != null ? eligibleFrom.getYear() : LocalDate.now().getYear());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting membership fee: " + e.getMessage(), e);
        }
        throw new RuntimeException("Failed to insert membership fee");
    }

    private MembershipFee mapRow(ResultSet rs) throws SQLException {
        MembershipFee fee = new MembershipFee();
        fee.setId(Integer.parseInt(String.valueOf(rs.getLong("id"))));
        fee.setLabel(rs.getString("label"));
        fee.setAmount(rs.getDouble("amount"));
        fee.setStatus(rs.getBoolean("is_active") ? ActivityStatus.ACTIVE : ActivityStatus.INACTIVE);

        String freq = rs.getString("frequency");
        if (freq != null) {
            fee.setFrequency(switch (freq) {
                case "MONTHLY"  -> Frequency.MONTHLY;
                case "ANNUAL"   -> Frequency.ANNUALLY;
                case "PUNCTUAL" -> Frequency.PUNCTUALLY;
                default         -> Frequency.PUNCTUALLY;
            });
        }

        int year = rs.getInt("year");
        if (year > 0) fee.setEligibleFrom(String.valueOf(LocalDate.of(year, 1, 1)));

        return fee;
    }
}