package com.example.learningmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class InstructorHomeController implements UserAwareController {
    @FXML private Label welcomeLabel;
    private User user;

    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";

    @Override
    public void initUserData(User user) {
        this.user = user;
        welcomeLabel.setText("Welcome Instructor " + user.getFullName());
    }

    @Override
    public void initializeWithUser(User user) {
        // Reserved for future use if needed
    }

    @FXML
    private void handleManageCourses() {
        log("Managing Courses");
        switchScene("instructor_manage_courses.fxml");
    }

    @FXML
    private void handleCreateAssignments() {
        log("Creating Assignments");
        switchScene("instructor_create_assignments.fxml");
    }

    @FXML
    private void handleGradeSubmissions() {
        log("Grading Submissions");
        switchScene("instructor_grade_submissions.fxml");
    }

    @FXML
    private void handlePostAnnouncements() {
        log("Posting Announcements");
        switchScene("instructor_post_announcements.fxml");
    }

    @FXML
    private void handleLogout() {
        log("Logging out");
        switchScene("login.fxml");
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

    private void log(String action) {
        if (user != null) {
            System.out.println(action + " for: " + user.getUsername());
        } else {
            System.out.println(action + " (user not initialized)");
        }
    }
}
