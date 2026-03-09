package model.workout.service.components.optimization;

import model.workout.enums.MuscleGroup;
import model.workout.service.components.data.MuscleGroups;
import model.workout.service.interfaces.IRecoveryManager;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles recovery rules and spacing.
 */
public class RecoveryManager implements IRecoveryManager {

    public RecoveryManager() {
    }

    // Checks if high-stress muscles are trained too frequently.
    // A map of Day → Set of muscles trained on that day

    public void enforceSpacing(Map<Integer, Set<MuscleGroup>> dayMuscleFocus, int minHoursBetween) {
        if (dayMuscleFocus == null)
            throw new IllegalArgumentException("dayMuscleFocus must not be null");
        if (minHoursBetween < 0)
            throw new IllegalArgumentException("minHoursBetween must be >= 0");

        // Convert hours to days (rounding down, min 1 day).
        int minDaySpacing = minHoursBetween / 24;
        if (minDaySpacing < 1)
            minDaySpacing = 1;

        EnumMap<MuscleGroup, Integer> lastAppearance = new EnumMap<>(MuscleGroup.class);

        // We need to process days in order.
        // Since we can't use Collections.sort, we'll extract keys and sort them
        // manually.
        List<Integer> sortedDays = new ArrayList<>(dayMuscleFocus.keySet());

        // Manual Bubble Sort implementation to sort the days.
        int n = sortedDays.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sortedDays.get(j) > sortedDays.get(j + 1)) {
                    // Swap temp and sortedDays[i]
                    int temp = sortedDays.get(j);
                    sortedDays.set(j, sortedDays.get(j + 1));
                    sortedDays.set(j + 1, temp);
                }
            }
        }

        // Now iterate through the sorted days.
        for (Integer day : sortedDays) {
            Set<MuscleGroup> muscles = dayMuscleFocus.get(day);
            if (muscles == null)
                continue;

            for (MuscleGroup muscle : muscles) {
                // We only care about high-stress muscles (like legs and back).
                if (MuscleGroups.HIGH_STRESS_MUSCLES.contains(muscle)) {
                    Integer prevDay = lastAppearance.get(muscle);

                    // If we've seen this muscle before, check the gap.
                    if (prevDay != null) {
                        int gap = day - prevDay;
                        if (gap < minDaySpacing) {
                            throw new IllegalStateException("Muscle " + muscle + " scheduled too soon (day " + day
                                    + " vs " + prevDay + ")");
                        }
                    }
                    // Update the last seen day for this muscle.
                    lastAppearance.put(muscle, day);
                }
            }
        }
    }
}
