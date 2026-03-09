package model.workout.service.components.construction.strategies;

import model.workout.enums.MuscleGroup;
import model.workout.service.components.data.MuscleGroups;
import java.util.*;

/**
 * Strategy for Upper/Lower training split.
 * Alternates between upper and lower body muscle groups.
 */
public class UpperLowerStrategy implements SplitStrategy {

    @Override
    public Map<Integer, Set<MuscleGroup>> generateSplit(int trainingDays) {
        Map<Integer, Set<MuscleGroup>> focus = new HashMap<>();
        List<Set<MuscleGroup>> templates = Arrays.asList(
                MuscleGroups.UPPER,
                MuscleGroups.LOWER);

        int safeDays = Math.max(1, trainingDays);
        for (int day = 1; day <= safeDays; day++) {
            int templateIndex = (day - 1) % templates.size();
            focus.put(day, new HashSet<>(templates.get(templateIndex)));
        }
        return focus;
    }
}
