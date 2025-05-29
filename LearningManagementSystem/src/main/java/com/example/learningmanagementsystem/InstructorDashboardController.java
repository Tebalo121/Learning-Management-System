package com.example.learningmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class InstructorDashboardController implements UserAwareController {
    @FXML private Label welcomeLabel;
    private User user;

    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";

    @Override
    public void initUserData(User user) {
        this.user = user;
        welcomeLabel.setText("Welcome to Dashboard, " + user.getFullName());
    }

    @Override
    public void initializeWithUser(User user) {
        // Reserved for future use if needed
    }

    @FXML
    private void handleBackToHome() {
        log("Navigating back to Home");
        switchScene("instructor_home.fxml");
    }

    @FXML
    private void handleLogout() {
        log("Logging out");
        this.user = null; // Clear user data
        switchScene("login.fxml");
    }

    private void switchScene(String fxmlName) {
        try {
            String resourcePath = BASE_FXML_PATH + fxmlName;
            System.out.println("Attempting to load FXML: " + resourcePath);
            var resource = getClass().getResource(resourcePath);
            if (resource == null) {
                throw new IllegalStateException("Cannot find FXML resource: " + resourcePath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAware) {
                userAware.initUserData(user);
            }

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("❌ Failed to load scene: " + fxmlName);
            e.printStackTrace();
        }
    }

    private void log(String action) {
        if (user != null) {
            System.out.println(action + " for: " + user.getUsername());
        } else {
            System.out.println(action + " (user not initialized)");
        }
    }
}