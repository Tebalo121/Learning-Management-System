package com.example.learningmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AdminHomeController implements UserAwareController {
    @FXML private Label welcomeLabel;
    private User user;

    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";

    public static void switchSceneStatic(String fxmlName, User user, TableView<Course> stage) throws IOException {
        String fullPath = "/com/example/learningmanagementsystem/" + fxmlName;
        URL fxmlResource = AdminHomeController.class.getResource(fullPath);
        if (fxmlResource == null) {
            throw new IOException("FXML file not found at path: " + fullPath);
        }

        FXMLLoader loader = new FXMLLoader(fxmlResource);
        Parent root = loader.load();

        Object controller = loader.getController();
        if (controller instanceof UserAwareController userAware) {
            userAware.initUserData(user);
        }

        stage.getScene();
    }

    @Override
    public void initUserData(User user) {
        this.user = user;
        if (user != null) {
            welcomeLabel.setText("Welcome Admin " + user.getFullName());
        } else {
            welcomeLabel.setText("Welcome Admin");
            System.err.println("⚠️ Warning: user is null in AdminHomeController");
        }
    }

    @Override
    public void initializeWithUser(User user) {
        // Reserved for future use
    }

    @FXML
    private void handleManageUsers() {
        log("Navigating to Manage Users");
        switchScene("admin_manage_users.fxml");
    }

    @FXML
    private void handleViewCourses() {
        log("Navigating to View Courses");
        switchScene("admin_view_course.fxml");
    }

    @FXML
    private void handleViewReports() {
        log("Navigating to View Reports");
        switchScene("admin_view_reports.fxml");
    }

    @FXML
    private void handleManageEnrollments() {
        log("Navigating to Manage Enrollments");
        switchScene("admin_manage_enrollments.fxml");
    }

    @FXML
    private void handleSystemAnalytics() {
        log("Navigating to System Analytics");
        switchScene("admin_system_analytics.fxml");
    }

    @FXML
    private void handleLogout() {
        log("Logging out");
        switchScene("login-view.fxml");
    }

    private void switchScene(String fxmlName) {
        try {
            String fullPath = BASE_FXML_PATH + fxmlName;
            URL fxmlResource = getClass().getResource(fullPath);
            if (fxmlResource == null) {
                throw new IOException("FXML file not found at path: " + fullPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlResource);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAware) {
                userAware.initUserData(user);
            }

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("❌ Failed to load FXML: " + fxmlName);
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