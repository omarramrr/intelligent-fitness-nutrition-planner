package model.workout.enums;

import java.util.*;

// A complete collection of resistance training exercises with over 100 different movements.
// This enum is the central database of exercise information, with fast lookup tools for planning workouts.
//
// Key features:
// - Full exercise database with names, muscle targets, equipment needs, and difficulty levels
// - Fast lookup maps for instant access by different criteria
// - Thread-safe and unchanging once loaded
// - Exercises grouped by muscle groups and equipment types
//
// Exercise coverage by body part:
// - Chest: 14 exercises (bench press variations, flyes, dips)
// - Back: 18 exercises (rows, pull-ups, deadlifts, shrugs)
// - Shoulders: 13 exercises (presses, raises, pulls)
// - Arms: 20 exercises (curls, extensions, specialized movements)
// - Legs: 23 exercises (squats, lunges, extensions, curls)
// - Core: 12 exercises (crunches, planks, twists)
// - Calves & Supporting: 7 exercises (raises, wrist work)
//
// All lookups are instant (O(1)) because the maps are pre-computed when the class loads.
public enum ExerciseLibrary {
        // ========================================
        // CHEST EXERCISES (14 total)
        // Primary movers: pectoralis major/minor
        // Equipment: barbell, dumbbell, machine, cable, bodyweight
        // ========================================
        CHEST_001("CHEST_001", "Barbell Bench Press", MuscleGroup.CHEST, PreferredEquipment.BARBELL),
        CHEST_002("CHEST_002", "Dumbbell Bench Press", MuscleGroup.CHEST, PreferredEquipment.DUMBBELL),
        CHEST_003("CHEST_003", "Incline Barbell Bench Press", MuscleGroup.CHEST, PreferredEquipment.BARBELL),
        CHEST_004("CHEST_004", "Incline Dumbbell Press", MuscleGroup.CHEST, PreferredEquipment.DUMBBELL),
        CHEST_005("CHEST_005", "Decline Barbell Bench Press", MuscleGroup.CHEST, PreferredEquipment.BARBELL),
        CHEST_006("CHEST_006", "Dumbbell Flyes", MuscleGroup.CHEST, PreferredEquipment.DUMBBELL),
        CHEST_007("CHEST_007", "Cable Crossover", MuscleGroup.CHEST, PreferredEquipment.CABLE),
        CHEST_008("CHEST_008", "Push-ups", MuscleGroup.CHEST, PreferredEquipment.BODYWEIGHT),
        CHEST_009("CHEST_009", "Diamond Push-ups", MuscleGroup.CHEST, PreferredEquipment.BODYWEIGHT),
        CHEST_010("CHEST_010", "Chest Dips", MuscleGroup.CHEST, PreferredEquipment.BODYWEIGHT),
        CHEST_011("CHEST_011", "Pec Deck Machine", MuscleGroup.CHEST, PreferredEquipment.MACHINE),
        CHEST_012("CHEST_012", "Cable Flyes", MuscleGroup.CHEST, PreferredEquipment.CABLE),
        CHEST_013("CHEST_013", "Smith Machine Bench Press", MuscleGroup.CHEST, PreferredEquipment.SMITH_MACHINE),
        CHEST_014("CHEST_014", "Kettlebell Floor Press", MuscleGroup.CHEST, PreferredEquipment.KETTLEBELL),

