package model.nutrition.domain;

import java.time.LocalDate;

public class MaintainPlan extends DietPlan {
    // Balanced macro ratios suitable for weight maintenance.
    // Standard distribution that supports overall health and stable weight.
    private static final double PROTEIN_PERCENT = 0.30;
    private static final double CARBS_PERCENT = 0.40;
    private static final double FATS_PERCENT = 0.30;

    // Maintenance plans typically don't adjust calories weekly.
    // Small adjustments are made only if weight deviates significantly.
    private static final int WEEKLY_CALORIE_ADJUSTMENT = 0;

    // Default constructor for testing and demonstration purposes.
    // Creates a maintenance plan with standard calorie targets.
    /**
     * Default constructor (demo/test use).
     */
    public MaintainPlan() {
        super();
        setPlanDescriptionInternal("Maintenance Plan");
        setTargetDailyCaloriesInternal(2200.0); // typical TDEE maintainer
        setMacros(getTargetDailyCaloriesInternal(), PROTEIN_PERCENT, CARBS_PERCENT, FATS_PERCENT);
        setPlanDescriptionInternal(
                "Maintain weight with balanced calorie/macros; only small weekly adjustments if needed.");
    }

    // Main constructor for creating a maintenance plan with specific parameters.
    // Sets up the plan with custom calorie targets and balanced macros for weight
    // stability.
    /**
     * Main constructor for practical plan creation.
     */
    public MaintainPlan(int dietPlanId, String userId, String planName, LocalDate startDate, LocalDate endDate,
            double targetDailyCalories) {
        super(dietPlanId, userId, planName, startDate, endDate, targetDailyCalories);
        setMacros(targetDailyCalories, PROTEIN_PERCENT, CARBS_PERCENT, FATS_PERCENT);
        setPlanDescriptionInternal("Balanced maintenance plan. Calories/macro set for long-term stability.");
    }

    // Provides a human-readable description of the maintenance plan's objectives.
    // Explains the approach of stable weight maintenance with minor adjustments.
    /**
     * Description of the goal for maintenance.
     */
    @Override
    public String describeGoal() {
        return "Maintain current weight via stable calories and balanced macros. " +
                "(±0.5kg weekly variation is normal)";
    }

    // Returns the weekly calorie adjustment for maintenance.
    // Maintenance plans typically don't adjust calories, but may make small
    // corrections if needed.
    /**
     * Calorie adjustment for adaptive dieting - always 0 for maintenance plans.
     */
    @Override
    public int goalCalorieAdjustment() {
        return WEEKLY_CALORIE_ADJUSTMENT;
    }

    /**
     * Adaptive adjustment for maintenance with drift correction.
     * Makes small corrections only when weight drifts beyond acceptable range.
     * ±0.3kg/week variation is considered normal and requires no adjustment.
     */
    public int adaptiveAdjustment(double actualProgress, int baseAdjustment) {
        // Rule: diff < -0.5 -> +100 cal (Losing weight during maintenance)
        if (actualProgress < -0.5) {
            return 100;
        }
        // Rule: diff > 0.5 -> -100 cal (Gaining weight during maintenance)
        if (actualProgress > 0.5) {
            return -100;
        }
        return 0; // Within range → no adjustment
    }

    // Provides detailed debugging information about the maintenance plan.
    // Shows all key metrics including plan ID, calories, macros, and adjustments.
    /**
     * Status/debug info
     */
    @Override
    public String toString() {
        return "MaintainPlan{" +
                "id=" + getDietPlanIdInternal() +
                ", user='" + getUserIdInternal() + '\'' +
                ", cal=" + (int) getTargetDailyCaloriesInternal() +
                ", protein=" + String.format("%.1f", getTargetProteinGramsInternal()) +
                ", carbs=" + String.format("%.1f", getTargetCarbsGramsInternal()) +
                ", fats=" + String.format("%.1f", getTargetFatsGramsInternal()) +
                ", adjust/wk=" + goalCalorieAdjustment() +
                ", days=" + getPlanDurationDays() +
                '}';
    }
}
