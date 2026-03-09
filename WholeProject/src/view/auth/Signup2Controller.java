package view.auth;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import app.UserManager;
import app.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import view.shared.SceneNavigator;
import model.user.domain.Profile;
import model.user.domain.User;
import model.user.enums.FitnessLevel;
import model.user.enums.Gender;
import model.user.enums.GoalType;

public class Signup2Controller implements Initializable {

    @FXML
    private ChoiceBox<String> genderChoiceBox;

    @FXML
    private TextField birthdateField;

    @FXML
    private TextField weightField;

    @FXML
    private TextField heightField;

    @FXML
    private ChoiceBox<String> fitnessLevelChoiceBox;

    @FXML
    private ChoiceBox<String> goalChoiceBox;

    @FXML
    private Label messageLabel;

    // Data passed from Step 1
    private String username;
    private String email;
    private String password;

    private String[] levels = { "Beginner", "Intermediate", "Advanced", "Professional" };
    private String[] genders = { "Male", "Female" };
    private String[] goals = { "Loss weight", "Gain weight", "Maintenance" };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ChoiceBoxes
        genderChoiceBox.getItems().add("Choose your Gender");
        genderChoiceBox.getItems().addAll(genders);
        genderChoiceBox.setValue("Choose your Gender");

        fitnessLevelChoiceBox.getItems().add("Choose your fitness level");
        fitnessLevelChoiceBox.getItems().addAll(levels);
        fitnessLevelChoiceBox.setValue("Choose your fitness level");

        goalChoiceBox.getItems().add("Choose your Goal");
        goalChoiceBox.getItems().addAll(goals);
        goalChoiceBox.setValue("Choose your Goal");

        // Input formatters
        setupDateFormatting();
        setupNumericFormatting();
    }

    public void initData(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    private void setupDateFormatting() {
        birthdateField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("[0-9/]*"))
                return null;
            if (newText.length() > 10)
                return null;

            if (change.isAdded()) {
                int caretPos = change.getCaretPosition();
                if (newText.length() == 4 && !newText.contains("/")) {
                    change.setText(change.getText() + "/");
                    change.setCaretPosition(caretPos + 1);
                    change.setAnchor(caretPos + 1);
                } else if (newText.length() == 7 && newText.charAt(4) == '/' && newText.lastIndexOf('/') == 4) {
                    change.setText(change.getText() + "/");
                    change.setCaretPosition(caretPos + 1);
                    change.setAnchor(caretPos + 1);
                }
            }
            return change;
        }));
    }

    private void setupNumericFormatting() {
        weightField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return (newText.isEmpty() || newText.matches("\\d{0,3}")) ? change : null;
        }));

        heightField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return (newText.isEmpty() || newText.matches("\\d{0,3}")) ? change : null;
        }));
    }

    @FXML
    public void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            // Try to go back using history
            SceneNavigator.goBack(stage);
        } catch (Exception e) {
            // Fallback if history logic fails or is empty, load Signup1 explicitly
            try {
                // Note: Going back explicitly won't restore data unless we pass it back.
                // But SceneNavigator stack should handle it if we pushed it.
                Parent root = FXMLLoader.load(getClass().getResource("/view/auth/SignupPassword.fxml"));
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @FXML
    public void completeSignup(ActionEvent event) {
        messageLabel.getStyleClass().removeAll("success-message", "error-message");
        messageLabel.getStyleClass().add("error-message");

        // Use valid Gender/Goal/FitnessLevel/Weight/Height from UI
        // ... (Validation logic)
        if (genderChoiceBox.getValue().equals("Choose your Gender")) {
            messageLabel.setText("Please select your gender.");
            return;
        }

        String birthdate = birthdateField.getText();
        if (birthdate.isEmpty() || !birthdate.matches("\\d{4}/\\d{2}/\\d{2}")) {
            messageLabel.setText("Invalid birthdate (yyyy/mm/dd).");
            return;
        }

        if (weightField.getText().isEmpty()) {
            messageLabel.setText("Weight is required.");
            return;
        }

        if (heightField.getText().isEmpty()) {
            messageLabel.setText("Height is required.");
            return;
        }

        if (fitnessLevelChoiceBox.getValue().equals("Choose your fitness level")) {
            messageLabel.setText("Please select your fitness level.");
            return;
        }

        if (goalChoiceBox.getValue().equals("Choose your Goal")) {
            messageLabel.setText("Please select your goal.");
            return;
        }

        try {
            // Parse data for validation
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate dateOfBirth = LocalDate.parse(birthdate, formatter);

            // Age Validation
            if (dateOfBirth.isAfter(LocalDate.now())) {
                messageLabel.setText("Date of birth cannot be in the future.");
                return;
            }
            if (dateOfBirth.isBefore(LocalDate.now().minusYears(120))) {
                messageLabel.setText("Date of birth exceeds age limit.");
                return;
            }
            int age = java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
            if (age < 10) {
                messageLabel.setText("You must be at least 10 years old.");
                return;
            }

            double weight = Double.parseDouble(weightField.getText());
            double height = Double.parseDouble(heightField.getText());

            // Range Validation
            if (weight < 30.0 || weight > 300.0) {
                messageLabel.setText("Weight must be between 30 and 300 kg.");
                return;
            }
            if (height < 100.0 || height > 250.0) {
                messageLabel.setText("Height must be between 100 and 250 cm.");
                return;
            }

            Gender userGender = genderChoiceBox.getValue().equals("Male") ? Gender.MALE : Gender.FEMALE;

            FitnessLevel userLevel = FitnessLevel.BEGINNER;
            switch (fitnessLevelChoiceBox.getValue()) {
                case "Intermediate":
                    userLevel = FitnessLevel.INTERMEDIATE;
                    break;
                case "Advanced":
                    userLevel = FitnessLevel.ADVANCED;
                    break;
                case "Professional":
                    userLevel = FitnessLevel.PROFESSIONAL;
                    break;
            }

            GoalType userGoal = GoalType.MAINTENANCE;
            switch (goalChoiceBox.getValue()) {
                case "Loss weight":
                    userGoal = GoalType.LOSE_WEIGHT;
                    break;
                case "Gain weight":
                    userGoal = GoalType.GAIN_WEIGHT;
                    break;
            }

            // Create Profile object
            Profile newProfile = new Profile(username, email, password, height, weight, userGoal, userGender,
                    dateOfBirth, userLevel);

            // Persist using Main (Constraint: "Access UserRepository ONLY through methods
            // exposed by Main")
            boolean saved = app.Main.getUserRepository().saveUser(newProfile);

            if (saved) {
                // Set current user (Constraint: "Set the current user in Main")
                app.Main.setCurrentProfile(newProfile);

                // Navigate to Home
                Parent root = FXMLLoader.load(getClass().getResource("/view/home/Scene4.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                messageLabel.setText("Database Error: Could not save user.");
            }

        } catch (DateTimeParseException e) {
            messageLabel.setText("Invalid date. Please check your birthdate.");
        } catch (Exception e) {
            messageLabel.setText("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