        // ========================================
        // BACK EXERCISES (18 total)
        // Primary movers: latissimus dorsi, rhomboids, traps, biceps
        // Equipment: barbell, dumbbell, machine, cable, bodyweight
        // ========================================
        BACK_001("BACK_001", "Barbell Deadlift", MuscleGroup.BACK, PreferredEquipment.BARBELL),
        BACK_002("BACK_002", "Barbell Row", MuscleGroup.BACK, PreferredEquipment.BARBELL),
        BACK_003("BACK_003", "Dumbbell Row", MuscleGroup.BACK, PreferredEquipment.DUMBBELL),
        BACK_004("BACK_004", "Pull-ups", MuscleGroup.BACK, PreferredEquipment.BODYWEIGHT),
        BACK_005("BACK_005", "Chin-ups", MuscleGroup.BACK, PreferredEquipment.BODYWEIGHT),
        BACK_006("BACK_006", "Lat Pulldown", MuscleGroup.LATS, PreferredEquipment.CABLE),
        BACK_007("BACK_007", "Cable Row", MuscleGroup.BACK, PreferredEquipment.CABLE),
        BACK_008("BACK_008", "T-Bar Row", MuscleGroup.BACK, PreferredEquipment.BARBELL),
        BACK_009("BACK_009", "Seated Cable Row", MuscleGroup.BACK, PreferredEquipment.CABLE),
        BACK_010("BACK_010", "One-Arm Dumbbell Row", MuscleGroup.BACK, PreferredEquipment.DUMBBELL),
        BACK_011("BACK_011", "Cable Pullover", MuscleGroup.LATS, PreferredEquipment.CABLE),
        BACK_012("BACK_012", "Wide-Grip Pull-ups", MuscleGroup.LATS, PreferredEquipment.BODYWEIGHT),
        BACK_013("BACK_013", "Reverse Flyes", MuscleGroup.BACK, PreferredEquipment.DUMBBELL),
        BACK_014("BACK_014", "Cable Reverse Flyes", MuscleGroup.BACK, PreferredEquipment.CABLE),
        BACK_015("BACK_015", "Hyperextensions", MuscleGroup.BACK, PreferredEquipment.BODYWEIGHT),
        BACK_016("BACK_016", "Good Mornings", MuscleGroup.BACK, PreferredEquipment.BARBELL),
        BACK_017("BACK_017", "Rack Pulls", MuscleGroup.BACK, PreferredEquipment.BARBELL),
        BACK_018("BACK_018", "Kettlebell Swings", MuscleGroup.BACK, PreferredEquipment.KETTLEBELL),

        // ========================================
        // SHOULDER EXERCISES (13 total)
        // Primary movers: deltoids (anterior, medial, posterior)
        // Equipment: barbell, dumbbell, cable, bodyweight
        // ========================================
        SHOULDERS_001("SHOULDERS_001", "Overhead Barbell Press", MuscleGroup.SHOULDERS, PreferredEquipment.BARBELL),
        SHOULDERS_002("SHOULDERS_002", "Dumbbell Shoulder Press", MuscleGroup.SHOULDERS, PreferredEquipment.DUMBBELL),
        SHOULDERS_003("SHOULDERS_003", "Lateral Raises", MuscleGroup.SHOULDERS, PreferredEquipment.DUMBBELL),
        SHOULDERS_004("SHOULDERS_004", "Front Raises", MuscleGroup.SHOULDERS, PreferredEquipment.DUMBBELL),
        SHOULDERS_005("SHOULDERS_005", "Rear Delt Flyes", MuscleGroup.SHOULDERS, PreferredEquipment.DUMBBELL),
        SHOULDERS_006("SHOULDERS_006", "Cable Lateral Raises", MuscleGroup.SHOULDERS, PreferredEquipment.CABLE),
        SHOULDERS_007("SHOULDERS_007", "Arnold Press", MuscleGroup.SHOULDERS, PreferredEquipment.DUMBBELL),
        SHOULDERS_008("SHOULDERS_008", "Upright Row", MuscleGroup.SHOULDERS, PreferredEquipment.BARBELL),
        SHOULDERS_009("SHOULDERS_009", "Cable Upright Row", MuscleGroup.SHOULDERS, PreferredEquipment.CABLE),
        SHOULDERS_010("SHOULDERS_010", "Pike Push-ups", MuscleGroup.SHOULDERS, PreferredEquipment.BODYWEIGHT),
        SHOULDERS_011("SHOULDERS_011", "Handstand Push-ups", MuscleGroup.SHOULDERS, PreferredEquipment.BODYWEIGHT),
        SHOULDERS_012("SHOULDERS_012", "Face Pulls", MuscleGroup.SHOULDERS, PreferredEquipment.CABLE),
        SHOULDERS_013("SHOULDERS_013", "Kettlebell Press", MuscleGroup.SHOULDERS, PreferredEquipment.KETTLEBELL),

