package model.workout.service;

import model.user.domain.Profile;
import model.workout.domain.PlanContext;
import model.workout.service.components.AutomaticPlanBuilder;
import model.workout.domain.RepsRange;
import model.workout.domain.WorkoutDay;
import model.workout.domain.WorkoutExerciseEntry;
import model.workout.domain.WorkoutPlan;
import model.workout.enums.MuscleGroup;
import model.workout.enums.TrainingSplit;


import java.util.*;

/**
 * WorkoutPlanGenerator - Unified Workout Plan Creation Class
 * 
 * This class acts as a facade/factory for creating workout plans.
 * It supports two modes of operation:
 * 1. Smart Generation: Delegates to AutomaticPlanBuilder.
 * 2. Manual Building: Allows step-by-step construction of a workout plan.
 * 
 * Refactored to follow clean architecture and remove duplication.
 */
public class WorkoutPlanGenerator {

    private final PlanContext planContext;
    private final WorkoutService workoutService;

    // ==================== MANUAL BUILDER FIELDS ====================
    private Profile manualProfile;

    // ==================== BUILDER STATE FIELDS ====================
    private final List<WorkoutDay> builtDays = new ArrayList<>();
    private int currentDayIndex = 0;
    private TrainingSplit currentSplitType;
    private final List<WorkoutExerciseEntry> currentDayExercises = new ArrayList<>();

    // ==================== CONSTRUCTORS ====================

    /**
     * Private constructor for the Manual Builder mode.
     */
    private WorkoutPlanGenerator() {
        this.planContext = null;
        this.workoutService = null;
    }

    /**
     * Constructor for Smart Generation mode.
     * Uses composition to delegate logic and data.
     */
    public WorkoutPlanGenerator(PlanContext planContext, WorkoutService workoutService) {
        this.planContext = Objects.requireNonNull(planContext, "PlanContext is required");
        this.workoutService = Objects.requireNonNull(workoutService, "WorkoutService is required");
        this.manualProfile = null;
    }

    // ==================== FACTORY METHOD ====================

    /**
     * Factory method to create a new WorkoutPlanGenerator instance for manual
     * building.
     */
    public static WorkoutPlanGenerator create() {
        return new WorkoutPlanGenerator();
    }

    // ==================== SMART GENERATION LOGIC ====================

    /**
     * Generates a complete WorkoutPlan based on the initialized configuration.
     * Delegates to AutomaticPlanBuilder.
     */
    public WorkoutPlan generate() {
        if (planContext == null || workoutService == null) {
            throw new IllegalStateException("Cannot generate plan: PlanContext or WorkoutService is missing.");
        }

        return new AutomaticPlanBuilder(planContext, workoutService).build();
    }

    // ==================== MANUAL BUILDER LOGIC ====================

    /**
     * Set the profile for this workout plan.
     */
    public WorkoutPlanGenerator withProfile(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        this.manualProfile = profile;
        return this;
    }

    /**
     * Start building a new day in the workout plan.
     */
    public WorkoutPlanGenerator newDay(int dayIndex, TrainingSplit splitType) {
        // Save previous day if it exists
        if (!currentDayExercises.isEmpty()) {
            builtDays.add(new WorkoutDay(currentDayIndex, currentSplitType, currentDayExercises));
            currentDayExercises.clear();
        }

        if (dayIndex < 1)
            throw new IllegalArgumentException("Day index must be at least 1");
        Objects.requireNonNull(splitType, "Split type cannot be null");

        this.currentDayIndex = dayIndex;
        this.currentSplitType = splitType;
        return this;
    }

    /**
     * Add an exercise to the current day.
     */
    public WorkoutPlanGenerator addExercise(String exerciseName, MuscleGroup muscleGroup, int sets,
            int minReps, int maxReps) {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(exerciseName, muscleGroup, sets,
                new RepsRange(minReps, maxReps));
        currentDayExercises.add(entry);
        return this;
    }

    /**
     * Add an exercise to the current day with a reps range object.
     */
    public WorkoutPlanGenerator addExercise(String exerciseName, MuscleGroup muscleGroup, int sets,
            RepsRange repsRange) {
        WorkoutExerciseEntry entry = new WorkoutExerciseEntry(exerciseName, muscleGroup, sets, repsRange);
        currentDayExercises.add(entry);
        return this;
    }

    /**
     * Build the final WorkoutPlan object from manually added days.
     */
    public WorkoutPlan build() {
        // Add the last day if it has exercises
        if (!currentDayExercises.isEmpty()) {
            builtDays.add(new WorkoutDay(currentDayIndex, currentSplitType, currentDayExercises));
        }

        // Validate requirements
        if (manualProfile == null) {
            throw new IllegalStateException("Profile is required");
        }
        if (builtDays.isEmpty()) {
            throw new IllegalStateException("Workout plan must have at least one day");
        }

        return new WorkoutPlan(new ArrayList<>(builtDays), manualProfile.getProfileId(), manualProfile.getGoal());
    }
}
