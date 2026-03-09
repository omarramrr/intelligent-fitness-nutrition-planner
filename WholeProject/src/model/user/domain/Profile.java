package model.user.domain;

import java.time.LocalDate;
import java.time.Period;
import model.user.enums.GoalType;
import model.user.enums.Gender;
import model.trackbodyweight.service.WeightTracker;
import model.user.enums.FitnessLevel;
import model.nutrition.domain.IDietPlan;
import model.nutrition.logic.AdaptiveDietManager;
import model.workout.domain.WorkoutPlan;
import model.workout.domain.PlanContext;

public class Profile {
    private final String profileId;
    private final User user;
    // Age is calculated from dateOfBirth, not stored
    private double heightInCm;
    private double weightInKg;
    private double startWeight;
    private GoalType goal;
    private Gender gender;
    private LocalDate dateOfBirth;
    private FitnessLevel fitnessLevel;
    private WeightTracker weightTracker;
    private IDietPlan currentDietPlan;
    private AdaptiveDietManager dietManager;
    private WorkoutPlan currentWorkoutPlan;
    private PlanContext lastWorkoutContext;
    private int lastAdjustedWeekIndex = 0; // Tracks the last week index where calories were adjusted

    private String generateUniqueProfileId() {
        return "PROF-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Constructs a new Profile for an existing User.
     *
     * @param user         The user associated with this profile.
     * @param heightInCm   Height in centimeters.
     * @param weightInKg   Current weight in kilograms.
     * @param goal         The fitness goal.
     * @param gender       The gender.
     * @param dateOfBirth  The date of birth.
     * @param fitnessLevel The current fitness level.
     */
    public Profile(User user, double heightInCm, double weightInKg,
            GoalType goal, Gender gender, LocalDate dateOfBirth,
            FitnessLevel fitnessLevel) {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");
        this.profileId = generateUniqueProfileId();
        this.user = user;
        setDateOfBirth(dateOfBirth); // Set DOB first to validate age
        setHeightInCm(heightInCm);
        setWeightInKg(weightInKg);
        this.startWeight = weightInKg; // Store initial weight
        setGoal(goal);
        setGender(gender);
        setFitnessLevel(fitnessLevel);
        this.weightTracker = new WeightTracker(this.profileId);
    }

    /**
     * Constructs a new Profile and creates a new User internally.
     *
     * @param username     The username for the new User.
     * @param email        The email for the new User.
     * @param password     The password for the new User.
     * @param heightInCm   Height in centimeters.
     * @param weightInKg   Current weight in kilograms.
     * @param goal         The fitness goal.
     * @param gender       The gender.
     * @param dateOfBirth  The date of birth.
     * @param fitnessLevel The current fitness level.
     */
    public Profile(String username, String email, String password,
            double heightInCm, double weightInKg, GoalType goal,
            Gender gender, LocalDate dateOfBirth, FitnessLevel fitnessLevel) {
        this(createUserWithPassword(username, email, password), heightInCm, weightInKg,
                goal, gender, dateOfBirth, fitnessLevel);
    }

    private static User createUserWithPassword(String username, String email, String password) {
        User user = new User(username, email);
        user.setPassword(password);
        return user;
    }

    /**
     * Constructs a Profile with a specific ID (useful for loading from storage).
     *
     * @param profileId    The existing profile ID.
     * @param user         The user associated with this profile.
     * @param heightInCm   Height in centimeters.
     * @param weightInKg   Current weight in kilograms.
     * @param goal         The fitness goal.
     * @param gender       The gender.
     * @param dateOfBirth  The date of birth.
     * @param fitnessLevel The current fitness level.
     */
    public Profile(String profileId, User user, double heightInCm,
            double weightInKg, GoalType goal, Gender gender,
            LocalDate dateOfBirth, FitnessLevel fitnessLevel) {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null");
        this.profileId = profileId;
        this.user = user;
        setDateOfBirth(dateOfBirth); // Set DOB first to validate age
        setHeightInCm(heightInCm);
        setWeightInKg(weightInKg);
        this.startWeight = weightInKg; // Store initial weight
        setGoal(goal);
        setGender(gender);
        setFitnessLevel(fitnessLevel);
        this.weightTracker = new WeightTracker(this.profileId);
    }

    // ====== SETTERS ======

    public void setHeightInCm(double heightInCm) {
        if (heightInCm < 100 || heightInCm > 250)
            throw new IllegalArgumentException("Height must be 100-250 cm");
        this.heightInCm = heightInCm;
    }

    public void setWeightInKg(double weightInKg) {
        if (weightInKg < 30 || weightInKg > 300)
            throw new IllegalArgumentException("Weight must be 30-300 kg");
        this.weightInKg = weightInKg;
    }

    public void setGoal(GoalType goal) {
        if (goal == null)
            throw new IllegalArgumentException("Goal required");

        boolean isDifferent = (this.goal != goal);
        this.goal = goal;

        // Trigger automatic update if initialized and goal actually changed
        if (isDifferent && dietManager != null) {
            dietManager.onGoalOrFitnessChange();
        }
    }

    public void setGender(Gender gender) {
        if (gender == null)
            throw new IllegalArgumentException("Gender required");
        this.gender = gender;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null)
            throw new IllegalArgumentException("DOB required");
        if (dateOfBirth.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("DOB cannot be in future");
        if (dateOfBirth.isBefore(LocalDate.now().minusYears(120)))
            throw new IllegalArgumentException("DOB exceeds age limit");

        // Validate calculated age
        int calculatedAge = calculateAge(dateOfBirth);
        if (calculatedAge < 10 || calculatedAge > 120)
            throw new IllegalArgumentException("Age must be 10-120");

        this.dateOfBirth = dateOfBirth;
    }

    public void setFitnessLevel(FitnessLevel fitnessLevel) {
        if (fitnessLevel == null)
            throw new IllegalArgumentException("Fitness level needed");

        boolean isDifferent = (this.fitnessLevel != fitnessLevel);
        this.fitnessLevel = fitnessLevel;

        // Trigger automatic update if initialized and fitness level actually changed
        if (isDifferent && dietManager != null) {
            dietManager.onGoalOrFitnessChange();
        }
    }

    public void setCurrentDietPlan(IDietPlan plan) {
        this.currentDietPlan = plan;
    }

    public void setCurrentWorkoutPlan(WorkoutPlan plan) {
        this.currentWorkoutPlan = plan;
    }

    public void setLastWorkoutContext(PlanContext context) {
        this.lastWorkoutContext = context;
    }

    public PlanContext getLastWorkoutContext() {
        return lastWorkoutContext;
    }

    public Double addDailyWeight(double weight) {
        return weightTracker.addDailyWeight(weight);
    }

    public void loadWeightHistory(java.util.List<model.trackbodyweight.domain.WeeklyAverageEntry> history) {
        weightTracker.loadHistory(history);
    }

    // ====== GETTERS ======

    public String getProfileId() {
        return profileId;
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public int getAge() {
        return calculateAge(dateOfBirth);
    }

    public double getHeightInCm() {
        return heightInCm;
    }

    public double getWeightInKg() {
        return weightInKg;
    }

    public GoalType getGoal() {
        return goal;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public FitnessLevel getFitnessLevel() {
        return fitnessLevel;
    }

    public IDietPlan getCurrentDietPlan() {
        return currentDietPlan;
    }

    public WorkoutPlan getCurrentWorkoutPlan() {
        return currentWorkoutPlan;
    }

    public void setLastAdjustedWeekIndex(int lastAdjustedWeekIndex) {
        this.lastAdjustedWeekIndex = lastAdjustedWeekIndex;
    }

    public int getLastAdjustedWeekIndex() {
        return lastAdjustedWeekIndex;
    }

    public double thisWeekWeeklyAvg() {
        return weightTracker.thisWeekWeeklyAvg();
    }

    /**
     * Returns an unmodifiable view of the daily weights.
     * To add weights, use {@link #addDailyWeight(double)}.
     */
    public java.util.List<Double> getDailyWeights() {
        return java.util.Collections.unmodifiableList(weightTracker.getDailyWeights());
    }

    public double getLastCompletedWeekAvg() {
        return weightTracker.getLastCompletedWeekAvg();
    }

    // ====== OTHER METHODS ======

    // Helper method to calculate age from date of birth
    private int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 0;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public void printWeeklyAverages() {
        weightTracker.printWeeklyAverages();
    }

    public boolean hasFullWeek() {
        return weightTracker.hasFullWeek();
    }

    public void initializeDietManager() {
        this.dietManager = new AdaptiveDietManager(this);
    }

    public boolean updateNutritionalPlan() {
        if (dietManager == null) {
            System.out.println("⚠️  Diet manager not initialized");
            return false;
        }
        return dietManager.processWeeklyUpdate(this);
    }

    public void displayProfile() {
        System.out.println("\n👤 YOUR PROFILE");
        System.out.println("─".repeat(70));
        System.out.println("   Name: " + getUsername());
        System.out.println("   User ID: " + getProfileId());
        System.out.println("   Age: " + getAge() + " years");
        System.out.println("   Fitness Level: " + getFitnessLevel());
        System.out.println("   Goal: " + getGoal().getDescription());
        System.out.println("   Start Weight: " + startWeight + " kg");

        double lastWeeklyAvg = getLastCompletedWeekAvg();
        if (lastWeeklyAvg > 0) {
            System.out.println("   Last Weekly Average Weight: " + String.format("%.2f", lastWeeklyAvg) + " kg");
        } else {
            System.out.println("   Last Weekly Average Weight: N/A (No completed week yet)");
        }
        System.out.println("─".repeat(70));
    }

    public WeightTracker getWeightTracker() {
        return weightTracker;
    }

}
