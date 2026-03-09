package model.workout.domain;

import model.user.domain.Profile;
import model.workout.enums.MuscleGroup;
import model.workout.enums.PreferredEquipment;
import model.workout.enums.TrainingSplit;
import java.util.*;

/**
 * This class represents the full planning context used to build a workout plan.
 * It collects everything the generator needs—such as the user profile,
 * chosen split, weekly frequency, volume targets, equipment preferences,
 * weak points, and restrictions—and stores them in immutable form.
 * The goal is to centralize all logic-related values so that blueprint
 * classes can focus only on describing the session layout rather than
 * recalculating rules or reading profile data on their own.
 */
public final class PlanContext {

    // The user's personal profile, containing experience level,
    // age, goals, and other attributes needed to adjust volume and difficulty.
    private final Profile profile;

    // The training split the user selected (e.g., push/pull/legs, full-body, etc.),
    // which determines how days are organized.
    private final TrainingSplit split;

    // The number of training days per week that the user committed to.
    private final int trainingDays;

    // A map showing which muscle groups are the main target for each day.
    // Example: Day 1 → {CHEST, SHOULDERS}. Stored as immutable EnumSets.
    private final Map<Integer, Set<MuscleGroup>> dayMuscleFocus;

    // A nested map showing how many sets each muscle gets on each day.
    // Example: Day 1 → {CHEST=12, TRICEPS=8}. All maps are defensive copies.
    private final Map<Integer, Map<MuscleGroup, Integer>> dailyMuscleVolumes;

    // Total weekly sets assigned to each muscle group after combining all days.
    private final Map<MuscleGroup, Integer> weeklyMuscleVolumes;

    // Frequency targets per muscle group (how many times per week each should
    // appear).
    private final Map<MuscleGroup, Integer> frequencyTargets;

    // The user's preferred equipment for each muscle group, stored as lists.
    private final Map<MuscleGroup, List<PreferredEquipment>> equipmentPreferences;

    // The user's weak points—muscles that need extra attention or volume
    // adjustments.
    private final Set<MuscleGroup> weakPoints;

    /**
     * Private constructor that receives a Builder instance.
     * The constructor’s main job is to create immutable defensive copies
     * so nothing can modify the context after creation, ensuring a safe,
     * predictable workflow for the workout generator.
     */
    private PlanContext(Builder builder) {
        this.profile = builder.profile;
        this.split = builder.split;
        this.trainingDays = builder.trainingDays;

        // Defensive copies ensure the maps cannot be modified outside.
        this.dayMuscleFocus = wrapFocus(builder.dayMuscleFocus);
        this.dailyMuscleVolumes = wrapNestedMap(builder.dailyMuscleVolumes);

        // EnumMap is used because it is fast, memory-efficient, and ideal for enums.
        this.weeklyMuscleVolumes = new EnumMap<>(builder.weeklyMuscleVolumes);
        this.frequencyTargets = new EnumMap<>(builder.frequencyTargets);

        // Equipment lists are also copied so nothing external can alter them.
        this.equipmentPreferences = wrapEquipment(builder.equipmentPreferences);

        // Weak points and restrictions also copied as new HashSets.
        this.weakPoints = new HashSet<>(builder.weakPoints);
    }

    /**
     * Creates a deep defensive copy of the nested “daily volume” map.
     * This ensures that each day has its own inner EnumMap for storing
     * muscle volumes, preventing outside modification.
     */
    private static Map<Integer, Map<MuscleGroup, Integer>> wrapNestedMap(
            Map<Integer, Map<MuscleGroup, Integer>> source) {

        Map<Integer, Map<MuscleGroup, Integer>> result = new HashMap<>();

        for (Map.Entry<Integer, Map<MuscleGroup, Integer>> entry : source.entrySet()) {
            Integer day = entry.getKey();
            Map<MuscleGroup, Integer> inner = entry.getValue();

            // Inner copy. If null, create an empty one.
            Map<MuscleGroup, Integer> copy = new EnumMap<>(MuscleGroup.class);
            if (inner != null) {
                copy.putAll(inner);
            }
            result.put(day, copy);
        }
        return result;
    }

    /**
     * Creates a defensive copy of muscle focus per day.
     * EnumSet is used because it stores enum values compactly and efficiently.
     */
    private static Map<Integer, Set<MuscleGroup>> wrapFocus(Map<Integer, Set<MuscleGroup>> source) {

        Map<Integer, Set<MuscleGroup>> result = new HashMap<>();

        for (Map.Entry<Integer, Set<MuscleGroup>> entry : source.entrySet()) {
            Integer day = entry.getKey();
            Set<MuscleGroup> muscles = entry.getValue();

            // If the incoming set is empty, create an empty EnumSet instead of copying.
            Set<MuscleGroup> copy = muscles.isEmpty()
                    ? EnumSet.noneOf(MuscleGroup.class)
                    : EnumSet.copyOf(muscles);

            result.put(day, copy);
        }
        return result;
    }

