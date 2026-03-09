package model.nutrition.logic;

import model.user.domain.Profile;
import model.nutrition.domain.IDietPlan;
import model.nutrition.domain.DietPlan;


/* This class manages all adaptive nutrition logic. It adjusts the user's weekly
   calorie targets based on their progress and selected diet plan. Each diet
   plan applies its own strategy for modifying calories, allowing weight loss,
   muscle gain, or maintenance goals to behave differently through polymorphism. 
   
   All weekly average body weight data is now accessed through the Profile object,
   which serves as a gateway to the WeeklyAvg tracker. */
public class AdaptiveDietManager {

    /*
     * This field holds a reference to the user's profile, which contains
     * the weekly average body weight tracker. All weight tracking operations
     * are performed through the Profile object.
     */
    private final Profile userProfile;

    /*
     * This constructor initializes a new adaptive diet manager with a user profile.
     * The profile contains the weekly average tracker which is used to monitor
     * the user's progress.
     */
    public AdaptiveDietManager(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
        this.userProfile = profile;
    }

    /*
     * This method calculates the user's new weekly calorie target based on their
     * progress and the selected diet plan. It first attempts to use the tracked
     * weekly weight change if a complete week of data exists; otherwise, it
     * falls back to the manually provided weekly progress value.
     * 
     * The method recalculates TDEE using updated weight when possible, then
     * applies the plan-specific adjustment logic:
     * 
     * – Weight loss plans decrease calories (with safety throttling)
     * – Muscle gain plans increase calories (with rate-of-gain moderation)
     * – Maintenance plans apply small corrections when drifting upward/downward
     * 
     * The final result is clamped to reasonable safety bounds.
     */
    public int adjustCalories(Profile profile, double weeklyProgressKg, IDietPlan plan) {
        if (profile == null || plan == null) {
            return 0;
        }

        double actualProgress = weeklyProgressKg;
        Profile adjustedProfile = profile;

        // Use tracked 7-day data logic removed - relying on passed weeklyProgressKg

        // Base calorie calculation using updated weight if available
        // Base calorie calculation using updated weight if available
        int currentCalories = CalorieCalculator.generateSmartCalories(adjustedProfile, plan);

        if (currentCalories <= 0) {
            return currentCalories;
        }

        // Apply plan-specific adaptive adjustment using polymorphism
        int baseAdjustment = plan.goalCalorieAdjustment();
        int adjustment = plan.adaptiveAdjustment(actualProgress, baseAdjustment);

        // Safety bounds
        int result = currentCalories + adjustment;
        if (result < 1200)
            result = 1200;
        if (result > 5000)
            result = 5000;

        return result;
    }

    /*
     * This helper method creates a new profile instance with an updated bodyweight
     * while preserving all other attributes. It is used internally to ensure that
     * calorie calculations reflect the user's current weight rather than stale
     * profile data.
     */
    private Profile createUpdatedProfile(Profile originalProfile, double newWeight) {
        return new Profile(
                originalProfile.getUser(), // Pass the User object directly
                originalProfile.getHeightInCm(),
                newWeight, // updated weight
                originalProfile.getGoal(),
                originalProfile.getGender(),
                originalProfile.getDateOfBirth(),
                originalProfile.getFitnessLevel());
    }

    /*
     * This method determines whether a calorie adjustment is necessary. For
     * weight loss or muscle gain plans, adjustments are always applied. For
     * maintenance, adjustments are applied only when the user drifts outside
     * an acceptable range around their target bodyweight.
     */
    public boolean shouldAdjustCalories(Profile profile, double weeklyProgressKg, IDietPlan plan) {
        double actualProgress = weeklyProgressKg;

        // Access weekly data logic removed - relying on passed weeklyProgressKg

        // For weight loss and muscle gain, always adjust
        // For maintenance, only adjust if drifting significantly
        // This logic is now handled polymorphically in each plan's adaptiveAdjustment
        // method
        return plan.adaptiveAdjustment(actualProgress, plan.goalCalorieAdjustment()) != 0;
    }

