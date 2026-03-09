package model.workout.service.components.optimization;

import model.workout.enums.MuscleGroup;
import model.workout.enums.TrainingSplit;
import model.workout.service.interfaces.IFrequencyManager;
import java.util.*;

/**
 * Handles frequency calculations and scheduling.
 */
public class FrequencyManager implements IFrequencyManager {
    private final WeakPointManager weakPointManager;

    public FrequencyManager(WeakPointManager weakPointManager) {
        this.weakPointManager = weakPointManager;
    }

    // Calculates how many times each muscle should be hit per week.
    public Map<MuscleGroup, Integer> deriveFrequencyTargets(TrainingSplit split, int trainingDays,
            Set<MuscleGroup> weakPoints) {
        EnumMap<MuscleGroup, Integer> targets = new EnumMap<>(MuscleGroup.class);

        // Ensure we have at least 1 training day.
        int safeDays = Math.max(1, trainingDays);

        // Calculate a base frequency. Roughly every 2-3 days.
        // If training 4 days, base is 3. If 2 days, base is 2.
        int base = safeDays / 2 + 1;

        // Clamp the base frequency between 2 and 3.
        if (base < 2)
            base = 2;
        if (base > 3)
            base = 3;

        // Clean up the weak points set.
        Set<MuscleGroup> sanitizedWeak = weakPointManager.sanitizeWeakPoints(weakPoints);

        // Loop through every muscle group to set its target.
        for (MuscleGroup muscle : MuscleGroup.values()) {
            int freq = base;

            // Full body splits hit everything more often, so we bump frequency up by 1 (max
            // 3).
            if (split == TrainingSplit.FULL_BODY) {
                freq = freq + 1;
                if (freq > 3)
                    freq = 3;
            }

            // If it's a weak point, we want to hit it more often.
            if (sanitizedWeak.contains(muscle)) {
                freq = freq + 1;
                // But we can't train it more days than we actually go to the gym.
                if (freq > safeDays)
                    freq = safeDays;
            }

            // Ensure frequency is at least 1.
            if (freq < 1)
                freq = 1;

            targets.put(muscle, freq);
        }
        return targets;
    }

    // Tries to add extra muscle sessions to days to meet the frequency targets.
    public Map<Integer, Set<MuscleGroup>> applyFrequencyTargets(Map<Integer, Set<MuscleGroup>> baseFocus,
            Map<MuscleGroup, Integer> frequencyTargets, int minSpacingDays) {

        // Create a copy of the base schedule so we don't modify the original.
        Map<Integer, Set<MuscleGroup>> adjusted = new HashMap<>();

        // This map tracks which days each muscle is currently scheduled for.
        EnumMap<MuscleGroup, List<Integer>> scheduled = new EnumMap<>(MuscleGroup.class);

        // Initialize our working structures with the base schedule.
        for (Map.Entry<Integer, Set<MuscleGroup>> entry : baseFocus.entrySet()) {
            Integer day = entry.getKey();
            // Copy the set of muscles for this day.
            Set<MuscleGroup> muscles = new HashSet<>(entry.getValue());
            adjusted.put(day, muscles);

            // Record that these muscles are trained on this day.
            for (MuscleGroup m : muscles) {
                // Get the list of days for this muscle, creating it if it doesn't exist.
                List<Integer> daysList = scheduled.get(m);
                if (daysList == null) {
                    daysList = new ArrayList<>();
                    scheduled.put(m, daysList);
                }
                daysList.add(day);
            }
        }

        // Ensure spacing is at least 1 day.
        int spacing = minSpacingDays;
        if (spacing < 1)
            spacing = 1;

        // Loop through each muscle and its target frequency.
        for (Map.Entry<MuscleGroup, Integer> entry : frequencyTargets.entrySet()) {
            MuscleGroup muscle = entry.getKey();
            int target = entry.getValue();

            // Get the days currently assigned to this muscle.
            List<Integer> assignedDays = scheduled.get(muscle);
            if (assignedDays == null) {
                assignedDays = new ArrayList<>();
                scheduled.put(muscle, assignedDays);
            }

            // We use a safety counter to prevent infinite loops if we can't fit the muscle
            // anywhere.
            int attempts = 0;
            int guardLimit = 50; // Arbitrary limit, should be enough.

            // Keep trying to add days until we reach the target frequency.
            while (assignedDays.size() < target && attempts < guardLimit) {
                attempts++;

                // Find a day where we can add this muscle without violating spacing rules.
                int candidate = findInsertionDay(adjusted, assignedDays, spacing);

                if (candidate != -1) {
                    // Found a valid day! Add the muscle to that day.
                    adjusted.get(candidate).add(muscle);
                    // Record that the muscle is now trained on this day.
                    assignedDays.add(candidate);
                } else {
                    // No valid day found, stop trying for this muscle.
                    break;
                }
            }
        }
        return adjusted;
    }

    // Helper to find a day to add a muscle session.
    private int findInsertionDay(Map<Integer, Set<MuscleGroup>> focus, List<Integer> assignedDays,
            int spacing) {
        // Loop through all available training days.
        for (Integer day : focus.keySet()) {
            // If the muscle is already trained on this day, skip it.
            boolean alreadyAssigned = false;
            for (Integer assigned : assignedDays) {
                if (assigned.equals(day)) {
                    alreadyAssigned = true;
                    break;
                }
            }
            if (alreadyAssigned)
                continue;

            // Check if this day is too close to any existing session for this muscle.
            boolean respects = true;
            for (Integer existing : assignedDays) {
                int diff = existing - day;
                // We want the absolute difference.
                if (diff < 0)
                    diff = -diff;

                if (diff < spacing) {
                    respects = false;
                    break;
                }
            }

            // If spacing is respected, this day is a good candidate.
            if (respects)
                return day;
        }
        // No suitable day found.
        return -1;
    }
}
