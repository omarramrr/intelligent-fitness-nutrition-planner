package model.workout.domain;

public class RepsRange {
    private final int minReps;
    private final int maxReps;

    public RepsRange(int minReps, int maxReps) {
        if (minReps < 1)
            throw new IllegalArgumentException("Min reps must be >= 1");
        if (maxReps < 1)
            throw new IllegalArgumentException("Max reps must be >= 1");
        if (minReps > maxReps)
            throw new IllegalArgumentException("Min reps cannot exceed max reps");
        this.minReps = minReps;
        this.maxReps = maxReps;
    }

    public int getMinReps() {
        return minReps;
    }

    public int getMaxReps() {
        return maxReps;
    }
}