    /**
     * Generates an updated nutritional plan based on the user's progress.
     * This method orchestrates the entire adaptive process:
     * 1. Checks if adjustment is needed
     * 2. Calculates new calorie targets
     * 3. Generates a new plan with updated macros
     * 
     * @param profile          The user's profile
     * @param weeklyProgressKg The actual weight change over the last week
     * @return A new DietPlan object if adjustment occurred, or null if no change
     *         needed
     */
    public IDietPlan generateUpdatedPlan(Profile profile, double weeklyProgressKg) {
        if (profile == null) {
            return null;
        }

        IDietPlan currentPlan = profile.getCurrentDietPlan();
        if (currentPlan == null) {
            // Generate initial plan if none exists
            return NutritionalPlanGenerator.generatePlan(profile);
        }

        // Check if adjustment is needed
        if (!shouldAdjustCalories(profile, weeklyProgressKg, currentPlan)) {
            System.out.println("📊 Progress on track - no adjustment needed");
            return null; // No change needed
        }

        // Calculate new calories
        int newCalories = adjustCalories(profile, weeklyProgressKg, currentPlan);

        // Generate updated plan with new calories
        IDietPlan updatedPlan = NutritionalPlanGenerator.generateCustomPlan(profile, newCalories);

        System.out.printf("🔄 Calories adjusted: %d → %d kcal/day%n",
                (int) currentPlan.getTargetDailyCalories(),
                newCalories);

        return updatedPlan;
    }

    /**
     * Triggers a full regeneration of the nutrition plan based on updated Goal or
     * Fitness Level.
     * This ensures the user's plan always matches their current profile settings.
     */
    public void onGoalOrFitnessChange() {
        if (userProfile == null) {
            return;
        }

        System.out.println("🔄 Detected change in Goal or Fitness Level...");
        System.out.println("⚙️  Recalculating TDEE and regenerating plan...");

        // 1. Generate a new plan based on the UPDATED profile fields
        IDietPlan newPlan = NutritionalPlanGenerator.generatePlan(userProfile);

        // 2. Set the new plan to the user profile
        userProfile.setCurrentDietPlan(newPlan);

        // 3. Print confirmation
        System.out.println("✅ Nutrition Plan updated successfully!");
        if (newPlan instanceof DietPlan) {
            System.out.println("   New Target: " + (int) newPlan.getTargetDailyCalories() + " kcal/day");
            System.out.println("   Focus: " + newPlan.describeGoal());
        }
    }

    /**
     * Processes the weekly update for the user's nutritional plan.
     * This method encapsulates the logic for checking if an update is due,
     * validating data requirements, and applying the update if necessary.
     *
     * @param profile The user's profile to update.
     */
    public boolean processWeeklyUpdate(Profile profile) {
        if (profile == null) {
            return false;
        }

        int completedWeeks = profile.getWeightTracker().getCompletedWeeksCount();
        int lastAdjusted = profile.getLastAdjustedWeekIndex();

        System.out.println("[DEBUG] Adaptive Check: Completed=" + completedWeeks + ", LastAdjusted=" + lastAdjusted);

        // Enforce 2-week interval for adjustments
        if ((completedWeeks - lastAdjusted) < 2) {
            System.out.println("ℹ️  Calorie adjustment will happen after 2 weeks of new data (Current Diff: "
                    + (completedWeeks - lastAdjusted) + ").");
            return false;
        }

        // Check if we have enough previous history for the new formula
        double last2WeeksAvg = profile.getWeightTracker().getPreviousTwoWeeksAverage();
        if (last2WeeksAvg <= 0) {
            System.out.println("⚠️  Need at least 2 weeks of history for calculation (Current: "
                    + profile.getLastCompletedWeekAvg() + ")");
            return false;
        }

        // Formula: diff = currentWeeklyAvg - last2WeeksAvg
        // currentWeeklyAvg is the one just completed, stored in
        // profile.getLastCompletedWeekAvg()
        double currentWeeklyAvg = profile.getLastCompletedWeekAvg();
        double weeklyProgress = currentWeeklyAvg - last2WeeksAvg;

        System.out.println("[DEBUG] Progress calc: Current(" + currentWeeklyAvg + ") - Last2Avg(" + last2WeeksAvg
                + ") = " + weeklyProgress);

        IDietPlan updatedPlan = generateUpdatedPlan(profile, weeklyProgress);

        if (updatedPlan != null) {
            profile.setCurrentDietPlan(updatedPlan);
            profile.setWeightInKg(profile.getLastCompletedWeekAvg());
            profile.setLastAdjustedWeekIndex(completedWeeks); // Mark this week as adjusted
            System.out.println("✅ Nutritional plan updated!");
            return true;
        }

        return false;
    }
}
