package model.nutrition.logic;

import model.user.domain.Profile;
import model.user.enums.GoalType;
import model.nutrition.domain.IDietPlan;
import model.nutrition.domain.DietPlan;
import model.nutrition.domain.WeightLossPlan;
import model.nutrition.domain.MuscleGainPlan;
import model.nutrition.domain.MaintainPlan;

import java.time.LocalDate;

/**
 * NutritionalPlanGenerator
 * 
 * This class is responsible for generating appropriate nutritional/diet plans
 * based on user profiles and their fitness goals. It analyzes the user's
 * profile data (age, weight, height, goal, fitness level) and creates a
 * customized diet plan with appropriate calorie targets and macro
 * distributions.
 */
public class NutritionalPlanGenerator {

    /**
     * Generates a diet plan based on the user's profile and goals.
     * This is the main entry point for creating a nutritional plan.
     * 
     * @param profile The user's profile containing personal data and goals
     * @return A DietPlan object (WeightLossPlan, MuscleGainPlan, or MaintainPlan)
     */
    public static IDietPlan generatePlan(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }

        // Calculate target calories based on profile
        int targetCalories = CalorieCalculator.generateSmartCalories(
                profile,
                createTemporaryPlan(profile.getGoal()));

        // Generate plan based on goal type
        return createPlanByGoal(profile, targetCalories);
    }

    /**
     * Generates a diet plan with a specific duration.
     * 
     * @param profile       The user's profile
     * @param durationWeeks The duration of the plan in weeks
     * @return A DietPlan object with start and end dates
     */
    public static IDietPlan generatePlan(Profile profile, int durationWeeks) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
        if (durationWeeks <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        // Calculate dates
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusWeeks(durationWeeks);

        // Calculate target calories
        int targetCalories = CalorieCalculator.generateSmartCalories(
                profile,
                createTemporaryPlan(profile.getGoal()));

        // Generate plan with dates
        return createPlanByGoal(profile, targetCalories, startDate, endDate);
    }

    /**
     * Generates a custom diet plan with specific calorie target.
     * Useful when you want to override the calculated calories.
     * 
     * @param profile        The user's profile
     * @param customCalories The custom daily calorie target
     * @return A DietPlan object with custom calories
     */
    public static IDietPlan generateCustomPlan(Profile profile, int customCalories) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
        if (customCalories < 1200 || customCalories > 5000) {
            throw new IllegalArgumentException("Calories must be between 1200-5000");
        }

        return createPlanByGoal(profile, customCalories);
    }

    /**
     * Creates a diet plan based on the user's goal type.
     * This is a helper method that instantiates the appropriate plan subclass.
     */
    private static DietPlan createPlanByGoal(Profile profile, int targetCalories) {
        GoalType goal = profile.getGoal();
        String planName = generatePlanName(goal, profile.getUsername());

        switch (goal) {
            case LOSE_WEIGHT:
                return new WeightLossPlan(
                        generatePlanId(),
                        profile.getProfileId(),
                        planName,
                        LocalDate.now(),
                        LocalDate.now().plusWeeks(12), // Default 12 weeks
                        targetCalories);

            case GAIN_WEIGHT:
                return new MuscleGainPlan(
                        generatePlanId(),
                        profile.getProfileId(),
                        planName,
                        LocalDate.now(),
                        LocalDate.now().plusWeeks(16), // Default 16 weeks for bulking
                        targetCalories);

            case MAINTENANCE:
                return new MaintainPlan(
                        generatePlanId(),
                        profile.getProfileId(),
                        planName,
                        LocalDate.now(),
                        LocalDate.now().plusWeeks(8), // Default 8 weeks
                        targetCalories);

            default:
                // Default to maintenance plan
                return new MaintainPlan(
                        generatePlanId(),
                        profile.getProfileId(),
                        "General Fitness Plan",
                        LocalDate.now(),
                        LocalDate.now().plusWeeks(8),
                        targetCalories);
        }
    }

    /**
     * Creates a diet plan with specific start and end dates.
     */
    private static DietPlan createPlanByGoal(Profile profile, int targetCalories,
            LocalDate startDate, LocalDate endDate) {
        GoalType goal = profile.getGoal();
        String planName = generatePlanName(goal, profile.getUsername());

        switch (goal) {
            case LOSE_WEIGHT:
                return new WeightLossPlan(
                        generatePlanId(),
                        profile.getProfileId(),
                        planName,
                        startDate,
                        endDate,
                        targetCalories);

            case GAIN_WEIGHT:
                return new MuscleGainPlan(
                        generatePlanId(),
                        profile.getProfileId(),
                        planName,
                        startDate,
                        endDate,
                        targetCalories);

            case MAINTENANCE:
            default:
                return new MaintainPlan(
                        generatePlanId(),
                        profile.getProfileId(),
                        planName,
                        startDate,
                        endDate,
                        targetCalories);
        }
    }

    /**
     * Creates a temporary plan object for calorie calculation purposes.
     * This is used internally to get the right calorie adjustment factors.
     */
    private static DietPlan createTemporaryPlan(GoalType goal) {
        switch (goal) {
            case LOSE_WEIGHT:
                return new WeightLossPlan();
            case GAIN_WEIGHT:
                return new MuscleGainPlan();
            default:
                return new MaintainPlan();
        }
    }

    /**
     * Generates a descriptive plan name based on goal and username.
     */
    private static String generatePlanName(GoalType goal, String username) {
        String baseName = username + "'s ";

        switch (goal) {
            case LOSE_WEIGHT:
                return baseName + "Weight Loss Plan";
            case GAIN_WEIGHT:
                return baseName + "Muscle Building Plan";
            case MAINTENANCE:
                return baseName + "Maintenance Plan";
            default:
                return baseName + "Nutrition Plan";
        }
    }

    /**
     * Generates a unique plan ID.
     * In a real system, this would query a database for the next ID.
     */
    private static int generatePlanId() {
        return (int) (System.currentTimeMillis() % 1000000);
    }
}
