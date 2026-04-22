package com.hei.openapi_federation.repository;

import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;


@Repository
public class SponsorshipRepository {

    private final DataSource dataSource;

    public SponsorshipRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void insert(Long idCandidate, Long idSponsor, Long idCollectivity, String relationship) {
        String sql = """
                INSERT INTO sponsorship (id_candidate, id_sponsor, id_collectivity, relationship, created_at)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idCandidate);
            ps.setLong(2, idSponsor);
            ps.setLong(3, idCollectivity);
            ps.setString(4, relationship);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting sponsorship: " + e.getMessage(), e);
        }
    }


    public boolean existsByCandidateAndSponsor(Long idCandidate, Long idSponsor) {
        String sql = """
                SELECT 1 FROM sponsorship
                WHERE id_candidate = ? AND id_sponsor = ?
                LIMIT 1
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idCandidate);
            ps.setLong(2, idSponsor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking sponsorship existence: " + e.getMessage(), e);
        }
    }
}