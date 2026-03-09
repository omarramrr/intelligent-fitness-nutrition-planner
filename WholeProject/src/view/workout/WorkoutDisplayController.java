package view.workout;

import app.UserSession;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.shared.SceneNavigator;

import model.user.domain.Profile;
import model.workout.domain.WorkoutDay;
import model.workout.domain.WorkoutExerciseEntry;
import model.workout.domain.WorkoutPlan;

import java.net.URL;
import java.util.ResourceBundle;

public class WorkoutDisplayController implements Initializable {

    @FXML
    private Label fitnessLevelLabel;

    @FXML
    private Label trainingSplitLabel;

    @FXML
    private Label trainingDaysLabel;

    @FXML
    private VBox workoutDaysContainer;

    private Profile currentProfile;
    private WorkoutPlan workoutPlan;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get current profile from UserSession
        currentProfile = UserSession.getInstance().getCurrentProfile();

        if (currentProfile == null) {
            fitnessLevelLabel.setText("ERROR: No user profile available!");
            return;
        }

        // Get workout plan from profile
        workoutPlan = currentProfile.getCurrentWorkoutPlan();

        if (workoutPlan == null) {
            fitnessLevelLabel.setText("ERROR: No workout plan found!");
            trainingSplitLabel.setText("N/A");
            trainingDaysLabel.setText("N/A");
            return;
        }

        // Display the workout plan
        displayPlan();
    }

    /**
     * Sets the workout plan to display (called from WorkoutConfigController)
     * 
     * @param plan The workout plan to display
     */
    public void setWorkoutPlan(WorkoutPlan plan) {
        this.workoutPlan = plan;
        if (workoutPlan != null && currentProfile != null) {
            displayPlan();
        }
    }

    /**
     * Display the workout plan with all days and exercises
     */
    private void displayPlan() {
        // Clear previous content to avoid duplicates
        workoutDaysContainer.getChildren().clear();

        // Set plan info
        fitnessLevelLabel.setText(currentProfile.getFitnessLevel().toString());

        // Get training split from first day (all days should have same split type)
        if (!workoutPlan.getDays().isEmpty()) {
            trainingSplitLabel.setText(workoutPlan.getDays().get(0).getSplitType().getDisplayName());
        }

        trainingDaysLabel.setText(String.valueOf(workoutPlan.getDays().size()));

        // Create a card for each workout day
        for (WorkoutDay day : workoutPlan.getDays()) {
            VBox dayCard = createDayCard(day);
            workoutDaysContainer.getChildren().add(dayCard);
        }
    }

    /**
     * Create a styled card for a workout day with its exercises
     */
    private VBox createDayCard(WorkoutDay day) {
        VBox card = new VBox(15);
        card.getStyleClass().add("day-card");
        card.setPadding(new Insets(20, 25, 20, 25));

        // Day header
        Label dayHeader = new Label("Day " + day.getDayIndex() + " - " + day.getSplitType().getDisplayName());
        dayHeader.getStyleClass().add("day-header");

        // Create table for exercises
        TableView<ExerciseData> exerciseTable = createExerciseTable(day);

        card.getChildren().addAll(dayHeader, exerciseTable);
        return card;
    }

    /**
     * Create a TableView for the exercises in a workout day
     */
    private TableView<ExerciseData> createExerciseTable(WorkoutDay day) {
        TableView<ExerciseData> table = new TableView<>();
        table.getStyleClass().add("exercise-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(day.getExercises().size() * 35 + 50); // Dynamic height based on rows

        // Create columns
        TableColumn<ExerciseData, String> exerciseCol = new TableColumn<>("Exercise");
        exerciseCol.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        exerciseCol.setPrefWidth(300);

        TableColumn<ExerciseData, String> muscleCol = new TableColumn<>("Muscle Group");
        muscleCol.setCellValueFactory(new PropertyValueFactory<>("muscleGroup"));
        muscleCol.setPrefWidth(150);

        TableColumn<ExerciseData, Integer> setsCol = new TableColumn<>("Sets");
        setsCol.setCellValueFactory(new PropertyValueFactory<>("sets"));
        setsCol.setPrefWidth(80);

        TableColumn<ExerciseData, String> repsCol = new TableColumn<>("Reps");
        repsCol.setCellValueFactory(new PropertyValueFactory<>("reps"));
        repsCol.setPrefWidth(100);

        table.getColumns().addAll(exerciseCol, muscleCol, setsCol, repsCol);

        // Populate table with exercise data
        ObservableList<ExerciseData> data = FXCollections.observableArrayList();
        for (WorkoutExerciseEntry exercise : day.getExercises()) {
            String repsDisplay = exercise.getRepsRange().getMinReps() == exercise.getRepsRange().getMaxReps()
                    ? String.valueOf(exercise.getRepsRange().getMinReps())
                    : exercise.getRepsRange().getMinReps() + "-" + exercise.getRepsRange().getMaxReps();

            data.add(new ExerciseData(
                    exercise.getExerciseName(),
                    exercise.getMuscleGroup().getDisplayName(),
                    exercise.getSets(),
                    repsDisplay));
        }

        table.setItems(data);
        return table;
    }

    /**
     * Navigate back to Generate Workout screen
     */
    @FXML
    public void goHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/workout/SceneGenerateWorkout.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Data model class for TableView
     */
    public static class ExerciseData {
        private final SimpleStringProperty exerciseName;
        private final SimpleStringProperty muscleGroup;
        private final SimpleIntegerProperty sets;
        private final SimpleStringProperty reps;

        public ExerciseData(String exerciseName, String muscleGroup, int sets, String reps) {
            this.exerciseName = new SimpleStringProperty(exerciseName);
            this.muscleGroup = new SimpleStringProperty(muscleGroup);
            this.sets = new SimpleIntegerProperty(sets);
            this.reps = new SimpleStringProperty(reps);
        }

        public String getExerciseName() {
            return exerciseName.get();
        }

        public String getMuscleGroup() {
            return muscleGroup.get();
        }

        public int getSets() {
            return sets.get();
        }

        public String getReps() {
            return reps.get();
        }
    }
}
