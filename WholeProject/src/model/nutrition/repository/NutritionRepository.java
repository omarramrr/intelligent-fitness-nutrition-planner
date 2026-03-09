package model.nutrition.repository;

import config.DatabaseConnection;
import model.nutrition.domain.*;
import java.sql.*;
import java.time.LocalDate;

public class NutritionRepository {

    public boolean savePlan(IDietPlan plan) {
        String sql = "INSERT INTO diet_plans (profile_id, plan_name, target_calories, protein_grams, carbs_grams, fats_grams, start_date, plan_type, end_date, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (!conn.getAutoCommit()) {
                conn.setAutoCommit(true);
            }

            stmt.setString(1, plan.getUserId());
            stmt.setString(2, plan.getPlanName());
            stmt.setInt(3, (int) plan.getTargetDailyCalories());
            stmt.setInt(4, (int) plan.getTargetProteinGrams());
            stmt.setInt(5, (int) plan.getTargetCarbsGrams());
            stmt.setInt(6, (int) plan.getTargetFatsGrams());
            stmt.setDate(7, java.sql.Date.valueOf(plan.getStartDate()));
            
            // Determine plan type string
            String planType = "MAINTAIN";
            if (plan instanceof WeightLossPlan) planType = "WEIGHT_LOSS";
            else if (plan instanceof MuscleGainPlan) planType = "MUSCLE_GAIN";
            
            stmt.setString(8, planType);
            stmt.setDate(9, java.sql.Date.valueOf(plan.getEndDate()));
            stmt.setBoolean(10, plan.isActive());
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public IDietPlan getLatestPlanByProfileId(String profileId) {
        String sql = "SELECT * FROM diet_plans WHERE profile_id = ? AND is_active = 1 ORDER BY plan_id DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, profileId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String planType = rs.getString("plan_type");
                int planId = rs.getInt("plan_id");
                
                String pId = rs.getString("profile_id");
                String pName = rs.getString("plan_name");
                int cals = rs.getInt("target_calories");
                LocalDate start = rs.getDate("start_date").toLocalDate();
                LocalDate end = rs.getDate("end_date").toLocalDate();
                
                // Factory logic
                if ("WEIGHT_LOSS".equalsIgnoreCase(planType)) {
                    return new WeightLossPlan(planId, pId, pName, start, end, cals);
                } else if ("MUSCLE_GAIN".equalsIgnoreCase(planType)) {
                    return new MuscleGainPlan(planId, pId, pName, start, end, cals);
                } else {
                    return new MaintainPlan(planId, pId, pName, start, end, cals);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
