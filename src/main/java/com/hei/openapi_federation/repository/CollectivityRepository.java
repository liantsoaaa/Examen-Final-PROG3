package com.hei.openapi_federation.repository;

import com.hei.openapi_federation.entity.Member;
import com.hei.openapi_federation.entity.MemberCollectivity;
import com.hei.openapi_federation.entity.MemberOccupation;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class CollectivityRepository {

    private final DataSource dataSource;

    public CollectivityRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Optional<Long> findCityIdByName(String name) {
        String sql = "SELECT id FROM city WHERE LOWER(name) = LOWER(?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding city: " + e.getMessage(), e);
        }
        return Optional.empty();
    }


    public Long findOrCreateCity(String name) {
        return findCityIdByName(name).orElseGet(() -> {
            String sql = "INSERT INTO city (name) VALUES (?) RETURNING id";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong("id");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error creating city: " + e.getMessage(), e);
            }
            throw new RuntimeException("Failed to create city");
        });
    }


    public Long getOrCreateFederationId() {
        String select = "SELECT id FROM federation LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(select);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong("id");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching federation: " + e.getMessage(), e);
        }
        // Create federation with default cotisation percentage
        String insert = "INSERT INTO federation (cotisation_percentage) VALUES (10.00) RETURNING id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong("id");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating federation: " + e.getMessage(), e);
        }
        throw new RuntimeException("Failed to get or create federation");
    }


    public Long insertCollectivity(String number, String name, String speciality,
                                   Long idFederation, Long idCity) {
        String sql = """
                INSERT INTO collectivity
                    (number, name, speciality, creation_datetime, status, authorization_date, id_federation, id_city)
                VALUES (?, ?, ?, ?, 'APPROVED'::collectivity_status, ?, ?, ?)
                RETURNING id
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();
            ps.setString(1, number);
            ps.setString(2, name);
            ps.setString(3, speciality);
            ps.setTimestamp(4, Timestamp.valueOf(now));
            ps.setTimestamp(5, Timestamp.valueOf(now));
            ps.setLong(6, idFederation);
            ps.setLong(7, idCity);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting collectivity: " + e.getMessage(), e);
        }
        throw new RuntimeException("Failed to insert collectivity");
    }


    public void insertMemberCollectivity(Long idMember, Long idCollectivity, String postName) {
        String sql = """
                INSERT INTO member_collectivity (id_member, id_collectivity, post_name, start_date)
                VALUES (?, ?, ?::collectivity_post_name, ?)
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMember);
            ps.setLong(2, idCollectivity);
            ps.setString(3, postName);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting member_collectivity: " + e.getMessage(), e);
        }
    }


    public int countMembersWithSeniorityInFederation(List<Long> memberIds, int months) {
        if (memberIds == null || memberIds.isEmpty()) return 0;
        String placeholders = memberIds.stream().map(id -> "?").reduce((a, b) -> a + "," + b).orElse("?");
        String sql = """
                SELECT COUNT(DISTINCT mc.id_member) FROM member_collectivity mc
                WHERE mc.id_member IN (%s)
                  AND mc.end_date IS NULL
                  AND mc.start_date <= NOW() - INTERVAL '%d months'
                """.formatted(placeholders, months);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < memberIds.size(); i++) {
                ps.setLong(i + 1, memberIds.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting senior members: " + e.getMessage(), e);
        }
        return 0;
    }


    public String generateCollectivityNumber() {
        String sql = "SELECT COUNT(*) FROM collectivity";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                long count = rs.getLong(1);
                return "COL-%04d".formatted(count + 1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating collectivity number: " + e.getMessage(), e);
        }
        return "COL-0001";
    }
}