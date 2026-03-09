package model.trackbodyweight.domain;

import java.time.LocalDateTime;

public class WeeklyAverageEntry {
    private int weekNumber;
    private double averageWeight;
    private LocalDateTime recordedAt;

    public WeeklyAverageEntry(int weekNumber, double averageWeight, LocalDateTime recordedAt) {
        this.weekNumber = weekNumber;
        this.averageWeight = averageWeight;
        this.recordedAt = recordedAt;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public double getAverageWeight() {
        return averageWeight;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
}
