package model.nutrition.domain;

import java.time.LocalDate;

/**
 * Interface defining the contract for all diet plans.
 * Enables dependency injection, mocking for tests, and decouples
 * implementations.
 */
public interface IDietPlan {

    // ==================== GOAL AND ADJUSTMENT METHODS ====================

    /**
     * Brief description of the goal of this plan.
     * 
     * @return Human-readable goal description
     */
    String describeGoal();

    /**
     * Weekly calorie adjustment for adaptive dieting.
     * 
     * @return Calorie adjustment (+, -, or 0)
     */
    int goalCalorieAdjustment();

    /**
     * Calculates adaptive calorie adjustment based on actual progress.
     * 
     * @param actualProgress Actual weekly weight change in kg
     * @param baseAdjustment Base calorie adjustment from goalCalorieAdjustment()
     * @return Final adjusted calorie amount
     */
    int adaptiveAdjustment(double actualProgress, int baseAdjustment);

    // ==================== GETTERS FOR PLAN DATA ====================

    int getDietPlanId();

    String getUserId();

    String getPlanName();

    LocalDate getStartDate();

    LocalDate getEndDate();

    double getTargetDailyCalories();

    double getTargetProteinGrams();

    double getTargetCarbsGrams();

    double getTargetFatsGrams();

    String getPlanDescription();

    // ==================== STATUS METHODS ====================

    /**
     * Returns if the plan is active AND within the date range.
     * 
     * @return true if plan is valid
     */
    boolean isValid();

    boolean isActive();

    /**
     * Plan has already started?
     * 
     * @return true if current date is on or after start date
     */
    boolean hasStarted();

    /**
     * Plan has ended?
     * 
     * @return true if current date is after end date
     */
    boolean hasEnded();

    /**
     * Get plan duration in days.
     * 
     * @return Number of days between start and end date
     */
    long getPlanDurationDays();

    // ==================== DISPLAY METHOD ====================

    /**
     * Generates a detailed formatted summary of this diet plan.
     * 
     * @return Formatted string summary of the plan
     */
    String getPlanSummary();
}
