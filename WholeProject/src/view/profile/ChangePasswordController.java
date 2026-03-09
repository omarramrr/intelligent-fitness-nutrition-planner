package view.profile;

import app.UserSession;
import app.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import model.user.domain.User;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {

    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private TextField visibleCurrentPasswordField;
    @FXML
    private Button toggleCurrentPasswordBtn;

    @FXML
    private PasswordField newPasswordField;
    @FXML
    private TextField visibleNewPasswordField;
    @FXML
    private Button toggleNewPasswordBtn;

    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField visibleConfirmPasswordField;
    @FXML
    private Button toggleConfirmPasswordBtn;

    @FXML
    private Label messageLabel;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get current user
        currentUser = UserSession.getInstance().getCurrentUser();

        if (currentUser == null) {
            messageLabel.setText("ERROR: No user logged in!");
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        // Clear message label
        messageLabel.setText("");
    }

    /**
     * Save the new password after validation
     */
    /**
     * Toggle current password visibility
     */
    @FXML
    public void toggleCurrentPassword() {
        togglePasswordVisibility(currentPasswordField, visibleCurrentPasswordField, toggleCurrentPasswordBtn);
    }

    /**
     * Toggle new password visibility
     */
    @FXML
    public void toggleNewPassword() {
        togglePasswordVisibility(newPasswordField, visibleNewPasswordField, toggleNewPasswordBtn);
    }

    /**
     * Toggle confirm password visibility
     */
    @FXML
    public void toggleConfirmPassword() {
        togglePasswordVisibility(confirmPasswordField, visibleConfirmPasswordField, toggleConfirmPasswordBtn);
    }

    /**
     * Helper method to toggle password visibility
     */
    private void togglePasswordVisibility(PasswordField passwordField, TextField textField, Button toggleBtn) {
        if (textField.isVisible()) {
            // Hide text, show password field
            textField.setVisible(false);
            textField.setManaged(false);
            passwordField.setText(textField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            toggleBtn.setText("👁");
        } else {
            // Show text field
            textField.setText(passwordField.getText());
            textField.setVisible(true);
            textField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            toggleBtn.setText("👁‍🗨");
        }
    }

    /**
     * Save the new password after validation
     */
    @FXML
    public void savePassword(ActionEvent event) {
        if (currentUser == null) {
            messageLabel.setText("❌ Error: No user logged in!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        // Get password from visible field if shown, otherwise from password field
        String currentPassword = visibleCurrentPasswordField.isVisible()
                ? visibleCurrentPasswordField.getText()
                : currentPasswordField.getText();
        String newPassword = visibleNewPasswordField.isVisible()
                ? visibleNewPasswordField.getText()
                : newPasswordField.getText();
        String confirmPassword = visibleConfirmPasswordField.isVisible()
                ? visibleConfirmPasswordField.getText()
                : confirmPasswordField.getText();

        // Validate inputs
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("⚠️ All fields are required!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        // Verify current password
        if (!currentPassword.equals(currentUser.getPassword())) {
            messageLabel.setText("❌ Current password is incorrect!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("❌ New passwords do not match!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        // Check if new password is same as current
        if (newPassword.equals(currentPassword)) {
            messageLabel.setText("⚠️ New password must be different from current password!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            return;
        }

        try {
            // Update password (this will validate password requirements)
            currentUser.setPassword(newPassword);

            // Save user to database using Main repository (Constraint)
            boolean success = app.Main.getUserRepository().updatePassword(currentUser.getUserId(), newPassword);

            if (!success) {
                throw new Exception("Database update failed.");
            }

            // Show success message
            messageLabel.setText("✅ Password updated successfully!");
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("success-message");

            // Navigate back to profile after 1.5 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> goBack(event));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IllegalArgumentException e) {
            // Password validation failed
            messageLabel.setText("❌ " + e.getMessage());
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
        } catch (Exception e) {
            messageLabel.setText("❌ Error updating password: " + e.getMessage());
            messageLabel.getStyleClass().clear();
            messageLabel.getStyleClass().add("error-message");
            e.printStackTrace();
        }
    }

    /**
     * Cancel and return to profile
     */
    @FXML
    public void cancel(ActionEvent event) {
        goBack(event);
    }

    /**
     * Navigate back to profile page
     */
    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/profile/SceneProfile.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
