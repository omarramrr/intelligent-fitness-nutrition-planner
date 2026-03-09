package model.nutrition.domain;

import java.time.LocalDate;

/**
 * WEIGHT LOSS PLAN
 * ----------------------------------
 * For users aiming to lose weight safely (target 0.5-1kg weekly loss).
 * Implements a calorie deficit and high-protein macro split.
 */

// This class implements a weight loss diet plan with specific macro ratios and
// calorie adjustments.
// It focuses on creating a sustainable calorie deficit while preserving muscle
// mass through higher protein intake.
// The plan is designed for safe weight loss of 0.5-1kg per week.
public class WeightLossPlan extends DietPlan {
    // Macro nutrient ratios specifically optimized for weight loss.
    // Higher protein helps preserve muscle mass during calorie deficit.
    private static final double PROTEIN_PERCENT = 0.35;
    private static final double CARBS_PERCENT = 0.35;
    private static final double FATS_PERCENT = 0.30;

    // Standard weekly calorie reduction for weight loss plans.
    // This creates the calorie deficit needed for fat loss.
    // Standard weekly calorie reduction for weight loss plans.
    // This creates the calorie deficit needed for fat loss.
    // private static final int WEEKLY_CALORIE_DECREMENT = -175; // REMOVED: Managed
    // by AdaptiveDietManager now

    // Default constructor for testing and demonstration purposes.
    // Creates a weight loss plan with standard calorie targets.
    /**
     * Default (test/demo) constructor.
     */
    public WeightLossPlan() {
        super();
        setPlanDescriptionInternal("Weight Loss Plan");
        setTargetDailyCaloriesInternal(1800.0); // typical deficit
        setMacros(getTargetDailyCaloriesInternal(), PROTEIN_PERCENT, CARBS_PERCENT, FATS_PERCENT);
        setPlanDescriptionInternal("Lose weight via calorie deficit and high protein");
    }

    // Main constructor for creating a weight loss plan with specific parameters.
    // Sets up the plan with custom calorie targets and weight loss optimized
    // macros.
    /**
     * Main constructor for practical plan creation.
     */
    public WeightLossPlan(int dietPlanId, String userId, String planName, LocalDate startDate, LocalDate endDate,
            double targetDailyCalories) {
        super(dietPlanId, userId, planName, startDate, endDate, targetDailyCalories);
        setMacros(targetDailyCalories, PROTEIN_PERCENT, CARBS_PERCENT, FATS_PERCENT);
        setPlanDescriptionInternal(
                "Weight loss plan with controlled deficit and elevated protein to aid muscle retention.");
    }

    // Provides a human-readable description of the weight loss plan's objectives.
    // Explains the approach of steady weight loss with muscle preservation.
    /**
     * Human-friendly description of this plan's goal.
     */
    @Override
    public String describeGoal() {
        return "Lose weight steadily by creating a calorie deficit. " +
                "Aims for sustainable, healthy weight loss (0.5-1kg/week), " +
                "with high protein to preserve muscle.";
    }

    // Returns the weekly calorie adjustment for weight loss.
    // This creates the calorie deficit needed for fat loss while allowing adaptive
    // changes.
    /**
     * Calorie adjustment for adaptive dieting (used by AdaptiveDietManager).
     */
    @Override
    public int goalCalorieAdjustment() {
        return -500; // Base deficit for weight loss
    }

    /**
     * Adaptive adjustment with throttling for rapid weight loss.
     * If losing weight too fast (< -0.5kg/week), reduces the deficit by half
     * to prevent excessive muscle loss and metabolic adaptation.
     */
    public int adaptiveAdjustment(double actualProgress, int baseAdjustment) {
        // Rule: diff < -0.5 -> +100 cal (Losing too fast)
        if (actualProgress < -0.5) {
            return 100;
        }
        // Rule: diff > -0.25 -> -150 cal (Not losing enough/Gaining)
        if (actualProgress > -0.25) {
            return -150;
        }
        return 0;
    }

    // Provides detailed debugging information about the weight loss plan.
    // Shows all key metrics including plan ID, calories, macros, and adjustments.
    /**
     * Status/debug info
     */
    @Override
    public String toString() {
        return "WeightLossPlan{" +
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