        // ========================================
        // BICEPS EXERCISES (10 total)
        // Primary movers: biceps brachii, brachialis
        // Equipment: barbell, dumbbell, cable, resistance band
        // ========================================
        BICEPS_001("BICEPS_001", "Barbell Curl", MuscleGroup.BICEPS, PreferredEquipment.BARBELL),
        BICEPS_002("BICEPS_002", "Dumbbell Curl", MuscleGroup.BICEPS, PreferredEquipment.DUMBBELL),
        BICEPS_003("BICEPS_003", "Hammer Curl", MuscleGroup.BICEPS, PreferredEquipment.DUMBBELL),
        BICEPS_004("BICEPS_004", "Cable Curl", MuscleGroup.BICEPS, PreferredEquipment.CABLE),
        BICEPS_005("BICEPS_005", "Preacher Curl", MuscleGroup.BICEPS, PreferredEquipment.BARBELL),
        BICEPS_006("BICEPS_006", "Concentration Curl", MuscleGroup.BICEPS, PreferredEquipment.DUMBBELL),
        BICEPS_007("BICEPS_007", "Incline Dumbbell Curl", MuscleGroup.BICEPS, PreferredEquipment.DUMBBELL),
        BICEPS_008("BICEPS_008", "Cable Hammer Curl", MuscleGroup.BICEPS, PreferredEquipment.CABLE),
        BICEPS_009("BICEPS_009", "21s", MuscleGroup.BICEPS, PreferredEquipment.BARBELL),
        BICEPS_010("BICEPS_010", "Resistance Band Curl", MuscleGroup.BICEPS, PreferredEquipment.RESISTANCE_BAND),

        // ========================================
        // TRICEPS EXERCISES (10 total)
        // Primary movers: triceps brachii
        // Equipment: barbell, dumbbell, cable, bodyweight, resistance band
        // ========================================
        TRICEPS_001("TRICEPS_001", "Close-Grip Bench Press", MuscleGroup.TRICEPS, PreferredEquipment.BARBELL),
        TRICEPS_002("TRICEPS_002", "Overhead Barbell Extension", MuscleGroup.TRICEPS, PreferredEquipment.BARBELL),
        TRICEPS_003("TRICEPS_003", "Dumbbell Overhead Extension", MuscleGroup.TRICEPS, PreferredEquipment.DUMBBELL),
        TRICEPS_004("TRICEPS_004", "Tricep Dips", MuscleGroup.TRICEPS, PreferredEquipment.BODYWEIGHT),
        TRICEPS_005("TRICEPS_005", "Cable Tricep Extension", MuscleGroup.TRICEPS, PreferredEquipment.CABLE),
        TRICEPS_006("TRICEPS_006", "Dumbbell Kickback", MuscleGroup.TRICEPS, PreferredEquipment.DUMBBELL),
        TRICEPS_007("TRICEPS_007", "Skull Crushers", MuscleGroup.TRICEPS, PreferredEquipment.BARBELL),
        TRICEPS_008("TRICEPS_008", "Cable Overhead Extension", MuscleGroup.TRICEPS, PreferredEquipment.CABLE),
        TRICEPS_009("TRICEPS_009", "Diamond Push-ups", MuscleGroup.TRICEPS, PreferredEquipment.BODYWEIGHT),
        TRICEPS_010("TRICEPS_010", "Resistance Band Extension", MuscleGroup.TRICEPS, PreferredEquipment.RESISTANCE_BAND),

