package model.trackbodyweight.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import model.trackbodyweight.repository.BodyWeightRepository;

/**
 * WeightTracker class for tracking weekly average body weight measurements.
 */
public class WeightTracker {
    private ArrayList<Double> dailyBodyWeight;
    private Map<Integer, Double> weeklyAverage;
    private Map<Integer, LocalDateTime> weeklyTimestamps;
    private int currentWeek;
    private final String profileId;
    private final BodyWeightRepository repository;

    /**
     * Constructs a new WeightTracker with a default repository.
     * 
     * @param profileId The ID of the profile this tracker belongs to.
     */
    public WeightTracker(String profileId) {
        this(profileId, new BodyWeightRepository());
    }

    /**
     * Constructs a new WeightTracker with a provided repository (DI).
     * 
     * @param profileId  The ID of the profile this tracker belongs to.
     * @param repository The repository to use for data persistence.
     */
    public WeightTracker(String profileId, BodyWeightRepository repository) {
        this.profileId = profileId;
        this.repository = repository;
        this.dailyBodyWeight = new ArrayList<>();
        this.weeklyAverage = new HashMap<>();
        this.weeklyTimestamps = new HashMap<>();
        this.currentWeek = 1;

        // Load existing history if available
        loadFromRepository();
    }

    private void loadFromRepository() {
        // Load weekly averages
        java.util.List<model.trackbodyweight.domain.WeeklyAverageEntry> history = repository
                .getWeeklyAverageHistory(profileId);
        loadHistory(history);

        // Load daily weights for current partial week could be implemented here if
        // needed,
        // but keeping it simple to match existing behavior which might imply daily
        // weights aren't persisted
        // across restarts unless they form a complete week?
        // Actually, TrackingRepository has logWeight (daily).
        // We should probably allow resuming the current week.
        // For now, let's duplicate the loadHistory logic.
    }

    // ====== Add a daily weight ======
    public Double addDailyWeight(double weight) {
        // 1. Log to DB
        repository.logWeight(profileId, weight, java.time.LocalDate.now());

        // 2. Add to memory
        dailyBodyWeight.add(weight);

        // If 7 entries reached → finish the week
        if (dailyBodyWeight.size() == 7) {
            double avg = calculateAverage();
            LocalDateTime now = LocalDateTime.now();

            // Persist weekly average
            repository.saveWeeklyAverage(profileId, currentWeek, avg, now.toLocalDate());

            weeklyAverage.put(currentWeek, avg);
            weeklyTimestamps.put(currentWeek, now);

            currentWeek++; // Move to next week
            dailyBodyWeight.clear(); // Reset list for new week

            return avg; // Return the average that was just calculated
        }
        return null;
    }

    // ====== Calculate weekly average ======
    private double calculateAverage() {
        double sum = 0;
        for (double w : dailyBodyWeight) {
            sum += w;
        }
        return sum / dailyBodyWeight.size();
    }

    /**
     * Gets the map of weekly averages.
     *
     * @return A map where key is week number and value is average weight.
     */
    public Map<Integer, Double> getWeeklyAverages() {
        return weeklyAverage;
    }

    /**
     * Gets the map of timestamps for when each week was completed.
     *
     * @return A map where key is week number and value is completion timestamp.
     */
    public Map<Integer, LocalDateTime> getWeeklyTimestamps() {
        return weeklyTimestamps;
    }

    /**
     * Gets the timestamp for a specific week.
     *
     * @param weekNumber The week number.
     * @return The timestamp, or null if not found.
     */
    public LocalDateTime getTimestampForWeek(int weekNumber) {
        return weeklyTimestamps.get(weekNumber);
    }

    // ====== Methods for AdaptiveDietManager ======

    /**
     * Returns a copy of the current daily weights being tracked.
     * This allows AdaptiveDietManager to access the current week's data.
     */
    public ArrayList<Double> getDailyWeights() {
        return new ArrayList<>(dailyBodyWeight);
    }

    /**
     * Checks if a full week (7 days) of data has been recorded.
     *
     * @return true if exactly 7 daily weights are present, false otherwise.
     */
    public boolean hasFullWeek() {
        return dailyBodyWeight.size() == 7;
    }

