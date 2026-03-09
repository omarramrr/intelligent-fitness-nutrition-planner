package model.trackbodyweight.repository;

import model.trackbodyweight.domain.BodyWeightEntry;
import model.trackbodyweight.domain.WeeklyAverageEntry;

import config.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class BodyWeightRepository {

    public boolean logWeight(String profileId, double weightKg, LocalDate date) {
        // Use MERGE to Insert or Update (Upsert) based on profile_id and log_date
        String sql = "MERGE INTO daily_weight_logs AS target " +
                     "USING (VALUES (?, ?, ?)) AS source (profile_id, log_date, weight_kg) " +
                     "ON target.profile_id = source.profile_id AND target.log_date = source.log_date " +
                     "WHEN MATCHED THEN " +
                     "    UPDATE SET weight_kg = source.weight_kg " +
                     "WHEN NOT MATCHED THEN " +
                     "    INSERT (profile_id, log_date, weight_kg) VALUES (source.profile_id, source.log_date, source.weight_kg);";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Ensure auto-commit is on
            if (!conn.getAutoCommit()) {
                conn.setAutoCommit(true);
            }

            stmt.setString(1, profileId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            stmt.setDouble(3, weightKg);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<BodyWeightEntry> getWeightHistory(String profileId) {
        List<BodyWeightEntry> history = new ArrayList<>();
        String sql = "SELECT log_id, log_date, weight_kg FROM daily_weight_logs WHERE profile_id = ? ORDER BY log_date ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profileId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String logId = rs.getString("log_id");
                    LocalDate date = rs.getDate("log_date").toLocalDate();
                    double weight = rs.getDouble("weight_kg");
                    history.add(new BodyWeightEntry(logId, profileId, date, weight));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }
    public boolean saveWeeklyAverage(String profileId, int weekNumber, double averageWeight, LocalDate date) {
        // Upsert for weekly logs using MERGE
        String sql = "MERGE INTO weekly_weight_averages AS target " +
              "USING (VALUES (?, ?, ?, ?)) AS source (profile_id, week_number, average_weight_kg, recorded_at) " +
              "ON target.profile_id = source.profile_id AND target.week_number = source.week_number " +
              "WHEN MATCHED THEN " +
              "    UPDATE SET average_weight_kg = source.average_weight_kg, recorded_at = source.recorded_at " +
              "WHEN NOT MATCHED THEN " +
              "    INSERT (profile_id, week_number, average_weight_kg, recorded_at) VALUES (source.profile_id, source.week_number, source.average_weight_kg, source.recorded_at);";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
             // Ensure auto-commit is on
            if (!conn.getAutoCommit()) {
                conn.setAutoCommit(true);
            }

            stmt.setString(1, profileId);
            stmt.setInt(2, weekNumber);
            stmt.setDouble(3, averageWeight);
            stmt.setDate(4, java.sql.Date.valueOf(date));

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database Error (Weekly Avg): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public List<WeeklyAverageEntry> getWeeklyAverageHistory(String profileId) {
        List<WeeklyAverageEntry> history = new ArrayList<>();
        String sql = "SELECT week_number, average_weight_kg, recorded_at FROM weekly_weight_averages WHERE profile_id = ? ORDER BY week_number ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profileId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int weekNum = rs.getInt("week_number");
                    double avg = rs.getDouble("average_weight_kg");
                    Timestamp ts = rs.getTimestamp("recorded_at");
                    LocalDateTime recordedAt = (ts != null) ? ts.toLocalDateTime() : null;
                    
                    history.add(new WeeklyAverageEntry(weekNum, avg, recordedAt));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("DEBUG: Retrieved " + history.size() + " weekly entries for " + profileId);
        return history;
    }
}
