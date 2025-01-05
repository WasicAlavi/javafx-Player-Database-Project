package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginPageController {

    @FXML
    private TextField clubNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button guestButton;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private TextField passwordTextField;



    private Map<String, String> credentials;

    @FXML
    private void initialize() {
        // Ensure the eye icon is set when the scene is initialized
        //setEyeIcon("eye_close.png");

        // Ensure the PasswordField is visible and TextField is hidden on startup
        passwordField.setVisible(true);
        passwordTextField.setVisible(false);
    }
    /*private void setEyeIcon(String imageFileName) {
        InputStream imageStream = getClass().getResourceAsStream("/" + imageFileName);
        if (imageStream != null) {
            eyeIcon.setImage(new Image(imageStream));
        } else {
            System.err.println("Image not found: /" + imageFileName);
        }
    }*/
    @FXML
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            // Show password
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            // Hide password
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordTextField.setVisible(false);
        }

        // Manage visibility correctly to ensure layout stays consistent
        passwordField.setManaged(!showPasswordCheckBox.isSelected());
        passwordTextField.setManaged(showPasswordCheckBox.isSelected());
    }

    private void showPassword() {
        // Ensure the password is copied from TextField to PasswordField correctly
        if (passwordTextField.isVisible()) {
            passwordField.setText(passwordTextField.getText());  // Copy text to PasswordField
        }

        // Make PasswordField visible and TextField hidden
        passwordField.setVisible(true);
        passwordTextField.setVisible(false);

        // Ensure layout doesn't break by setting managed correctly
        passwordField.setManaged(true);
        passwordTextField.setManaged(false);
    }

    private void hidePassword() {
        // Ensure the password is copied from PasswordField to TextField correctly
        if (passwordField.isVisible()) {
            passwordTextField.setText(passwordField.getText());  // Copy text to TextField
        }

        // Make TextField visible and PasswordField hidden
        passwordTextField.setVisible(true);
        passwordField.setVisible(false);

        // Ensure layout doesn't break by setting managed correctly
        passwordTextField.setManaged(true);
        passwordField.setManaged(false);

        // Explicitly show the text characters in the TextField
        passwordTextField.selectAll();  // This makes the characters visible
    }


    @FXML
    private void onForgotPassword() {
        // Create a TextInputDialog to get the user's email
        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Forgot Password");
        emailDialog.setHeaderText("Enter your email address");
        emailDialog.setContentText("Email:");

        // Show the dialog and wait for user input
        Optional<String> result = emailDialog.showAndWait();

        result.ifPresent(email -> {
            // Simulate sending OTP
            String otp = sendOtp(email);  // Assume this method sends the OTP and returns it

            // Ask the user to enter the OTP
            TextInputDialog otpDialog = new TextInputDialog();
            otpDialog.setTitle("OTP Verification");
            otpDialog.setHeaderText("Enter the OTP sent to your email");
            otpDialog.setContentText("OTP:");

            Optional<String> otpResult = otpDialog.showAndWait();

            otpResult.ifPresent(enteredOtp -> {
                if (enteredOtp.equals(otp)) {
                    // Successful verification
                    showAlert(Alert.AlertType.INFORMATION, "Success", "OTP verified successfully. You can reset your password.");
                } else {
                    // OTP mismatch
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid OTP. Please try again.");
                }
            });
        });
    }

    private String sendOtp(String email) {
        // Simulate sending an OTP - replace this with actual email sending logic
        String otp = String.valueOf((int) (Math.random() * 9000) + 1000);  // Generate a random 4-digit OTP
        System.out.println("OTP sent to " + email + ": " + otp);  // Simulate sending email
        return otp;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public LoginPageController() {
        credentials = new HashMap<>();
        loadCredentials();
    }

    private void loadCredentials() {
        // Load credentials from a file (clubName,password)
        File file = new File("target/classes/credentials.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    credentials.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load credentials file.");
        }
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String clubName = clubNameField.getText().trim();
        String password;
        if (showPasswordCheckBox.isSelected()) {
            password = passwordTextField.getText().trim();
        }else{

            password = passwordField.getText().trim();
        }

        System.out.println("Login input - Club Name: " + clubName + ", Password: " + password);

        if (clubName.isEmpty() || password.isEmpty()) {
            showErrorAlert("Input Error", "Please enter both Club Name and Password.");
            return;
        }

        // Ensure that clubName and password are captured correctly
        System.out.println("Captured Credentials - Club Name: " + clubName + ", Password: " + password);

        try {
            // Load the Dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/dashboard.fxml"));
            Parent root = loader.load();

            // Get the controller and set credentials dynamically
            DashboardController controller = loader.getController();
            controller.setCredentials(clubName, password);
        if(controller.successfulLogin) {
         // Show the Dashboard scene
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Connection Error", "Unable to load the Dashboard.");
        }
    }



    @FXML
    private void onContinueAsGuest(ActionEvent event) {
        navigateToScene(event, "/scenes/MainMenu.fxml", "Main Menu");

    }

    private void navigateToScene(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            MainMenuController mainMenuController = loader.getController();
            mainMenuController.setPreviousScene(stage.getScene());

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Navigation Error", "Unable to load the requested scene.");
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void onExitSystem(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}