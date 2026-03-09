package model.workout.service.components.construction.strategies;

import model.workout.enums.MuscleGroup;
import java.util.*;

/**
 * Strategy for Full Body training split.
 * Cycles through 3 different full-body templates.
 */
public class FullBodyStrategy implements SplitStrategy {

    @Override
    public Map<Integer, Set<MuscleGroup>> generateSplit(int trainingDays) {
        Map<Integer, Set<MuscleGroup>> focus = new HashMap<>();
        List<Set<MuscleGroup>> templates = buildFullBodyCycle();

        int safeDays = Math.max(1, trainingDays);
        for (int day = 1; day <= safeDays; day++) {
            int templateIndex = (day - 1) % templates.size();
            focus.put(day, new HashSet<>(templates.get(templateIndex)));
        }
        return focus;
    }

    private List<Set<MuscleGroup>> buildFullBodyCycle() {
        List<Set<MuscleGroup>> cycle = new ArrayList<>();

        // Day 1: Push focus + Abs
        Set<MuscleGroup> day1 = new HashSet<>();
        day1.add(MuscleGroup.CHEST);
        day1.add(MuscleGroup.SHOULDERS);
        day1.add(MuscleGroup.QUADRICEPS);
        day1.add(MuscleGroup.ABS);
        cycle.add(day1);

        // Day 2: Pull focus
        Set<MuscleGroup> day2 = new HashSet<>();
        day2.add(MuscleGroup.BACK);
        day2.add(MuscleGroup.LATS);
        day2.add(MuscleGroup.HAMSTRINGS);
        day2.add(MuscleGroup.GLUTES);
        day2.add(MuscleGroup.FOREARMS);
        cycle.add(day2);

        // Day 3: Mixed + Arms
        Set<MuscleGroup> day3 = new HashSet<>();
        day3.add(MuscleGroup.CHEST);
        day3.add(MuscleGroup.BACK);
        day3.add(MuscleGroup.CALVES);
        day3.add(MuscleGroup.ABS);
        day3.add(MuscleGroup.BICEPS);
        day3.add(MuscleGroup.TRICEPS);
        cycle.add(day3);

        return cycle;
    }
}