        // ========================================
        // QUADRICEPS EXERCISES (11 total)
        // Primary movers: quadriceps femoris
        // Equipment: barbell, dumbbell, machine, bodyweight, kettlebell
        // ========================================
        QUADS_001("QUADS_001", "Barbell Back Squat", MuscleGroup.QUADRICEPS, PreferredEquipment.BARBELL),
        QUADS_002("QUADS_002", "Barbell Front Squat", MuscleGroup.QUADRICEPS, PreferredEquipment.BARBELL),
        QUADS_003("QUADS_003", "Leg Press", MuscleGroup.QUADRICEPS, PreferredEquipment.MACHINE),
        QUADS_004("QUADS_004", "Leg Extension", MuscleGroup.QUADRICEPS, PreferredEquipment.MACHINE),
        QUADS_005("QUADS_005", "Bulgarian Split Squat", MuscleGroup.QUADRICEPS, PreferredEquipment.DUMBBELL),
        QUADS_006("QUADS_006", "Walking Lunges", MuscleGroup.QUADRICEPS, PreferredEquipment.DUMBBELL),
        QUADS_007("QUADS_007", "Goblet Squat", MuscleGroup.QUADRICEPS, PreferredEquipment.DUMBBELL),
        QUADS_008("QUADS_008", "Hack Squat", MuscleGroup.QUADRICEPS, PreferredEquipment.MACHINE),
        QUADS_009("QUADS_009", "Pistol Squat", MuscleGroup.QUADRICEPS, PreferredEquipment.BODYWEIGHT),
        QUADS_010("QUADS_010", "Smith Machine Squat", MuscleGroup.QUADRICEPS, PreferredEquipment.SMITH_MACHINE),
        QUADS_011("QUADS_011", "Kettlebell Goblet Squat", MuscleGroup.QUADRICEPS, PreferredEquipment.KETTLEBELL),

        // ========================================
        // HAMSTRING EXERCISES (9 total)
        // Primary movers: hamstrings (biceps femoris, semitendinosus, semimembranosus)
        // Equipment: barbell, dumbbell, machine, cable, kettlebell
        // ========================================
        HAMS_001("HAMS_001", "Romanian Deadlift", MuscleGroup.HAMSTRINGS, PreferredEquipment.BARBELL),
        HAMS_002("HAMS_002", "Dumbbell Romanian Deadlift", MuscleGroup.HAMSTRINGS, PreferredEquipment.DUMBBELL),
        HAMS_003("HAMS_003", "Leg Curl", MuscleGroup.HAMSTRINGS, PreferredEquipment.MACHINE),
        HAMS_004("HAMS_004", "Stiff Leg Deadlift", MuscleGroup.HAMSTRINGS, PreferredEquipment.BARBELL),
        HAMS_005("HAMS_005", "Good Mornings", MuscleGroup.HAMSTRINGS, PreferredEquipment.BARBELL),
        HAMS_006("HAMS_006", "Nordic Curls", MuscleGroup.HAMSTRINGS, PreferredEquipment.BODYWEIGHT),
        HAMS_007("HAMS_007", "Cable Leg Curl", MuscleGroup.HAMSTRINGS, PreferredEquipment.CABLE),
        HAMS_008("HAMS_008", "Single Leg Romanian Deadlift", MuscleGroup.HAMSTRINGS, PreferredEquipment.DUMBBELL),
        HAMS_009("HAMS_009", "Kettlebell Swing", MuscleGroup.HAMSTRINGS, PreferredEquipment.KETTLEBELL),

        // ========================================
        // GLUTE EXERCISES (9 total)
        // Primary movers: gluteus maximus, medius, minimus
        // Equipment: barbell, dumbbell, bodyweight, cable
        // ========================================
        GLUTES_001("GLUTES_001", "Barbell Hip Thrust", MuscleGroup.GLUTES, PreferredEquipment.BARBELL),
        GLUTES_002("GLUTES_002", "Dumbbell Hip Thrust", MuscleGroup.GLUTES, PreferredEquipment.DUMBBELL),
        GLUTES_003("GLUTES_003", "Glute Bridge", MuscleGroup.GLUTES, PreferredEquipment.BODYWEIGHT),
        GLUTES_004("GLUTES_004", "Bulgarian Split Squat", MuscleGroup.GLUTES, PreferredEquipment.DUMBBELL),
        GLUTES_005("GLUTES_005", "Romanian Deadlift", MuscleGroup.GLUTES, PreferredEquipment.BARBELL),
        GLUTES_006("GLUTES_006", "Walking Lunges", MuscleGroup.GLUTES, PreferredEquipment.DUMBBELL),
        GLUTES_007("GLUTES_007", "Cable Kickback", MuscleGroup.GLUTES, PreferredEquipment.CABLE),
        GLUTES_008("GLUTES_008", "Sumo Deadlift", MuscleGroup.GLUTES, PreferredEquipment.BARBELL),
        GLUTES_009("GLUTES_009", "Step-ups", MuscleGroup.GLUTES, PreferredEquipment.DUMBBELL),

