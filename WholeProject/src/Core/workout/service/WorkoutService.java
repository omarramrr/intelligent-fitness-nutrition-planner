package model.workout.service;

import model.user.domain.Profile;
import model.workout.domain.WorkoutExerciseEntry;
import model.workout.enums.ExerciseLibrary;
import model.workout.enums.MuscleGroup;
import model.workout.enums.PreferredEquipment;
import model.workout.enums.TrainingSplit;
import model.workout.service.components.construction.PlanBuilder;
import model.workout.service.components.data.MuscleGroups;
import model.workout.service.components.optimization.FrequencyManager;
import model.workout.service.components.optimization.RecoveryManager;
import model.workout.service.components.optimization.VolumeCalculator;
import model.workout.service.components.optimization.WeakPointManager;
import model.workout.service.components.selection.ExerciseSelector;
import model.workout.service.components.selection.RotationTracker;
import model.workout.service.components.selection.TrainingSplitSelector;
import model.workout.service.interfaces.*;

import java.util.*;

public class WorkoutService {

    private final IExerciseSelector exerciseSelector;
    private final IFrequencyManager frequencyManager;
    private final IVolumeCalculator volumeCalculator;
    private final IRecoveryManager recoveryManager;
    private final IWeakPointManager weakPointManager;
    private final IPlanBuilder planBuilder;

    // Constructor with dependency injection using interfaces
    public WorkoutService(
            IExerciseSelector exerciseSelector,
            IFrequencyManager frequencyManager,
            IVolumeCalculator volumeCalculator,
            IRecoveryManager recoveryManager,
            IWeakPointManager weakPointManager,
            IPlanBuilder planBuilder) {
        this.exerciseSelector = exerciseSelector;
        this.frequencyManager = frequencyManager;
        this.volumeCalculator = volumeCalculator;
        this.recoveryManager = recoveryManager;
        this.weakPointManager = weakPointManager;
        this.planBuilder = planBuilder;
    }

    // Default constructor that creates concrete implementations
    public WorkoutService() {
        WeakPointManager concreteWeakPointManager = new WeakPointManager();
        ExerciseSelector concreteExerciseSelector = new ExerciseSelector();

        this.exerciseSelector = concreteExerciseSelector;
        this.weakPointManager = concreteWeakPointManager;
        this.frequencyManager = new FrequencyManager(concreteWeakPointManager);
        this.volumeCalculator = new VolumeCalculator(concreteWeakPointManager);
        this.recoveryManager = new RecoveryManager();
        this.planBuilder = new PlanBuilder(concreteExerciseSelector);
    }

    // ==================== PUBLIC DELEGATES ====================

    /**
     * Returns a list of allowed training splits based on the number of training
     * days.
     */
    public List<TrainingSplit> getAllowedSplits(int trainingDays) {
        return new TrainingSplitSelector(trainingDays).determineSplit();
    }

    /**
     * Checks if a specific split is valid for the given number of training days.
     */
    public boolean isValidSplit(TrainingSplit split, int trainingDays) {
        List<TrainingSplit> allowed = new TrainingSplitSelector(trainingDays).determineSplit();
        return allowed.contains(split);
    }

    /**
     * Main method to select exercises.
     */
    public List<ExerciseLibrary> selectExercises(MuscleGroup group,
            List<PreferredEquipment> preferences, RotationTracker rotationTracker, int limit,
            int currentDay) {
        return exerciseSelector.selectExercises(group, preferences, rotationTracker, limit, currentDay);
    }

    /**
     * Derives how many times per week each muscle should be trained.
     */
    public Map<MuscleGroup, Integer> deriveFrequencyTargets(TrainingSplit split, int trainingDays,
            Set<MuscleGroup> weakPoints) {
        return frequencyManager.deriveFrequencyTargets(split, trainingDays, weakPoints);
    }

    /**
     * Applies the derived frequency targets onto an existing day→muscle mapping.
     */
    public Map<Integer, Set<MuscleGroup>> applyFrequencyTargets(Map<Integer, Set<MuscleGroup>> baseFocus,
            Map<MuscleGroup, Integer> frequencyTargets, int minSpacingDays) {
        return frequencyManager.applyFrequencyTargets(baseFocus, frequencyTargets, minSpacingDays);
    }

