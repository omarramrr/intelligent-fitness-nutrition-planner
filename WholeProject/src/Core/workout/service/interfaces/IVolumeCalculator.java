package model.workout.service.interfaces;

import model.user.domain.Profile;
import model.workout.enums.MuscleGroup;
import model.workout.enums.TrainingSplit;
import java.util.Map;
import java.util.Set;

/**
 * Interface for calculating training volume.
 */
public interface IVolumeCalculator {

        /**
         * Calculates total weekly sets per muscle based on profile and goals.
         */
        Map<MuscleGroup, Integer> calculateWeeklyVolume(Profile profile, Set<MuscleGroup> weakPoints,
                        TrainingSplit split, int trainingDays);

        /**
         * Splits the weekly volume into daily sessions.
         */
        Map<Integer, Map<MuscleGroup, Integer>> allocateDailyVolume(
                        Map<Integer, Set<MuscleGroup>> dayMuscleFocus,
                        Map<MuscleGroup, Integer> weeklyVolumes,
                        int trainingDays,
                        Set<MuscleGroup> weakPoints);

        /**
         * Determines the number of sets per day for a muscle group.
         */
        int determineDailySets(int trainingDays, boolean isWeakPoint);
}