    /**
     * Creates a defensive copy of equipment preferences.
     * Each muscle group gets a new list containing all preferred equipment.
     */
    private static Map<MuscleGroup, List<PreferredEquipment>> wrapEquipment(
            Map<MuscleGroup, List<PreferredEquipment>> equipmentPreferences) {

        Map<MuscleGroup, List<PreferredEquipment>> result = new EnumMap<>(MuscleGroup.class);

        for (Map.Entry<MuscleGroup, List<PreferredEquipment>> entry : equipmentPreferences.entrySet()) {
            MuscleGroup key = entry.getKey();
            List<PreferredEquipment> list = entry.getValue();

            result.put(key, new ArrayList<>(list)); // Copy the list
        }
        return result;
    }

    // Standard getters that expose the immutable data to other classes.

    public Profile getProfile() {
        return profile;
    }

    public TrainingSplit getSplit() {
        return split;
    }

    public int getTrainingDays() {
        return trainingDays;
    }

    public Map<Integer, Set<MuscleGroup>> getDayMuscleFocus() {
        return dayMuscleFocus;
    }

    public Map<Integer, Map<MuscleGroup, Integer>> getDailyMuscleVolumes() {
        return dailyMuscleVolumes;
    }

    public Map<MuscleGroup, Integer> getWeeklyMuscleVolumes() {
        return weeklyMuscleVolumes;
    }

    public Map<MuscleGroup, Integer> getFrequencyTargets() {
        return frequencyTargets;
    }

    public Map<MuscleGroup, List<PreferredEquipment>> getEquipmentPreferences() {
        return equipmentPreferences;
    }

    public Set<MuscleGroup> getWeakPoints() {
        return weakPoints;
    }

    // Returns a new builder instance for assembling a PlanContext.
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The Builder class collects all variables step-by-step.
     * Each method simply stores values until build() is called.
     * Validation happens at the end to guarantee correctness.
     */
    public static final class Builder {
        private Profile profile;
        private TrainingSplit split;
        private int trainingDays;

        // Default empty containers so builder never throws null pointer errors.
        private Map<Integer, Set<MuscleGroup>> dayMuscleFocus = new HashMap<>();
        private Map<Integer, Map<MuscleGroup, Integer>> dailyMuscleVolumes = new HashMap<>();
        private Map<MuscleGroup, Integer> weeklyMuscleVolumes = new EnumMap<>(MuscleGroup.class);
        private Map<MuscleGroup, Integer> frequencyTargets = new EnumMap<>(MuscleGroup.class);
        private Map<MuscleGroup, List<PreferredEquipment>> equipmentPreferences = new EnumMap<>(MuscleGroup.class);
        private Set<MuscleGroup> weakPoints = new HashSet<>();

        // Simple setter methods returning the same builder to allow chaining.
        public Builder profile(Profile profile) {
            this.profile = profile;
            return this;
        }

        public Builder split(TrainingSplit split) {
            this.split = split;
            return this;
        }

        public Builder trainingDays(int trainingDays) {
            this.trainingDays = trainingDays;
            return this;
        }

        public Builder dayMuscleFocus(Map<Integer, Set<MuscleGroup>> map) {
            this.dayMuscleFocus = new HashMap<>(map);
            return this;
        }

        public Builder dailyMuscleVolumes(Map<Integer, Map<MuscleGroup, Integer>> map) {
            this.dailyMuscleVolumes = new HashMap<>(map);
            return this;
        }

        public Builder weeklyMuscleVolumes(Map<MuscleGroup, Integer> map) {
            this.weeklyMuscleVolumes = new EnumMap<>(MuscleGroup.class);
            if (map != null)
                this.weeklyMuscleVolumes.putAll(map);
            return this;
        }

        public Builder frequencyTargets(Map<MuscleGroup, Integer> map) {
            this.frequencyTargets = new EnumMap<>(MuscleGroup.class);
            if (map != null)
                this.frequencyTargets.putAll(map);
            return this;
        }

        public Builder equipmentPreferences(Map<MuscleGroup, List<PreferredEquipment>> map) {
            this.equipmentPreferences = new EnumMap<>(MuscleGroup.class);
            if (map != null)
                this.equipmentPreferences.putAll(map);
            return this;
        }

        public Builder weakPoints(Set<MuscleGroup> set) {
            this.weakPoints = set == null ? new HashSet<>() : new HashSet<>(set);
            return this;
        }

        /**
         * Validates required fields before constructing the final context.
         * Ensures we never create an invalid workout planning configuration.
         */
        public PlanContext build() {
            if (profile == null)
                throw new IllegalStateException("Profile is required");

            if (split == null)
                throw new IllegalStateException("Training split is required");

            if (trainingDays <= 0)
                throw new IllegalStateException("Training days must be positive");

            return new PlanContext(this);
        }
    }
}
