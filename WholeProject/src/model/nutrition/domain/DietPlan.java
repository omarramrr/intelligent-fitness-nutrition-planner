package model.nutrition.domain;

import java.time.LocalDate;

/**
 * DIET PLAN (Abstract Base Class)
 * ----------------------------------
 * Base class for all nutrition plans.
 * Defines common attributes, main formula logic, plan duration, and status
 * methods.
 * Subclasses must implement their own goal and calorie adjustment logic.
 */
public abstract class DietPlan implements IDietPlan {

    // Default macro percentage constants used when specific ratios aren't provided.
    // These provide a balanced macronutrient distribution as a fallback.
    protected static final double DEFAULT_PROTEIN_PERCENT = 0.30;
    protected static final double DEFAULT_CARBS_PERCENT = 0.40;
    protected static final double DEFAULT_FATS_PERCENT = 0.30;

    // Core data fields that define a diet plan.
    // These fields are now private to prevent direct modification by subclasses.
    // Subclasses can access them through protected getter methods.
    private int dietPlanId;
    private String userId;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private double targetDailyCalories;
    private double targetProteinGrams;
    private double targetCarbsGrams;
    private double targetFatsGrams;
    private String planDescription;
    private boolean active;

    public DietPlan() {
        this(0, "", "", LocalDate.now(), LocalDate.now().plusMonths(1), 2000.0);
    }

    // This constructor creates a complete diet plan with all necessary parameters.
    // It sets up the plan identity, duration, and initial calorie targets.
    // Macros are calculated automatically based on default percentages.
    /**
     * Full constructor (for practical plan creation)
     */
    public DietPlan(int dietPlanId, String userId, String planName, LocalDate startDate, LocalDate endDate,
            double targetDailyCalories) {
        this.dietPlanId = dietPlanId;
        this.userId = userId;
        this.planName = planName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetDailyCalories = targetDailyCalories;
        setMacros(targetDailyCalories, DEFAULT_PROTEIN_PERCENT, DEFAULT_CARBS_PERCENT, DEFAULT_FATS_PERCENT);
        this.planDescription = "";
        this.active = true;
    }

    // Utility method for calculating macronutrient grams from percentages and
    // calories.
    // This is used by constructors and subclasses to set up macro targets.

    // This method converts calorie targets and macro percentages into gram amounts.
    // It uses standard conversion factors: 4 calories per gram of protein/carbs, 9
    // for fats.
    // Called automatically during plan creation to set up nutritional targets.
    /**
     * Set macronutrient values (called by constructors and subclasses)
     */
    protected void setMacros(double calories, double proteinPct, double carbsPct, double fatsPct) {
        this.targetProteinGrams = calories * proteinPct / 4.0;
        this.targetCarbsGrams = calories * carbsPct / 4.0;
        this.targetFatsGrams = calories * fatsPct / 9.0;
    }

    // ==================== PROTECTED GETTERS FOR SUBCLASS ACCESS
    // ====================
    // These methods allow subclasses to access private fields without direct
    // modification.

    protected int getDietPlanIdInternal() {
        return dietPlanId;
    }

    protected String getUserIdInternal() {
        return userId;
    }

    protected String getPlanNameInternal() {
        return planName;
    }

    protected LocalDate getStartDateInternal() {
        return startDate;
    }

    protected LocalDate getEndDateInternal() {
        return endDate;
    }

    protected double getTargetDailyCaloriesInternal() {
        return targetDailyCalories;
    }

    protected double getTargetProteinGramsInternal() {
        return targetProteinGrams;
    }

    protected double getTargetCarbsGramsInternal() {
        return targetCarbsGrams;
    }

    protected double getTargetFatsGramsInternal() {
        return targetFatsGrams;
    }

    protected String getPlanDescriptionInternal() {
        return planDescription;
    }

    protected boolean isActiveInternal() {
        return active;
    }

    // Protected setters for controlled modification by subclasses
    protected void setTargetDailyCaloriesInternal(double calories) {
        this.targetDailyCalories = calories;
    }

    protected void setPlanDescriptionInternal(String description) {
        this.planDescription = description;
    }

    // Abstract methods that must be implemented by each specific diet plan type.
    // These define the unique behavior and goals of different nutrition plans.

    // Each diet plan subclass must describe its specific goal in human-readable
    // terms.
    /**
     * Brief description of the goal of this plan.
     */
    public abstract String describeGoal();

    // Each plan type defines how calories should be adjusted for adaptive dieting.
    // Weight loss plans reduce calories, muscle gain plans increase them,
    // maintenance keeps them stable.
    /**
     * Weekly calorie adjustment for adaptive dieting (+, -, or 0 calories)
     */
    public abstract int goalCalorieAdjustment();

