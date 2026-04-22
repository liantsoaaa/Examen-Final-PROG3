package com.hei.openapi_federation.repository;

import com.hei.openapi_federation.entity.Member;
import com.hei.openapi_federation.entity.Gender;
import com.hei.openapi_federation.entity.MemberOccupation;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class MemberRepository {

    private final DataSource dataSource;

    public MemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Optional<Member> findById(String id) {
        String sql = """
                SELECT m.id, m.first_name, m.last_name, m.birth_date,
                       m.address, m.email, m.phone, m.job, m.gender,
                       mc.post_name
                FROM member m
                LEFT JOIN member_collectivity mc
                    ON mc.id_member = m.id AND mc.end_date IS NULL
                WHERE m.id = ?
                LIMIT 1
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, Long.parseLong(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding member by id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }


    public List<Member> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        String placeholders = ids.stream().map(id -> "?").reduce((a, b) -> a + "," + b).orElse("?");
        String sql = """
                SELECT m.id, m.first_name, m.last_name, m.birth_date,
                       m.address, m.email, m.phone, m.job, m.gender,
                       mc.post_name
                FROM member m
                LEFT JOIN member_collectivity mc
                    ON mc.id_member = m.id AND mc.end_date IS NULL
                WHERE m.id IN (%s)
                """.formatted(placeholders);
        List<Member> members = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) {
                ps.setLong(i + 1, Long.parseLong(ids.get(i)));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) members.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding members by ids: " + e.getMessage(), e);
        }
        return members;
    }


    public boolean collectivityExists(String collectivityId) {
        String sql = "SELECT 1 FROM collectivity WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, Long.parseLong(collectivityId));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking collectivity existence: " + e.getMessage(), e);
        }
    }


    public boolean isConfirmedMember(String memberId) {
        String sql = """
                SELECT 1 FROM member_collectivity
                WHERE id_member = ?
                  AND post_name = 'CONFIRMED'::collectivity_post_name
                  AND end_date IS NULL
                LIMIT 1
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, Long.parseLong(memberId));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking confirmed status: " + e.getMessage(), e);
        }
    }


    public Optional<Long> getCurrentCollectivityId(String memberId) {
        String sql = """
                SELECT id_collectivity FROM member_collectivity
                WHERE id_member = ?
                  AND end_date IS NULL
                LIMIT 1
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, Long.parseLong(memberId));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getLong("id_collectivity"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting current collectivity: " + e.getMessage(), e);
        }
        return Optional.empty();
    }


    public Long insert(String firstName, String lastName, LocalDate birthDate,
                       String gender, String address, String phone,
                       String email, String job) {
        String sql = """
                INSERT INTO member
                    (first_name, last_name, birth_date, enrolment_date, address, email, phone, job, gender)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::gender)
                RETURNING id
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setDate(3, Date.valueOf(birthDate));
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(5, address);
            ps.setString(6, email);
            ps.setString(7, phone);
            ps.setString(8, job);
            ps.setString(9, gender);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting member: " + e.getMessage(), e);
        }
        throw new RuntimeException("Failed to insert member");
    }


    public void insertAsJunior(Long memberId, Long collectivityId) {
        String sql = """
                INSERT INTO member_collectivity (id_member, id_collectivity, post_name, start_date)
                VALUES (?, ?, 'JUNIOR'::collectivity_post_name, ?)
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, memberId);
            ps.setLong(2, collectivityId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting member as junior: " + e.getMessage(), e);
        }
    }


    private Member mapRow(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setId(String.valueOf(rs.getLong("id")));
        m.setFirstName(rs.getString("first_name"));
        m.setLastName(rs.getString("last_name"));
        m.setBirthDate(rs.getDate("birth_date").toLocalDate());
        m.setAddress(rs.getString("address"));
        m.setEmail(rs.getString("email"));
        m.setPhoneNumber(Integer.parseInt(rs.getString("phone")));
        m.setProfession(rs.getString("job"));

        String genderStr = rs.getString("gender");
        if (genderStr != null) m.setGender(Gender.valueOf(genderStr));

        String postName = rs.getString("post_name");
        if (postName != null) m.setOccupation(dbPostToOccupation(postName));

        return m;
    }


    private MemberOccupation dbPostToOccupation(String dbPost) {
        return switch (dbPost) {
            case "JUNIOR"          -> MemberOccupation.JUNIOR;
            case "CONFIRMED"       -> MemberOccupation.SENIOR;
            case "SECRETARY"       -> MemberOccupation.SECRETARY;
            case "TREASURER"       -> MemberOccupation.TREASURER;
            case "DEPUTY_PRESIDENT" -> MemberOccupation.VICE_PRESIDENT;
            case "PRESIDENT"       -> MemberOccupation.PRESIDENT;
            default                -> MemberOccupation.JUNIOR;
        };
    }

    public void updateCollectivityId(Object id, String collectivityId) {

    }

    public Member save(Member member) {
            return null;
    }
}