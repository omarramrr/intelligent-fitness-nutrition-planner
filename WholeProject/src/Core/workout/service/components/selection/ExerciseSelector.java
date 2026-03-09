package model.workout.service.components.selection;

import model.workout.enums.ExerciseLibrary;
import model.workout.enums.MuscleGroup;
import model.workout.enums.PreferredEquipment;
import model.workout.service.interfaces.IExerciseSelector;
import java.util.*;

/**
 * Handles all logic related to selecting specific exercises.
 */
public class ExerciseSelector implements IExerciseSelector {
    private static final List<PreferredEquipment> DEFAULT_EQUIPMENT_PRIORITY = Arrays.asList(
            PreferredEquipment.BARBELL, PreferredEquipment.DUMBBELL, PreferredEquipment.MACHINE,
            PreferredEquipment.CABLE, PreferredEquipment.BODYWEIGHT);

    public ExerciseSelector() {
    }

    public List<ExerciseLibrary> selectExercises(MuscleGroup group, List<PreferredEquipment> preferences,
            RotationTracker rotationTracker, int limit, int currentDay) {

        // If the user didn't provide any equipment preferences, we use our default
        // list.
        List<PreferredEquipment> equipmentPriority = (preferences == null || preferences.isEmpty())
                ? DEFAULT_EQUIPMENT_PRIORITY
                : preferences;

        Set<String> seen = new HashSet<>();
        List<ExerciseLibrary> candidates = new ArrayList<>();

        // --- PASS 1: Strict Rotation (Avoid used exercises) ---
        // Try to find exercises that match equipment AND haven't been used.
        for (PreferredEquipment equipment : equipmentPriority) {
            List<ExerciseLibrary> possibleExercises = ExerciseLibrary.getExercisesForMuscleAndEquipment(group,
                    equipment);
            addCandidates(candidates, seen, possibleExercises, rotationTracker, group, limit, true); // strict=true
            if (candidates.size() >= limit)
                return candidates;
        }

        // If needed, check ALL exercises for this muscle (strict)
        if (candidates.size() < limit) {
            List<ExerciseLibrary> allForMuscle = ExerciseLibrary.getByMuscle().getOrDefault(group, new ArrayList<>());
            addCandidates(candidates, seen, allForMuscle, rotationTracker, group, limit, true); // strict=true
        }

        if (candidates.size() >= limit)
            return candidates;

        // --- PASS 2: Fallback (Allow used exercises) ---
        // If we still don't have enough, relax the rotation constraint.
        for (PreferredEquipment equipment : equipmentPriority) {
            List<ExerciseLibrary> possibleExercises = ExerciseLibrary.getExercisesForMuscleAndEquipment(group,
                    equipment);
            addCandidates(candidates, seen, possibleExercises, rotationTracker, group, limit, false); // strict=false
            if (candidates.size() >= limit)
                return candidates;
        }

        // Final fallback: ALL exercises (relaxed)
        if (candidates.size() < limit) {
            List<ExerciseLibrary> allForMuscle = ExerciseLibrary.getByMuscle().getOrDefault(group, new ArrayList<>());
            addCandidates(candidates, seen, allForMuscle, rotationTracker, group, limit, false); // strict=false
        }

        // Trim if we somehow got too many
        if (candidates.size() > limit) {
            return new ArrayList<>(candidates.subList(0, limit));
        }

        return candidates;
    }

    // Helper method to check a list of exercises and add valid ones to our
    // candidates list.
    private void addCandidates(List<ExerciseLibrary> candidates, Set<String> seen,
            List<ExerciseLibrary> source,
            RotationTracker tracker, MuscleGroup group, int limit, boolean strictRotation) {

        for (ExerciseLibrary ex : source) {
            if (candidates.size() >= limit)
                return;
            if (seen.contains(ex.getExerciseId()))
                continue;

            // In strict mode, skip if used. In non-strict (fallback), ignore usage.
            if (strictRotation && tracker != null && tracker.isUsed(ex.getExerciseId(), group)) {
                continue;
            }

            seen.add(ex.getExerciseId());
            candidates.add(ex);
        }
    }

}