    /**
     * Returns the average of this week's daily weights only.
     * This method calculates the average of the current week being tracked,
     * not the completed weeks stored in the map.
     * If no data exists for this week, returns 0.0.
     */
    public double thisWeekWeeklyAvg() {
        if (dailyBodyWeight.isEmpty()) {
            return 0.0;
        }
        return calculateAverage();
    }

    /**
     * Returns the average of the last completed week.
     *
     * @return The average weight of the previous week, or 0.0 if no weeks are
     *         completed.
     */
    public double getLastCompletedWeekAvg() {
        if (currentWeek <= 1 || weeklyAverage.isEmpty()) {
            return 0.0;
        }
        // Get the most recent completed week (currentWeek - 1)
        return weeklyAverage.getOrDefault(currentWeek - 1, 0.0);
    }

    /**
     * Calculates the average of the two weeks prior to the last completed week.
     * Used as a baseline to determine weight trend.
     * 
     * If the current completed week is N, this returns Avg(N-1, N-2).
     * If N-2 is missing, it returns the value of N-1.
     * If N-1 is missing, it returns 0.0.
     */
    public double getPreviousTwoWeeksAverage() {
        int lastCompletedWeek = currentWeek - 1;
        if (lastCompletedWeek < 2) {
            return 0.0; // Not enough history
        }

        // We want the average of week (lastCompletedWeek - 1) and (lastCompletedWeek -
        // 2)
        // Check if we have at least 1 previous week
        int prev1 = lastCompletedWeek - 1;
        if (!weeklyAverage.containsKey(prev1)) {
            return 0.0;
        }

        double prev1Avg = weeklyAverage.get(prev1);

        int prev2 = lastCompletedWeek - 2;
        if (weeklyAverage.containsKey(prev2)) {
            double prev2Avg = weeklyAverage.get(prev2);
            return (prev1Avg + prev2Avg) / 2.0;
        }

        // If only 1 previous week exists (e.g. we just finished week 2, so we look at
        // week 1)
        return prev1Avg;
    }

    /**
     * Prints all weekly averages in a formatted table to the console.
     */
    public void printWeeklyAverages() {
        if (weeklyAverage.isEmpty()) {
            System.out.println("No weekly averages recorded yet.");
            return;
        }

        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║              WEEKLY BODY WEIGHT AVERAGES                           ║");
        System.out.println("╠════════╦═══════════════╦═══════════════════════════════════════════╣");
        System.out.println("║  Week  ║  Avg Weight   ║           Recorded On                     ║");
        System.out.println("╠════════╬═══════════════╬═══════════════════════════════════════════╣");

        // Sort by week number and print each entry
        for (int week = 1; week < currentWeek; week++) {
            if (weeklyAverage.containsKey(week)) {
                double avg = weeklyAverage.get(week);
                LocalDateTime timestamp = weeklyTimestamps.get(week);

                String weekStr = String.format("   %-4d", week);
                String avgStr = String.format("  %.2f kg  ", avg);
                String timeStr = timestamp != null ? String.format("  %s", timestamp.toString().replace('T', ' '))
                        : "  N/A";

                System.out.printf("║%s║%s║%-39s║%n", weekStr, avgStr, timeStr);
            }
        }

        System.out.println("╚════════╩═══════════════╩═══════════════════════════════════════════╝");
        System.out.printf("Total weeks recorded: %d%n", weeklyAverage.size());
    }

    /**
     * Returns the number of fully completed weeks recorded.
     *
     * @return The count of completed weeks.
     */
    public int getCompletedWeeksCount() {
        return weeklyAverage.size();
    }

    public void loadHistory(java.util.List<model.trackbodyweight.domain.WeeklyAverageEntry> history) {
        if (history == null || history.isEmpty())
            return;

        int maxWeek = 0;
        for (model.trackbodyweight.domain.WeeklyAverageEntry entry : history) {
            weeklyAverage.put(entry.getWeekNumber(), entry.getAverageWeight());
            if (entry.getRecordedAt() != null) {
                weeklyTimestamps.put(entry.getWeekNumber(), entry.getRecordedAt());
            }
            if (entry.getWeekNumber() > maxWeek) {
                maxWeek = entry.getWeekNumber();
            }
        }
        // Set current week to next one
        if (maxWeek > 0) {
            currentWeek = maxWeek + 1;
        }
    }
}
