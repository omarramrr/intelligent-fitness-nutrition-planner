package view.workout;

import app.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import view.shared.SceneNavigator;

import model.user.domain.Profile;
import model.workout.domain.PlanContext;
import model.workout.domain.WorkoutPlan;
import model.workout.enums.MuscleGroup;
import model.workout.enums.PreferredEquipment;
import model.workout.enums.TrainingSplit;
import model.workout.service.WorkoutPlanGenerator;
import model.workout.service.WorkoutService;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class WorkoutConfigController implements Initializable {

    @FXML
    private ChoiceBox<Integer> trainingDaysChoiceBox;

    @FXML
    private ChoiceBox<String> splitChoiceBox;

    @FXML
    private ListView<String> weakMusclesListView;

    @FXML
    private ListView<String> equipmentListView;

    @FXML
    private Button generateButton;

    @FXML
    private Label messageLabel;

    private Profile currentProfile;

    // Mappings for UI Strings back to Enums
    private final Map<String, TrainingSplit> splitMap = new HashMap<>();
    private final Map<String, MuscleGroup> muscleMap = new LinkedHashMap<>(); // Linked to preserve order
    private final Map<String, PreferredEquipment> equipmentMap = new LinkedHashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentProfile = UserSession.getInstance().getCurrentProfile();

        if (currentProfile == null) {
            showMessage("❌ Error: No user profile loaded. Please login again.", true);
            generateButton.setDisable(true);
            return;
        }

        // 1. Initialize Training Days
        trainingDaysChoiceBox.setItems(FXCollections.observableArrayList(2, 3, 4, 5, 6));
        trainingDaysChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateSplitOptions(newVal);
                validateSelection();
            }
        });

        // 2. Initialize Split Options (Empty initially)
        splitChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> validateSelection());

        // 3. Initialize Weak Muscles (Multi-select)
        weakMusclesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (MuscleGroup mg : MuscleGroup.values()) {
            muscleMap.put(mg.getDisplayName(), mg);
        }
        weakMusclesListView.setItems(FXCollections.observableArrayList(muscleMap.keySet()));

        // 4. Initialize Equipment (Multi-select)
        equipmentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (PreferredEquipment pe : PreferredEquipment.values()) {
            equipmentMap.put(pe.getDisplayName(), pe);
        }
        equipmentListView.setItems(FXCollections.observableArrayList(equipmentMap.keySet()));

        // Pre-select some common equipment defaults if needed, or leave empty
        // For now, we leave empty to force user choice or handle "no selection" as
        // "all/default" in backend?
        // Let's select 'Bodyweight' by default to ensure at least one thing is checked
        equipmentListView.getSelectionModel().select(PreferredEquipment.BODYWEIGHT.getDisplayName());

        // Initial validation
        validateSelection();
    }

    /**
     * Strictly enforces split availability based on training days.
     */
    private void updateSplitOptions(int days) {
        ObservableList<String> validSplits = FXCollections.observableArrayList();
        splitMap.clear();

        // Rules based on requirements
        if (days == 2 || days == 4 || days == 6) {
            // Even days: All splits often allowed, but let's follow the specific
            // constraints
            // Requirement said: [2, 4, 6] -> ANTERIOR_POSTERIOR, FULL_BODY, UPPER_LOWER
            addSplitOption(validSplits, TrainingSplit.FULL_BODY);
            addSplitOption(validSplits, TrainingSplit.UPPER_LOWER);
            addSplitOption(validSplits, TrainingSplit.ANTERIOR_POSTERIOR);
        } else if (days == 3 || days == 5) {
            // Odd days: Full Body only (as per requirement example)
            addSplitOption(validSplits, TrainingSplit.FULL_BODY);
        }

        String currentSelection = splitChoiceBox.getValue();
        splitChoiceBox.setItems(validSplits);

        // Retain selection if valid, otherwise clear
        if (currentSelection != null && validSplits.contains(currentSelection)) {
            splitChoiceBox.setValue(currentSelection);
        } else {
            splitChoiceBox.getSelectionModel().clearSelection();
            if (!validSplits.isEmpty()) {
                splitChoiceBox.setValue(validSplits.get(0)); // Default to first valid
            }
        }
    }

    private void addSplitOption(ObservableList<String> list, TrainingSplit split) {
        list.add(split.getDisplayName());
        splitMap.put(split.getDisplayName(), split);
    }

    /**
     * strict validation to enable/disable Generate button
     */
    private void validateSelection() {
        boolean daysSelected = trainingDaysChoiceBox.getValue() != null;
        boolean splitSelected = splitChoiceBox.getValue() != null;

        boolean isValid = daysSelected && splitSelected;

        generateButton.setDisable(!isValid);

        if (!isValid) {
            showMessage("Please select Training Days and a Split Style.", false);
        } else {
            showMessage("", false); // Clear error
        }
    }

    @FXML
    public void generatePlan(ActionEvent event) {
        try {
            // 1. Gather Data
            int days = trainingDaysChoiceBox.getValue();
            String splitName = splitChoiceBox.getValue();
            TrainingSplit split = splitMap.get(splitName);

            // Map selected strings back to Enums
            Set<MuscleGroup> weakMuscles = weakMusclesListView.getSelectionModel().getSelectedItems().stream()
                    .map(muscleMap::get)
                    .collect(Collectors.toSet());

            Set<PreferredEquipment> equipment = equipmentListView.getSelectionModel().getSelectedItems().stream()
                    .map(equipmentMap::get)
                    .collect(Collectors.toSet());

            // 2. Build Context
            // Convert flat equipment set to Map for all muscle groups (User has this
            // equipment available for everything)
            Map<MuscleGroup, List<PreferredEquipment>> equipMap = new EnumMap<>(MuscleGroup.class);
            List<PreferredEquipment> equipList = new ArrayList<>(equipment);
            for (MuscleGroup mg : MuscleGroup.values()) {
                equipMap.put(mg, equipList);
            }

            PlanContext context = PlanContext.builder()
                    .profile(currentProfile)
                    .split(split)
                    .trainingDays(days)
                    .weakPoints(weakMuscles)
                    .equipmentPreferences(equipMap)
                    .build();

            // 3. Generate
            WorkoutService workoutService = new WorkoutService();
            WorkoutPlanGenerator generator = new WorkoutPlanGenerator(context, workoutService);
            WorkoutPlan plan = generator.generate();

            // 4. Save & Navigate
            currentProfile.setCurrentWorkoutPlan(plan);
            currentProfile.setLastWorkoutContext(context);

            // Persist to Database (Mirroring Main.generateWorkoutPlanFlow)
            model.workout.repository.WorkoutRepository workoutRepo = new model.workout.repository.WorkoutRepository();
            String planId = workoutRepo.saveWorkoutPlan(plan, currentProfile);
            if (planId != null) {
                System.out.println("💾 Plan saved to Database (ID: " + planId + ")");
            } else {
                System.err.println("⚠️ Could not save plan to Database.");
            }

            model.workout.repository.PlanContextRepository contextRepo = new model.workout.repository.PlanContextRepository();
            String contextId = contextRepo.saveContext(context);
            if (contextId != null) {
                System.out.println("💾 Plan Settings saved to Database");
            }

            showMessage("✅ Plan Generated! Redirecting...", false);
            messageLabel.getStyleClass().add("success-message");

            // Navigate to Display Page with the new plan
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/workout/SceneWorkoutDisplay.fxml"));
            Parent root = loader.load();

            WorkoutDisplayController controller = loader.getController();
            controller.setWorkoutPlan(plan); // Changed from newPlan to plan

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("❌ Error: " + e.getMessage(), true);
        }
    }

    // Removed goToDisplay method as its logic is now in generatePlan

    @FXML
    public void goBack(ActionEvent event) {
        try {
            // Go back to Generation Start Screen
            Parent root = FXMLLoader.load(getClass().getResource("/view/workout/SceneGenerateWorkout.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.navigateTo(stage, new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String msg, boolean isError) {
        messageLabel.setText(msg);
        messageLabel.getStyleClass().clear();
        if (isError) {
            messageLabel.getStyleClass().add("error-message");
        } else {
            messageLabel.getStyleClass().add("label-text"); // Neutral
        }
    }
}
