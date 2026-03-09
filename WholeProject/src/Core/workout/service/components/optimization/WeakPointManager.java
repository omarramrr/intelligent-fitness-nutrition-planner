package model.workout.service.components.optimization;

import model.workout.enums.MuscleGroup;
import model.workout.service.interfaces.IWeakPointManager;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Handles weak point logic.
 */
public class WeakPointManager implements IWeakPointManager {

    public WeakPointManager() {
    }

    /*
     * Takes a base map of muscle → volume (sets), detects weak muscles, and adjusts
     * their volume upward.
     */
    // Increases volume for weak point muscles.
    public Map<MuscleGroup, Integer> applyPriority(Map<MuscleGroup, Integer> baseVolumes,
            Set<MuscleGroup> weakPoints) {
        if (baseVolumes == null)
            throw new IllegalArgumentException("baseVolumes must not be null");

        // Create a copy of the base volumes.
        Map<MuscleGroup, Integer> adjusted = new EnumMap<>(baseVolumes);

        // Clean up the weak points set.
        Set<MuscleGroup> sanitized = sanitizeWeakPoints(weakPoints);

        /*
         * This ensures
         * If weakPoints is null → returns empty set
         * If not null → returns a safe copy
         */
        // Loop through each weak muscle.
        for (MuscleGroup muscle : sanitized) {
            // Get current volume.
            Integer currentObj = adjusted.get(muscle);
            int current = (currentObj == null) ? 8 : currentObj;
            /*
             * This means:
             * If the base volume exists → use it
             * If it does NOT exist → default to 8 sets
             */

            // Increase volume by 25%.
            int bumped = (int) Math.round(current * 1.25);
            /*
             * Why 25%?
             * That’s a common increase in hypertrophy programming to prioritize a muscle
             * without causing overtraining
             */

            /*
             * baseVolumes = {CHEST=12, BACK=14, LEGS=16}
             * weakPoints = {CHEST}
             */

            adjusted.put(muscle, bumped);
            // This replaces the old volume with the increased one.
        }
        return adjusted;

        /*
         * baseVolumes = {CHEST=12, BACK=14, LEGS=16}
         * weakPoints = {CHEST}
         */
    }

    // Creates a safe copy of the weak points set.
    public Set<MuscleGroup> sanitizeWeakPoints(Set<MuscleGroup> weakPoints) {
        if (weakPoints == null) {
            return new HashSet<>();
        }
        return new HashSet<>(weakPoints);
    }
}
