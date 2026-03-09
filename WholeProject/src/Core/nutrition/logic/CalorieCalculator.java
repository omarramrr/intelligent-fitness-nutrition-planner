package model.nutrition.logic;

import model.user.domain.Profile;
import model.user.enums.Gender;

import model.nutrition.domain.IDietPlan;

/**
 * CALORIE CALCULATOR
 * -----------------------------------
 * Provides nutrition calculation utilities for all diet plans.
 * Orchestrates BMR and TDEE computation, adaptive adjustments, and plan factory
 * methods.
 * Main entry-points: generateSmartCalories (for users+plans), generateDietPlan
 * (factories).
 */

// This class serves as the central calculator for nutrition-related
// computations.
// It handles all calorie calculations, from basic metabolic rate to complete
// diet plans.
// The class is designed to be used statically, with no instance state required.
public class CalorieCalculator {

    // This section contains the core metabolic calculation methods.
    // These calculate fundamental energy expenditure values used by all diet plans.

    // This method calculates Basal Metabolic Rate using the Harris-Benedict
    // formula.
    // BMR represents the calories needed to maintain basic bodily functions at
    // rest.
    // It requires accurate profile data including age, gender, height, and weight.
    /**
     * Calculate Basal Metabolic Rate (BMR) using revised Harris-Benedict formula.
     *
     * @param profile User profile (must include age, gender, height, weight)
     * @return BMR (kcal per day)
     */
    public static double calculateBMR(Profile profile) {
        if (profile == null || profile.getAge() <= 0 ||
                profile.getHeightInCm() <= 0 || profile.getWeightInKg() <= 0) {
            return 0;
        }

        final double weight = profile.getWeightInKg();
        final double height = profile.getHeightInCm();
        final int age = profile.getAge();
        final Gender gender = profile.getGender();

        if (gender == Gender.MALE) {
            return 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            return 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }
    }

    // This method applies goal-specific calorie adjustments to TDEE.
    // Different diet plans (weight loss, muscle gain, maintenance) require
    // different calorie targets.
    // The adjustment is determined by the specific plan type using polymorphism.
    /**
     * Adjust TDEE by plan's weekly goal using polymorphism (+, -, or 0)
     */
    public static int applyGoalAdjustment(double tdee, IDietPlan plan) {
        if (plan == null || tdee <= 0)
            return (int) Math.round(tdee);
        int adjustment = plan.goalCalorieAdjustment();
        double result = tdee + adjustment;
        if (result < 1200)
            result = 1200;
        return (int) Math.round(result);
    }

    // This section provides the main public methods for calculating calorie needs.
    // These are the primary entry points used by other parts of the application.

    // This method calculates daily calorie needs using a default moderate activity
    // level.
    // It's a convenience method that assumes typical daily activity for most users.
    /**
     * Compute a user's smart daily calorie need, using their Fitness Level
     * multiplier.
     */
    public static int generateSmartCalories(Profile profile, IDietPlan plan) {
        if (profile == null || plan == null)
            return 0;

        double bmr = calculateBMR(profile);
        if (bmr <= 0)
            return 0;

        // Use FitnessLevel multiplier directly
        double multiplier = profile.getFitnessLevel().getMultiplier();
        double tdee = bmr * multiplier;

        if (tdee <= 0)
            return 0;

        return applyGoalAdjustment(tdee, plan);
    }

}
