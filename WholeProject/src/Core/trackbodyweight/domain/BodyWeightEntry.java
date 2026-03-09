package model.trackbodyweight.domain;

import java.time.LocalDate;

public class BodyWeightEntry {
    private final String logId;
    private final String profileId;
    private final LocalDate date;
    private final double weightKg;

    public BodyWeightEntry(String logId, String profileId, LocalDate date, double weightKg) {
        this.logId = logId;
        this.profileId = profileId;
        this.date = date;
        this.weightKg = weightKg;
    }

    public String getLogId() {
        return logId;
    }

    public String getProfileId() {
        return profileId;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getWeightKg() {
        return weightKg;
    }

    @Override
    public String toString() {
        return date + ": " + weightKg + " kg";
    }
}
