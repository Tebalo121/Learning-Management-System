package com.example.learningmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class CourseViewController implements UserAwareController {
    @FXML private Text courseNameText;
    @FXML private TextArea descriptionTextArea;
    @FXML private Button backButton;

    private User instructor;
    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";

    public void setCourseData(Course course, User instructor) {
        this.instructor = instructor;
        courseNameText.setText(course.getCourseName());
        descriptionTextArea.setText(course.getDescription() != null ? course.getDescription() : "No description provided.");
    }

    @Override
    public void initUserData(User user) {
        this.instructor = user;
    }

    @Override
    public void initializeWithUser(User user) {
        // Optional
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_FXML_PATH + "instructor_manage_courses.fxml"));
            if (loader.getLocation() == null) {
                showAlert("Error", "FXML file not found: instructor_manage_courses.fxml");
                return;
            }
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAware) {
                userAware.initUserData(instructor);
            }
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load manage courses screen: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}