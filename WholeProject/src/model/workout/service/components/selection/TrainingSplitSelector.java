package model.workout.service.components.selection;

import model.workout.enums.TrainingSplit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Logic for selecting valid workout splits based on training frequency.
 * 
 * This class encapsulates the rules for determining allowed splits
 * based on the user's available training days.
 */
public class TrainingSplitSelector {

    private final int trainingDays;

    /**
     * Constructor that initializes the selector with the user's training days.
     * 
     * @param trainingDays Number of days the user can train per week.
     */
    public TrainingSplitSelector(int trainingDays) {
        this.trainingDays = trainingDays;
    }

    /**
     * Determines the allowed training splits based on the stored training days.
     * 
     * Logic:
     * 1. Odd number of days -> Only FULL_BODY is allowed.
     * 2. Even number of days -> All splits (Anterior/Posterior, Upper/Lower, Full
     * Body) are allowed.
     * 
     * @return List of valid TrainingSplit options.
     */
    public List<TrainingSplit> determineSplit() {
        List<TrainingSplit> allowed = new ArrayList<>();

        if (trainingDays % 2 != 0) {
            // Rule 1: Odd number of days -> Only FULL_BODY allowed.
            allowed.add(TrainingSplit.FULL_BODY);
        } else {
            // Rule 2: Even number of days -> All splits allowed.
            allowed.addAll(Arrays.asList(TrainingSplit.values()));
        }
        return allowed;
    }
}
