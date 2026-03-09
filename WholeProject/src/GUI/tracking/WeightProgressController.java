package view.tracking;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import app.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import model.user.domain.Profile;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

public class WeightProgressController implements Initializable {

    @FXML
    private TableView<WeeklyWeightData> weeklyTable;

    @FXML
    private TableColumn<WeeklyWeightData, Integer> weekColumn;

    @FXML
    private TableColumn<WeeklyWeightData, String> weightColumn;

    @FXML
    private TableColumn<WeeklyWeightData, String> dateColumn;

    @FXML
    private Label currentWeekLabel;

    private Profile currentProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get current profile from UserSession
        currentProfile = UserSession.getInstance().getCurrentProfile();

        if (currentProfile == null) {
            currentWeekLabel.setText("ERROR: No user profile available!");
            return;
        }

        // Set up table columns
        weekColumn.setCellValueFactory(new PropertyValueFactory<>("weekNumber"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("averageWeight"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateRecorded"));

        // Load weight data
        loadWeightData();
    }

    /**
     * Load weight tracking data from the profile's WeightTracker
     */
    private void loadWeightData() {
        ObservableList<WeeklyWeightData> data = FXCollections.observableArrayList();

        // Get weekly averages from WeightTracker
        Map<Integer, Double> weeklyAverages = currentProfile.getWeightTracker().getWeeklyAverages();
        Map<Integer, LocalDateTime> weeklyTimestamps = currentProfile.getWeightTracker().getWeeklyTimestamps();

        // Populate table data
        for (Map.Entry<Integer, Double> entry : weeklyAverages.entrySet()) {
            int weekNumber = entry.getKey();
            double avgWeight = entry.getValue();
            LocalDateTime timestamp = weeklyTimestamps.get(weekNumber);

            String formattedWeight = String.format("%.2f kg", avgWeight);
            String formattedDate = timestamp != null
                    ? timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    : "N/A";

            data.add(new WeeklyWeightData(weekNumber, formattedWeight, formattedDate));
        }

        weeklyTable.setItems(data);

        // Update current week progress
        updateCurrentWeekProgress();
    }

    /**
     * Update the current week progress label
     */
    private void updateCurrentWeekProgress() {
        int currentWeekDays = currentProfile.getWeightTracker().getDailyWeights().size();

        if (currentWeekDays == 0) {
            currentWeekLabel.setText("No data recorded for current week yet. Start tracking to see progress!");
        } else if (currentWeekDays < 7) {
            int daysRemaining = 7 - currentWeekDays;
            currentWeekLabel.setText(
                    String.format("📅 Days recorded this week: %d/7 | %d more day(s) needed to complete the week",
                            currentWeekDays, daysRemaining));
        } else {
            currentWeekLabel.setText("🎉 Week complete! Your weekly average has been calculated.");
        }

        // Show message if no weeks completed yet
        if (weeklyTable.getItems().isEmpty()) {
            currentWeekLabel.setText(currentWeekLabel.getText() +
                    "\n\n💡 Complete a full week (7 days) to see your first weekly average!");
        }
    }

    /**
     * Navigate back to Track Body Weight screen
     */
    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/tracking/SceneTrackBodyWeight.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Data model class for TableView
     */
    public static class WeeklyWeightData {
        private final SimpleIntegerProperty weekNumber;
        private final SimpleStringProperty averageWeight;
        private final SimpleStringProperty dateRecorded;

        public WeeklyWeightData(int weekNumber, String averageWeight, String dateRecorded) {
            this.weekNumber = new SimpleIntegerProperty(weekNumber);
            this.averageWeight = new SimpleStringProperty(averageWeight);
            this.dateRecorded = new SimpleStringProperty(dateRecorded);
        }

        public int getWeekNumber() {
            return weekNumber.get();
        }

        public String getAverageWeight() {
            return averageWeight.get();
        }

        public String getDateRecorded() {
            return dateRecorded.get();
        }
    }
}
