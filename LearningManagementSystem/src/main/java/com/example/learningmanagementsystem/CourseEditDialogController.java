package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.CourseDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CourseEditDialogController {
    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextArea descField;

    private boolean isAddMode;
    private int currentInstructorId;
    private Course currentCourse;

    public void setMode(boolean isAddMode, int instructorId) {
        this.isAddMode = isAddMode;
        this.currentInstructorId = instructorId;

        if (isAddMode) {
            titleLabel.setText("Add New Course");
        } else {
            titleLabel.setText("Edit Course");
        }
    }

    public void setCourseData(Course course) {
        this.currentCourse = course;
        nameField.setText(course.getCourseName());
        descField.setText(course.getDescription());
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String description = descField.getText().trim();

        if (name.isEmpty()) {
            showAlert("Validation Error", "Course name cannot be empty");
            return;
        }

        try {
            CourseDAO courseDAO = new CourseDAO();

            if (isAddMode) {
                courseDAO.addCourse(name, description, currentInstructorId);
            } else {
                courseDAO.updateCourse(currentCourse.getCourseId(), name, description);
            }

            closeDialog();

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save course: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}