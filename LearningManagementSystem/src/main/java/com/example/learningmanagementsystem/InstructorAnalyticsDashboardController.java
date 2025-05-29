package com.example.learningmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class InstructorAnalyticsDashboardController implements UserAwareController {
    private User instructor;

    @Override
    public void initUserData(User user) {
        this.instructor = user;
        System.out.println("InstructorAnalyticsDashboardController initialized with user: " +
                (user != null ? user.getUsername() : "null"));
    }

    @Override
    public void initializeWithUser(User user) {
        // Optional
    }

    @FXML
    private void handleBack() {
        try {
            String fxmlPath = "/com/example/learningmanagementsystem/instructor-home.fxml";
            System.out.println("Loading FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                System.err.println("FXML file not found: " + fxmlPath);
                return;
            }
            Parent root = loader.load();
            System.out.println("FXML loaded successfully");

            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAware) {
                System.out.println("Initializing controller with user: " +
                        (instructor != null ? instructor.getUsername() : "null"));
                userAware.initUserData(instructor);
            } else {
                System.out.println("Controller does not implement UserAwareController");
            }

            Stage stage = (Stage) root.getScene().getWindow();
            if (stage == null) {
                System.err.println("Stage is null");
                return;
            }
            stage.setScene(new Scene(root));
            System.out.println("Scene set for instructor-home.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load instructor home screen: " + e.getMessage());
        }
    }
}