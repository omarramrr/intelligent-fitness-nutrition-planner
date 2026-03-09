package model.nutrition.domain;

import java.time.LocalDate;

/**
 * MUSCLE GAIN PLAN
 * ----------------------------------
 * For users aiming to build muscle via calorie surplus and high protein.
 * Focuses on controlled surplus and optimal macros for muscle growth.
 */

// This class implements a muscle gain diet plan with high protein and calorie
// surplus.
// It emphasizes building lean muscle mass through elevated calorie intake and
// optimal macronutrient ratios.
// The plan targets steady muscle growth of 0.25-0.5kg per week.
public class MuscleGainPlan extends DietPlan {
    // Macro ratios optimized for muscle protein synthesis and growth.
    // Very high protein supports muscle repair and growth.
    private static final double PROTEIN_PERCENT = 0.40;
    private static final double CARBS_PERCENT = 0.40;
    private static final double FATS_PERCENT = 0.20;

    // Standard weekly calorie increase for muscle gain plans.
    // This creates the calorie surplus needed for muscle building.
    // Standard weekly calorie increase for muscle gain plans.
    // This creates the calorie surplus needed for muscle building.
    // private static final int WEEKLY_CALORIE_INCREMENT = 150; // REMOVED: Managed
    // by AdaptiveDietManager now

    // Default constructor for testing and demonstration purposes.
    // Creates a muscle gain plan with standard calorie targets.
    /**
     * Default constructor (for demo/test).
     */
    public MuscleGainPlan() {
        super();
        setPlanDescriptionInternal("Muscle Gain Plan");
        setTargetDailyCaloriesInternal(2500.0); // typical surplus
        setMacros(getTargetDailyCaloriesInternal(), PROTEIN_PERCENT, CARBS_PERCENT, FATS_PERCENT);
        setPlanDescriptionInternal("Build muscle via moderate calorie surplus and high protein");
    }

    // Main constructor for creating a muscle gain plan with specific parameters.
    // Sets up the plan with custom calorie targets and muscle-building optimized
    // macros.
    /**
     * Main constructor for practical plan creation.
     */
    public MuscleGainPlan(int dietPlanId, String userId, String planName, LocalDate startDate, LocalDate endDate,
            double targetDailyCalories) {
        super(dietPlanId, userId, planName, startDate, endDate, targetDailyCalories);
        setMacros(targetDailyCalories, PROTEIN_PERCENT, CARBS_PERCENT, FATS_PERCENT);
        setPlanDescriptionInternal(
                "Muscle gain plan with calorie surplus; prioritizes protein for lean mass accumulation.");
    }

    // Provides a human-readable description of the muscle gain plan's objectives.
    // Explains the approach of controlled surplus for steady muscle growth.
    /**
     * Human-friendly description of this plan's goal.
     */
    @Override
    public String describeGoal() {
        return "Increase muscle mass with a controlled surplus. " +
                "Aim for 0.25-0.5kg/week gain through high protein and adequate energy for training.";
    }

    // Returns the weekly calorie adjustment for muscle gain.
    // This creates the calorie surplus needed for muscle building while allowing
    // adaptive changes.
    /**
     * Calorie adjustment for adaptive dieting (used by AdaptiveDietManager).
     */
    @Override
    public int goalCalorieAdjustment() {
        return 300; // Base surplus for muscle gain
    }

    /**
     * Adaptive adjustment with moderation for rapid weight gain.
     * If gaining weight too fast (> 1.0kg/week), reduces the surplus by half
     * to minimize fat accumulation and promote lean muscle growth.
     */
    public int adaptiveAdjustment(double actualProgress, int baseAdjustment) {
        // Rule: diff > 0.5 -> -150 cal (Gaining too fast)
        if (actualProgress > 0.5) {
            return -150;
        }
        // Rule: diff < 0.25 -> +100 cal (Not gaining enough)
        if (actualProgress < 0.25) {
            return 100;
        }
        return 0;
    }

    // Provides detailed debugging information about the muscle gain plan.
    // Shows all key metrics including plan ID, calories, macros, and adjustments.
    /**
     * Status/debug info
     */
    @Override
    public String toString() {
        return "MuscleGainPlan{" +
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