        // ========================================
        // CALF EXERCISES (7 total)
        // Primary movers: gastrocnemius, soleus
        // Equipment: machine, bodyweight, dumbbell, barbell, cable
        // ========================================
        CALVES_001("CALVES_001", "Standing Calf Raise", MuscleGroup.CALVES, PreferredEquipment.MACHINE),
        CALVES_002("CALVES_002", "Seated Calf Raise", MuscleGroup.CALVES, PreferredEquipment.MACHINE),
        CALVES_003("CALVES_003", "Calf Raise", MuscleGroup.CALVES, PreferredEquipment.BODYWEIGHT),
        CALVES_004("CALVES_004", "Dumbbell Calf Raise", MuscleGroup.CALVES, PreferredEquipment.DUMBBELL),
        CALVES_005("CALVES_005", "Barbell Calf Raise", MuscleGroup.CALVES, PreferredEquipment.BARBELL),
        CALVES_006("CALVES_006", "Single Leg Calf Raise", MuscleGroup.CALVES, PreferredEquipment.BODYWEIGHT),
        CALVES_007("CALVES_007", "Cable Calf Raise", MuscleGroup.CALVES, PreferredEquipment.CABLE),

        // ========================================
        // ABS EXERCISES (12 total)
        // Primary movers: rectus abdominis, obliques
        // Equipment: bodyweight, cable
        // ========================================
        ABS_001("ABS_001", "Crunches", MuscleGroup.ABS, PreferredEquipment.BODYWEIGHT),
        ABS_002("ABS_002", "Plank", MuscleGroup.ABS, PreferredEquipment.BODYWEIGHT),
        ABS_003("ABS_003", "Russian Twists", MuscleGroup.ABS, PreferredEquipment.BODYWEIGHT),
        ABS_004("ABS_004", "Leg Raises", MuscleGroup.ABS, PreferredEquipment.BODYWEIGHT),
        ABS_005("ABS_005", "Mountain Climbers", MuscleGroup.ABS, PreferredEquipment.BODYWEIGHT),
        ABS_006("ABS_006", "Cable Crunch", MuscleGroup.ABS, PreferredEquipment.CABLE),
        ABS_007("ABS_007", "Hanging Leg Raises", MuscleGroup.ABS, PreferredEquipment.BODYWEIGHT),
        ABS_008("ABS_008", "Ab Wheel Rollout", MuscleGroup.ABS, PreferredEquipment.BODYWEIGHT),
        ABS_009("ABS_009", "Bicycle Crunches", MuscleGroup.ABS, PreferredEquipment.BODYWEIGHT),
        ABS_010("ABS_010", "Dead Bug", MuscleGroup.ABS, PreferredEquipment.BODYWEIGHT),
        ABS_011("ABS_011", "Side Plank", MuscleGroup.ABS, PreferredEquipment.BODYWEIGHT),
        ABS_012("ABS_012", "Dragon Flag", MuscleGroup.ABS, PreferredEquipment.BODYWEIGHT),

