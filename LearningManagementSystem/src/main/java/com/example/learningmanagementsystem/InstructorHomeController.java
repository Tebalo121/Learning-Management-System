package com.example.learningmanagementsystem;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class InstructorHomeController implements UserAwareController {

    @FXML private Label welcomeLabel;
    @FXML private MenuBar menuBar;
    @FXML private Button toggleMenuButton;
    @FXML private Circle holoTrigger;
    @FXML private GridPane holoGrid;
    private User user;

    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";

    @Override
    public void initUserData(User user) {
        this.user = user;
        if (user != null) {
            welcomeLabel.setText("Welcome Instructor " + user.getFullName());
            System.out.println("InstructorHomeController initialized with user: " + user.getUsername());
        } else {
            welcomeLabel.setText("Welcome Instructor (Unknown)");
            System.err.println("InstructorHomeController: User is null");
        }
    }

    @Override
    public void initializeWithUser(User user) {}

    @FXML
    private void handleToggleMenu() {
        boolean isVisible = menuBar.isVisible();
        FadeTransition fade = new FadeTransition(Duration.millis(300), menuBar);
        if (isVisible) {
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> {
                menuBar.setVisible(false);
                menuBar.setManaged(false);
                toggleMenuButton.setText("Show Menu");
            });
        } else {
            menuBar.setVisible(true);
            menuBar.setManaged(true);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            toggleMenuButton.setText("Hide Menu");
        }
        fade.play();
        log("Menu bar " + (isVisible ? "hidden" : "shown"));
    }

    @FXML
    private void handleShowHoloGrid() {
        holoGrid.setVisible(true);
        holoGrid.setManaged(true);
        holoGrid.getChildren().forEach(node -> {
            RotateTransition rt = new RotateTransition(Duration.millis(300), node);
            rt.setFromAngle(-10);
            rt.setToAngle(0);
            rt.play();
        });
        System.out.println("Holo grid shown");
    }

    @FXML
    private void handleHideHoloGrid() {
        holoGrid.setVisible(false);
        holoGrid.setManaged(false);
        System.out.println("Holo grid hidden");
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
    private void handleConductSurveys() {
        log("Conducting Surveys");
        switchScene("instructor_conduct_surveys.fxml");
    }

    @FXML
    private void handleSetDiscussionTopics() {
        log("Setting Discussion Topics");
        switchScene("instructor_set_discussion_topics.fxml");
    }

    @FXML
    private void handleTrackProgress() {
        log("Tracking Student Progress");
        switchScene("instructor_track_progress.fxml");
    }

    @FXML
    private void handleLogout() {
        log("Logging out");
        switchScene("login-view.fxml");
    }

    @FXML
    private void handleExit() {
        log("Exiting application");
        Platform.exit();
    }

    @FXML
    private void handleViewStudentProgress() {
        log("Viewing Student Progress");
        switchScene("instructor_view_progress.fxml");
    }

    @FXML
    private void handleHelpContents() {
        log("Opening Help Contents");
        showInfoDialog("Help", "This section provides guidance on how to use the system.");
    }

    @FXML
    private void handleAbout() {
        log("Opening About Dialog");
        showInfoDialog("About", "Learning Management System\nVersion 1.0\nDeveloped by YourNameHere");
    }

    private void switchScene(String fxmlName) {
        try {
            String path = BASE_FXML_PATH + fxmlName;
            System.out.println("Loading FXML: " + path);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            if (loader.getLocation() == null) {
                System.err.println("FXML file not found: " + path);
                return;
            }
            Parent root = loader.load();
            System.out.println("FXML loaded: " + fxmlName);

            Scene scene = new Scene(root);

            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAware) {
                userAware.initUserData(user);
                System.out.println("Controller initialized for: " + fxmlName);
            } else {
                System.out.println("Controller does not implement UserAwareController for: " + fxmlName);
            }

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            if (stage == null) {
                System.err.println("Stage is null for: " + fxmlName);
                return;
            }
            stage.setScene(scene);
            System.out.println("Scene set for: " + fxmlName);
        } catch (IOException e) {
            System.err.println("❌ Failed to load scene: " + fxmlName + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void log(String action) {
        if (user != null) {
            System.out.println(action + " for: " + user.getUsername());
        } else {
            System.out.println(action + " (user not initialized)");
        }
    }
}