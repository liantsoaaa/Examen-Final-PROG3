package com.hei.openapi_federation.repository;

import com.hei.openapi_federation.entity.Collectivity;
import com.hei.openapi_federation.entity.Member;
import com.hei.openapi_federation.entity.Gender;
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

    public Optional<CollectivityRow> findById(Long id) {
        String sql = """
                SELECT c.id, c.number, c.name, ci.name AS city_name
                FROM collectivity c
                JOIN city ci ON ci.id = c.id_city
                WHERE c.id = ?
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CollectivityRow row = new CollectivityRow();
                    row.id       = rs.getLong("id");
                    row.number   = rs.getString("number");
                    row.name     = rs.getString("name");
                    row.cityName = rs.getString("city_name");
                    return Optional.of(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding collectivity by id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public boolean nameExistsForOther(String name, Long excludeId) {
        String sql = "SELECT 1 FROM collectivity WHERE LOWER(name) = LOWER(?) AND id != ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setLong(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking name uniqueness: " + e.getMessage(), e);
        }
    }

    public boolean numberExistsForOther(String number, Long excludeId) {
        String sql = "SELECT 1 FROM collectivity WHERE LOWER(number) = LOWER(?) AND id != ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number);
            ps.setLong(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking number uniqueness: " + e.getMessage(), e);
        }
    }

    public void updateNumberAndName(Long id, String number, String name) {
        String sql = "UPDATE collectivity SET number = ?, name = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, number);
            ps.setString(2, name);
            ps.setLong(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating collectivity: " + e.getMessage(), e);
        }
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
            for (int i = 0; i < memberIds.size(); i++) ps.setLong(i + 1, memberIds.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting senior members: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<Member> findMembersByCollectivityId(Long collectivityId) {
        String sql = """
                SELECT m.id, m.first_name, m.last_name, m.birth_date,
                       m.address, m.email, m.phone, m.job, m.gender,
                       mc.post_name
                FROM member m
                JOIN member_collectivity mc ON mc.id_member = m.id
                WHERE mc.id_collectivity = ? AND mc.end_date IS NULL
                """;
        List<Member> members = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, collectivityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) members.add(mapMember(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching collectivity members: " + e.getMessage(), e);
        }
        return members;
    }


    public Collectivity save(Collectivity collectivity) {
            return null;

    }



    public static class CollectivityRow {
        public Long   id;
        public String number;
        public String name;
        public String cityName;
    }

    private Member mapMember(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setId(String.valueOf(rs.getLong("id")));
        m.setFirstName(rs.getString("first_name"));
        m.setLastName(rs.getString("last_name"));
        m.setBirthDate(rs.getDate("birth_date").toLocalDate());
        m.setAddress(rs.getString("address"));
        m.setEmail(rs.getString("email"));
        m.setPhoneNumber(rs.getString("phone"));
        m.setProfession(rs.getString("job"));
        String g = rs.getString("gender");
        if (g != null) m.setGender(Gender.valueOf(g));
        String post = rs.getString("post_name");
        if (post != null) m.setOccupation(dbPostToOccupation(post));
        return m;
    }

    private MemberOccupation dbPostToOccupation(String dbPost) {
        return switch (dbPost) {
            case "JUNIOR"         -> MemberOccupation.JUNIOR;
            case "CONFIRMED"      -> MemberOccupation.SENIOR;
            case "SECRETARY"      -> MemberOccupation.SECRETARY;
            case "TREASURER"      -> MemberOccupation.TREASURER;
            case "VICE_PRESIDENT" -> MemberOccupation.VICE_PRESIDENT;
            case "PRESIDENT"      -> MemberOccupation.PRESIDENT;
            default               -> MemberOccupation.JUNIOR;
        };
    }
}