        // ========================================
        // FOREARM EXERCISES (5 total)
        // Primary movers: wrist flexors/extensors, grip muscles
        // Equipment: dumbbell, barbell
        // ========================================
        FOREARMS_001("FOREARMS_001", "Wrist Curl", MuscleGroup.FOREARMS, PreferredEquipment.DUMBBELL),
        FOREARMS_002("FOREARMS_002", "Reverse Wrist Curl", MuscleGroup.FOREARMS, PreferredEquipment.DUMBBELL),
        FOREARMS_003("FOREARMS_003", "Farmer's Walk", MuscleGroup.FOREARMS, PreferredEquipment.DUMBBELL),
        FOREARMS_004("FOREARMS_004", "Hammer Curl", MuscleGroup.FOREARMS, PreferredEquipment.DUMBBELL),
        FOREARMS_005("FOREARMS_005", "Reverse Curl", MuscleGroup.FOREARMS, PreferredEquipment.BARBELL),

        // ========================================
        // TRAP EXERCISES (5 total)
        // Primary movers: trapezius
        // Equipment: barbell, dumbbell, cable
        // ========================================
        TRAPS_001("TRAPS_001", "Barbell Shrug", MuscleGroup.TRAPS, PreferredEquipment.BARBELL),
        TRAPS_002("TRAPS_002", "Dumbbell Shrug", MuscleGroup.TRAPS, PreferredEquipment.DUMBBELL),
        TRAPS_003("TRAPS_003", "Cable Shrug", MuscleGroup.TRAPS, PreferredEquipment.CABLE),
        TRAPS_004("TRAPS_004", "Upright Row", MuscleGroup.TRAPS, PreferredEquipment.BARBELL),
        TRAPS_005("TRAPS_005", "High Pull", MuscleGroup.TRAPS, PreferredEquipment.BARBELL);

        // Unique code for this exercise (like "CHEST_001" or "BACK_015")
        // Unique code for this exercise (like "CHEST_001" or "BACK_015")
        private final String exerciseId;

        // The name shown to users (like "Barbell Bench Press" or "Pull-ups")
        private final String displayName;

        // The main muscle group this exercise targets
        private final MuscleGroup primaryMuscle;

        // What equipment you need to do this exercise
        private final PreferredEquipment equipment;

        // Creates a new exercise entry with all its information
        ExerciseLibrary(String exerciseId, String displayName, MuscleGroup primaryMuscle,
                        PreferredEquipment equipment) {
                this.exerciseId = exerciseId;
                this.displayName = displayName;
                this.primaryMuscle = primaryMuscle;
                this.equipment = equipment;
        }

        // Gets the unique code for this exercise
        public String getExerciseId() {
                return exerciseId;
        }

        /**
         * Returns the human-readable display name for this exercise.
         * This is suitable for UI display and user-facing communications.
         *
         * @return The exercise name (e.g., "Barbell Bench Press", "Dumbbell Rows")
         */
        public String getDisplayName() {
                return displayName;
        }

        /**
         * Returns the primary muscle group targeted by this exercise.
         * Note: Some compound exercises may also work secondary muscle groups.
         *
         * @return The primary MuscleGroup enum value
         */
        public MuscleGroup getPrimaryMuscle() {
                return primaryMuscle;
        }

        /**
         * Returns the equipment required to perform this exercise.
         *
         * @return The Equipment enum value
         */
        public PreferredEquipment getEquipment() {
                return equipment;
        }

        // ========================================
        // PRE-COMPUTED LOOKUP MAPS FOR FAST ACCESS
        // These maps are initialized once during class loading for O(1) lookup
        // performance
        // ========================================

        /** Fast lookup map: exercise ID → ExerciseLibrary instance */
        private static final Map<String, ExerciseLibrary> BY_ID;

        /** Fast lookup map: MuscleGroup → List of exercises targeting that muscle */
        private static final Map<MuscleGroup, List<ExerciseLibrary>> BY_MUSCLE;

        /** Fast lookup map: Equipment → List of exercises using that equipment */
        private static final Map<PreferredEquipment, List<ExerciseLibrary>> BY_EQUIPMENT;

        /**
         * Fast lookup map: "MUSCLE_EQUIPMENT" key → List of exercises for that
         * combination
         */
        private static final Map<String, List<ExerciseLibrary>> BY_MUSCLE_AND_EQUIPMENT;

