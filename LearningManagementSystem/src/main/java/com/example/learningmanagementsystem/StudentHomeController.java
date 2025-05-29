package com.example.learningmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentHomeController implements UserAwareController {
    @FXML
    private Label welcomeLabel;
    private User user;

    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";

    @Override
    public void initUserData(User user) {
        this.user = user;
        if (user != null) {
            welcomeLabel.setText("Welcome Student " + user.getFullName());
        } else {
            welcomeLabel.setText("Welcome Student");
            System.err.println("⚠️ Warning: user is null in StudentHomeController");
        }
    }

    @Override
    public void initializeWithUser(User user) {
        // Reserved for future use if needed
    }

    @FXML
    private void handleViewCourses() {
        log("Navigating to Courses");
        switchScene("student_courses.fxml");
    }

    @FXML
    private void handleViewAssignments() {
        log("Navigating to Assignments");
        switchScene("student_assignments.fxml");
    }

    @FXML
    private void handleViewGrades() {
        log("Navigating to Grades");
        switchScene("student_grades.fxml");
    }

    @FXML
    private void handleTrackProgress() {
        log("Navigating to Progress");
        switchScene("student_progress.fxml");
    }

    @FXML
    private void handleViewSurveys() {
        log("Navigating to Surveys");
        switchScene("student_surveys.fxml");
    }

    @FXML
    private void handleViewDiscussion() {
        log("Navigating to Discussion");
        switchScene("student_discussion.fxml");
    }

    @FXML
    private void handleLogout() {
        log("Logging out");
        switchScene("login-view.fxml");
    }

    private void switchScene(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_FXML_PATH + fxmlName));
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

    private void log(String message) {
        System.out.println((user != null)
                ? message + " for: " + user.getUsername()
                : message + " (user not initialized)");
    }
}