    /**
     * Enforces minimum spacing between appearances of high-stress muscles.
     */
    public void enforceSpacing(Map<Integer, Set<MuscleGroup>> dayMuscleFocus, int minHoursBetween) {
        recoveryManager.enforceSpacing(dayMuscleFocus, minHoursBetween);
    }

    /**
     * Evidence-based weekly volume heuristics.
     */
    public Map<MuscleGroup, Integer> calculateWeeklyVolume(Profile profile, Set<MuscleGroup> weakPoints,
            TrainingSplit split, int trainingDays) {
        return volumeCalculator.calculateWeeklyVolume(profile, weakPoints, split, trainingDays);
    }

    /**
     * Apply weak-point priority by increasing weekly volume.
     */
    public Map<MuscleGroup, Integer> applyPriority(Map<MuscleGroup, Integer> baseVolumes,
            Set<MuscleGroup> weakPoints) {
        return weakPointManager.applyPriority(baseVolumes, weakPoints);
    }

    /**
     * Ensure the returned set is safe to iterate and modify.
     */
    public Set<MuscleGroup> sanitizeWeakPoints(Set<MuscleGroup> weakPoints) {
        return weakPointManager.sanitizeWeakPoints(weakPoints);
    }

    /**
     * Build a mapping of training day → targeted muscle groups.
     */
    public Map<Integer, Set<MuscleGroup>> buildSplitFocus(TrainingSplit split, int trainingDays) {
        return planBuilder.buildSplitFocus(split, trainingDays);
    }

    /**
     * Distributes total weekly training volume into daily volume.
     */
    public Map<Integer, Map<MuscleGroup, Integer>> allocateDailyVolume(
            Map<Integer, Set<MuscleGroup>> dayMuscleFocus,
            Map<MuscleGroup, Integer> weeklyVolumes,
            int trainingDays,
            Set<MuscleGroup> weakPoints) {
        return volumeCalculator.allocateDailyVolume(dayMuscleFocus, weeklyVolumes, trainingDays, weakPoints);
    }

    /**
     * Build the list of exercises for a single training day.
     */
    public List<WorkoutExerciseEntry> buildDayExercises(int dayIndex,
            Map<MuscleGroup, Integer> dayMuscles,
            Map<MuscleGroup, List<PreferredEquipment>> equipmentMap, RotationTracker rotationTracker,
            Profile profile, TrainingSplit split) {
        return planBuilder.buildDayExercises(dayIndex, dayMuscles, equipmentMap, rotationTracker, profile,
                split);
    }

    // ==================== MUSCLE GROUP CONSTANTS ====================

    public static Set<MuscleGroup> getLargeMuscles() {
        return MuscleGroups.LARGE_MUSCLES;
    }

    public static Set<MuscleGroup> getHighStressMuscles() {
        return MuscleGroups.HIGH_STRESS_MUSCLES;
    }

    public static Set<MuscleGroup> getAnteriorMuscles() {
        return MuscleGroups.ANTERIOR;
    }

    public static Set<MuscleGroup> getPosteriorMuscles() {
        return MuscleGroups.POSTERIOR;
    }

    public static Set<MuscleGroup> getUpperMuscles() {
        return MuscleGroups.UPPER;
    }

    public static Set<MuscleGroup> getLowerMuscles() {
        return MuscleGroups.LOWER;
    }

    // ==================== GETTERS ====================

    public IExerciseSelector getExerciseSelector() {
        return exerciseSelector;
    }

    public IFrequencyManager getFrequencyManager() {
        return frequencyManager;
    }

    public IVolumeCalculator getVolumeCalculator() {
        return volumeCalculator;
    }

    public IRecoveryManager getRecoveryManager() {
        return recoveryManager;
    }

    public IWeakPointManager getWeakPointManager() {
        return weakPointManager;
    }

    public IPlanBuilder getPlanBuilder() {
        return planBuilder;
    }
}