    // Each plan type implements its own adaptive adjustment logic based on actual
    // progress.
    // This allows throttling/moderation when weight changes are too rapid.
    /**
     * Calculates adaptive calorie adjustment based on actual progress.
     * Allows each plan type to implement its own throttling/moderation logic.
     * 
     * @param actualProgress Actual weekly weight change in kg (negative = loss,
     *                       positive = gain)
     * @param baseAdjustment Base calorie adjustment from goalCalorieAdjustment()
     * @return Final adjusted calorie amount
     */
    public abstract int adaptiveAdjustment(double actualProgress, int baseAdjustment);

    // Public getter methods to access plan information.
    // These provide read-only access to all plan data.
    public int getDietPlanId() {
        return dietPlanId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPlanName() {
        return planName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getTargetDailyCalories() {
        return targetDailyCalories;
    }

    public double getTargetProteinGrams() {
        return targetProteinGrams;
    }

    public double getTargetCarbsGrams() {
        return targetCarbsGrams;
    }

    public double getTargetFatsGrams() {
        return targetFatsGrams;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public boolean isActive() {
        return active;
    }

    // Limited setter methods for plan status and description.
    // Most plan properties are set during construction and remain immutable.
    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPlanDescription(String desc) {
        this.planDescription = desc;
    }

    // Methods that implement business logic for plan status and duration checking.
    // These determine whether a plan is currently valid and active.

    // This method checks if the plan is both active and within its valid date
    // range.
    // A plan is only valid if it's enabled and today's date falls within start and
    // end dates.
    /**
     * Returns if the plan is active AND within the date range.
     */
    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return active && (!today.isBefore(startDate)) && (!today.isAfter(endDate));
    }

    // Calculates the total duration of the plan in days.
    // Useful for displaying plan length and progress tracking.
    /** Get plan duration in days */
    public long getPlanDurationDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    // Checks if the plan has started (current date is on or after start date).
    /** Plan has already started? */
    public boolean hasStarted() {
        return !LocalDate.now().isBefore(startDate);
    }

    // Checks if the plan has ended (current date is after end date).
    /** Plan has ended? */
    public boolean hasEnded() {
        return LocalDate.now().isAfter(endDate);
    }

    // Provides a concise string representation of the diet plan for debugging.
    // Shows the plan name and key nutritional targets.
    // ==== toString: Concise for debugging ====
    @Override
    public String toString() {
        return planName + ": " + String.format("%d cal, %.1fg protein, %.1fg carbs, %.1fg fats (%d days)",
                (int) targetDailyCalories, targetProteinGrams, targetCarbsGrams, targetFatsGrams,
                getPlanDurationDays());
    }

    /**
     * Generates a detailed formatted summary of this diet plan.
     * Displays plan details, nutrition targets, and duration in a professional
     * format.
     * 
     * @return Formatted string summary of the plan
     * @throws IllegalStateException if plan data is invalid
     */
    public String getPlanSummary() {
        // Validate plan data
        if (planName == null || planName.trim().isEmpty()) {
            throw new IllegalStateException("Plan name cannot be null or empty");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalStateException("Plan dates cannot be null");
        }
        if (targetDailyCalories <= 0) {
            throw new IllegalStateException("Target calories must be positive");
        }

        StringBuilder summary = new StringBuilder();
        summary.append("╔════════════════════════════════════════════════════════════════╗\n");
        summary.append("║              NUTRITIONAL PLAN SUMMARY                          ║\n");
        summary.append("╚════════════════════════════════════════════════════════════════╝\n\n");

        summary.append("📋 Plan Details:\n");
        summary.append(String.format("  Plan Name: %s\n", planName));
        summary.append(String.format("  Goal: %s\n", describeGoal()));
        summary.append(String.format("  Duration: %d days\n", getPlanDurationDays()));
        summary.append(String.format("  Start Date: %s\n", startDate));
        summary.append(String.format("  End Date: %s\n", endDate));
        summary.append(String.format("  Status: %s\n\n", getStatusString()));

        summary.append("🍽️  Daily Nutrition Targets:\n");
        summary.append(String.format("  Calories: %d kcal\n", (int) targetDailyCalories));
        summary.append(String.format("  Protein: %.1f g (%.0f%%)\n",
                targetProteinGrams, (targetProteinGrams * 4 / targetDailyCalories) * 100));
        summary.append(String.format("  Carbs: %.1f g (%.0f%%)\n",
                targetCarbsGrams, (targetCarbsGrams * 4 / targetDailyCalories) * 100));
        summary.append(String.format("  Fats: %.1f g (%.0f%%)\n\n",
                targetFatsGrams, (targetFatsGrams * 9 / targetDailyCalories) * 100));

        summary.append("════════════════════════════════════════════════════════════════\n");

        return summary.toString();
    }

    /**
     * Helper method to get a human-readable status string.
     * 
     * @return Status description (e.g., "Active", "Not Started", "Completed")
     */
    private String getStatusString() {
        if (!active) {
            return "Inactive";
        }
        if (!hasStarted()) {
            return "Not Started";
        }
        if (hasEnded()) {
            return "Completed";
        }
        return "Active";
    }
}
