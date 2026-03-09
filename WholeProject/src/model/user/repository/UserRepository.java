package model.user.repository;

import config.DatabaseConnection;
import model.user.domain.Profile;
import model.user.domain.User;
import model.user.enums.FitnessLevel;
import model.user.enums.Gender;
import model.user.enums.GoalType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRepository {

    // Saves a Profile and its User to the database in a transaction
    public boolean saveUser(Profile profile) {
        Connection conn = null;
        PreparedStatement userStmt = null;
        PreparedStatement profileStmt = null;

        String insertUserSql = "INSERT INTO users (user_id, username, email, password_hash) VALUES (?, ?, ?, ?)";
        String insertProfileSql = "INSERT INTO profiles (profile_id, user_id, height_cm, current_weight_kg, start_weight_kg, gender, goal, fitness_level, date_of_birth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert User
            userStmt = conn.prepareStatement(insertUserSql);
            userStmt.setObject(1, profile.getUser().getUserId());
            userStmt.setString(2, profile.getUser().getUsername());
            userStmt.setString(3, profile.getUser().getEmail());
            userStmt.setString(4, profile.getUser().getPassword());
            userStmt.executeUpdate();

            // 2. Insert Profile
            profileStmt = conn.prepareStatement(insertProfileSql);
            profileStmt.setString(1, profile.getProfileId());
            profileStmt.setObject(2, profile.getUser().getUserId());
            profileStmt.setDouble(3, profile.getHeightInCm());
            profileStmt.setDouble(4, profile.getWeightInKg());
            // Assuming startWeight is same as current weight for new profiles if not
            // exposed
            // But actually Profile logic sets startWeight = weightInKg in constructor.
            // Using getWeightInKg for initial start weight.
            profileStmt.setDouble(5, profile.getWeightInKg());

            profileStmt.setString(6, mapGenderToDb(profile.getGender()));
            profileStmt.setString(7, mapGoalToDb(profile.getGoal()));
            profileStmt.setString(8, mapFitnessToDb(profile.getFitnessLevel()));
            profileStmt.setObject(9, java.sql.Date.valueOf(profile.getDateOfBirth()));

            profileStmt.executeUpdate();

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Error saving user: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (userStmt != null)
                    userStmt.close();
                if (profileStmt != null)
                    profileStmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Profile getUserByEmail(String email) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT u.user_id, u.username, u.email, u.password_hash, " +
                "p.profile_id, p.height_cm, p.current_weight_kg, p.start_weight_kg, " +
                "p.gender, p.goal, p.fitness_level, p.date_of_birth, p.last_adjusted_week " +
                "FROM users u " +
                "JOIN profiles p ON u.user_id = p.user_id " +
                "WHERE u.email = ? " +
                "ORDER BY u.created_at DESC";

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // reconstruct User
                UUID userId = UUID.fromString(rs.getString("user_id"));
                String username = rs.getString("username");
                String password = rs.getString("password_hash");

                User user = new User(username, email);
                user.setUserId(userId);
                user.setPassword(password);

                // Reconstruct Profile
                String profileId = rs.getString("profile_id");
                double height = rs.getDouble("height_cm");
                double weight = rs.getDouble("current_weight_kg");

                String genderStr = rs.getString("gender");
                String goalStr = rs.getString("goal");
                String fitnessStr = rs.getString("fitness_level");
                java.sql.Date dobDate = rs.getDate("date_of_birth");
                int lastAdjustedWeek = rs.getInt("last_adjusted_week");

                Gender gender = Gender.valueOf(genderStr);
                GoalType goal = mapGoal(goalStr);
                FitnessLevel fitness = FitnessLevel.valueOf(fitnessStr);

                Profile profile = new Profile(profileId, user, height, weight, goal, gender, dobDate.toLocalDate(),
                        fitness);
                profile.setLastAdjustedWeekIndex(lastAdjustedWeek);

                return profile;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean updateProfile(Profile profile) {
        String sql = "UPDATE profiles SET height_cm = ?, current_weight_kg = ?, goal = ?, fitness_level = ?, last_adjusted_week = ? WHERE profile_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, profile.getHeightInCm());
            stmt.setDouble(2, profile.getWeightInKg());
            stmt.setString(3, mapGoalToDb(profile.getGoal()));
            stmt.setString(4, mapFitnessToDb(profile.getFitnessLevel()));
            stmt.setInt(5, profile.getLastAdjustedWeekIndex());
            stmt.setString(6, profile.getProfileId());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(UUID userId, String newPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setObject(2, userId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private GoalType mapGoal(String dbGoal) {
        if (dbGoal == null)
            return GoalType.MAINTENANCE;
        switch (dbGoal) {
            case "LOSE_WEIGHT":
                return GoalType.LOSE_WEIGHT;
            case "GAIN_MUSCLE":
                return GoalType.GAIN_WEIGHT;
            case "MAINTAIN":
                return GoalType.MAINTENANCE;
            default:
                return GoalType.MAINTENANCE;
        }
    }

    private String mapGoalToDb(GoalType goal) {
        if (goal == null)
            return "MAINTAIN";
        switch (goal) {
            case LOSE_WEIGHT:
                return "LOSE_WEIGHT";
            case GAIN_WEIGHT:
                return "GAIN_MUSCLE";
            case MAINTENANCE:
                return "MAINTAIN";
            default:
                return "MAINTAIN";
        }
    }

    private String mapFitnessToDb(FitnessLevel level) {
        if (level == null)
            return "BEGINNER";
        return level.name();
    }

    private String mapGenderToDb(Gender gender) {
        if (gender == null)
            return "MALE";
        return gender.name();
    }
}
