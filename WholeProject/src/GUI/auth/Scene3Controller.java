package view.auth;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextFormatter;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import app.UserManager;
import app.UserSession;
import model.user.domain.User;
import model.user.domain.Profile;
import model.user.enums.Gender;
import model.user.enums.GoalType;
import model.user.enums.FitnessLevel;

public class Scene3Controller implements Initializable {

    @FXML
    private ChoiceBox<String> fitnessLevel;

    @FXML
    private ChoiceBox<String> gender;

    @FXML
    private ChoiceBox<String> goal;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField birthdateField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private TextField weightField;

    @FXML
    private TextField heightField;

    @FXML
    private javafx.scene.control.Button togglePasswordBtn;

    private String[] level = { "Beginner", "Intermediate", "Advanced", "Professional" };

    private String[] g = { "Male", "Female" };

    private String[] goals = { "Loss weight", "Gain weight", "Maintenance" };

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Add placeholder and options for gender
        gender.getItems().add("Choose your Gender");
        gender.getItems().addAll(g);
        gender.setValue("Choose your Gender"); // Set placeholder as default

        // Add placeholder and options for fitness level
        fitnessLevel.getItems().add("Choose your fitness level");
        fitnessLevel.getItems().addAll(level);
        fitnessLevel.setValue("Choose your fitness level"); // Set placeholder as default

        // Add placeholder and options for goal
        goal.getItems().add("Choose your Goal");
        goal.getItems().addAll(goals);
        goal.setValue("Choose your Goal"); // Set placeholder as default

        // Bind the visible and hidden password fields
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        // Auto-format birthdate field to yyyy/mm/dd with automatic slashes
        birthdateField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Only allow digits and slashes
            if (!newText.matches("[0-9/]*")) {
                return null;
            }

            // Limit to 10 characters (yyyy/mm/dd)
            if (newText.length() > 10) {
                return null;
            }

            // Auto-add slashes after year (4 digits) and month (2 digits)
            if (change.isAdded()) {
                int caretPos = change.getCaretPosition();

                // If user just typed 4th digit (year), add slash
                if (newText.length() == 4 && !newText.contains("/")) {
                    change.setText(change.getText() + "/");
                    change.setCaretPosition(caretPos + 1);
                    change.setAnchor(caretPos + 1);
                }
                // If user just typed 7th character (mm after yyyy/), add slash
                else if (newText.length() == 7 && newText.charAt(4) == '/' && newText.lastIndexOf('/') == 4) {
                    change.setText(change.getText() + "/");
                    change.setCaretPosition(caretPos + 1);
                    change.setAnchor(caretPos + 1);
                }
            }

            return change;
        }));

        // Restrict weight field to numbers only (max 3 digits)
        weightField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d{0,3}")) {
                return change;
            }
            return null;
        }));

        // Restrict height field to numbers only (max 3 digits)
        heightField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d{0,3}")) {
                return change;
            }
            return null;
        }));
    }

    @FXML
    public void togglePasswordVisibility() {
        if (passwordField.isVisible()) {
            // Show password
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
        } else {
            // Hide password
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }

    @FXML
    public void signUp(ActionEvent event) {
        String user = usernameField.getText();
        String birthdate = birthdateField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();
        String weight = weightField.getText();
        String height = heightField.getText();
        String selectedGender = gender.getValue();
        String selectedFitnessLevel = fitnessLevel.getValue();
        String selectedGoal = goal.getValue();

        // Check required fields
        if (user.isEmpty()) {
            System.out.println("ERROR: Username is required!");
            return;
        }

        if (birthdate.isEmpty()) {
            System.out.println("ERROR: Birthdate is required!");
            return;
        }

        if (email.isEmpty()) {
            System.out.println("ERROR: Email is required!");
            return;
        }

        if (pass.isEmpty()) {
            System.out.println("ERROR: Password is required!");
            return;
        }

        // Validate birthdate format (yyyy/mm/dd)
        if (!birthdate.matches("\\d{4}/\\d{2}/\\d{2}")) {
            System.out.println("ERROR: Invalid birthdate format! Please use yyyy/mm/dd (e.g., 2000/01/15)");
            return;
        }

        try {
            // Parse birthdate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate dateOfBirth = LocalDate.parse(birthdate, formatter);

            // Parse gender
            Gender userGender = null;
            if (selectedGender != null && !selectedGender.equals("Choose your Gender")) {
                userGender = selectedGender.equals("Male") ? Gender.MALE : Gender.FEMALE;
            }

            // Parse fitness level
            FitnessLevel userFitnessLevel = null;
            if (selectedFitnessLevel != null && !selectedFitnessLevel.equals("Choose your fitness level")) {
                switch (selectedFitnessLevel) {
                    case "Beginner":
                        userFitnessLevel = FitnessLevel.BEGINNER;
                        break;
                    case "Intermediate":
                        userFitnessLevel = FitnessLevel.INTERMEDIATE;
                        break;
                    case "Advanced":
                        userFitnessLevel = FitnessLevel.ADVANCED;
                        break;
                    case "Professional":
                        userFitnessLevel = FitnessLevel.PROFESSIONAL;
                        break;
                }
            }

            // Parse goal
            GoalType userGoal = null;
            if (selectedGoal != null && !selectedGoal.equals("Choose your Goal")) {
                switch (selectedGoal) {
                    case "Loss weight":
                        userGoal = GoalType.LOSE_WEIGHT;
                        break;
                    case "Gain weight":
                        userGoal = GoalType.GAIN_WEIGHT;
                        break;
                    case "Maintenance":
                        userGoal = GoalType.MAINTENANCE;
                        break;
                }
            }

            // Parse weight and height
            double userWeight = weight.isEmpty() ? 0 : Double.parseDouble(weight);
            double userHeight = height.isEmpty() ? 0 : Double.parseDouble(height);

            // Register user with backend
            UserManager userManager = UserManager.getInstance();

            if (userGender != null && userGoal != null && userFitnessLevel != null && userWeight > 0
                    && userHeight > 0) {
                // Register with complete profile
                userManager.registerUserWithProfile(user, email, pass, userGender, dateOfBirth,
                        userHeight, userWeight, userGoal, userFitnessLevel);
            } else {
                // Register with basic info only
                userManager.registerUser(user, email, pass);

                // Update profile with available data
                Profile profile = userManager.getProfile(user);
                if (userGender != null)
                    profile.setGender(userGender);
                if (dateOfBirth != null)
                    profile.setDateOfBirth(dateOfBirth);
                if (userHeight > 0)
                    profile.setHeightInCm(userHeight);
                if (userWeight > 0)
                    profile.setWeightInKg(userWeight);
                if (userGoal != null)
                    profile.setGoal(userGoal);
                if (userFitnessLevel != null)
                    profile.setFitnessLevel(userFitnessLevel);
            }

            // Log in the newly created user
            User newUser = userManager.getUser(user);
            Profile newProfile = userManager.getProfile(user);
            UserSession.getInstance().login(newUser, newProfile);

            System.out.println("SUCCESS: Account created and logged in for: " + user);

            // Navigate to Scene4 after successful sign up
            Parent root = FXMLLoader.load(getClass().getResource("/view/home/Scene4.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Scene4 loaded successfully!");

        } catch (DateTimeParseException e) {
            System.out.println("ERROR: Invalid date format: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Registration failed - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: Failed to complete registration!");
            e.printStackTrace();
        }
    }

    public void goToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/auth/Scene2.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToScene1(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/auth/Scene1.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