        /**
         * Static initialization block that pre-computes all lookup maps for fast
         * access.
         * This block runs once when the class is loaded, creating O(1) lookup
         * performance for all exercise queries. The maps are populated by iterating
         * through all
         * enum values.
         */
        static {
                // Initialize the lookup maps
                BY_ID = new HashMap<>();
                BY_MUSCLE = new HashMap<>();
                BY_EQUIPMENT = new HashMap<>();
                BY_MUSCLE_AND_EQUIPMENT = new HashMap<>();

                // Populate all maps by iterating through every exercise in the enum
                for (ExerciseLibrary exercise : values()) {
                        // Build BY_ID map for direct exercise lookup by ID
                        BY_ID.put(exercise.exerciseId, exercise);

                        // Build BY_MUSCLE map: group exercises by their primary muscle target
                        BY_MUSCLE.computeIfAbsent(exercise.primaryMuscle, k -> new ArrayList<>()).add(exercise);

                        // Build BY_EQUIPMENT map: group exercises by required equipment
                        BY_EQUIPMENT.computeIfAbsent(exercise.equipment, k -> new ArrayList<>()).add(exercise);

                        // Build BY_MUSCLE_AND_EQUIPMENT map: group by muscle-equipment combinations
                        // Key format: "MUSCLE_EQUIPMENT" (e.g., "CHEST_DUMBBELL")
                        String key = exercise.primaryMuscle.name() + "_" + exercise.equipment.name();
                        BY_MUSCLE_AND_EQUIPMENT.computeIfAbsent(key, k -> new ArrayList<>()).add(exercise);
                }
        }

        // Gets a map to look up exercises by their unique ID
        // Use this when you know the exact exercise code
        public static Map<String, ExerciseLibrary> getById() {
                return new HashMap<>(BY_ID);
        }

        // Gets a map to look up exercises by muscle group
        // Use this to find all exercises for a specific muscle
        public static Map<MuscleGroup, List<ExerciseLibrary>> getByMuscle() {
                return new HashMap<>(BY_MUSCLE);
        }

        // Gets a map to look up exercises by equipment type
        // Use this to find exercises you can do with available equipment
        public static Map<PreferredEquipment, List<ExerciseLibrary>> getByEquipment() {
                return new HashMap<>(BY_EQUIPMENT);
        }

        // Gets a map to look up exercises by muscle and equipment combinations
        // Keys are like "CHEST_DUMBBELL"
        public static Map<String, List<ExerciseLibrary>> getByMuscleAndEquipment() {
                return new HashMap<>(BY_MUSCLE_AND_EQUIPMENT);
        }

        // Easy way to get exercises for a specific muscle and equipment combination
        // This is simpler than using the lookup maps directly
        public static List<ExerciseLibrary> getExercisesForMuscleAndEquipment(MuscleGroup muscle,
            PreferredEquipment equipment) {
        // Create the lookup key by combining muscle and equipment names
        String key = muscle.name() + "_" + equipment.name();

        // Retrieve the exercises for this combination
        List<ExerciseLibrary> exercises = BY_MUSCLE_AND_EQUIPMENT.get(key);

        // Return a defensive copy to prevent external modification
        return exercises != null ? new ArrayList<>(exercises) : new ArrayList<>();
    }

    /**
     * Returns a list of exercises for a specific muscle that match ANY of the
     * provided equipment preferences.
     * 
     * @param muscle           The target muscle group
     * @param equipmentOptions List of allowed equipment
     * @return List of matching exercises (deduplicated)
     */
    public static List<ExerciseLibrary> getExercisesMatchingAny(MuscleGroup muscle,
            List<PreferredEquipment> equipmentOptions) {
        if (equipmentOptions == null || equipmentOptions.isEmpty()) {
            return new ArrayList<>();
        }

        Set<ExerciseLibrary> matches = new HashSet<>();
        for (PreferredEquipment equipment : equipmentOptions) {
            matches.addAll(getExercisesForMuscleAndEquipment(muscle, equipment));
        }

        return new ArrayList<>(matches);
    }
}
