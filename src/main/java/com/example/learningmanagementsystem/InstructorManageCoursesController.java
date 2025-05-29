package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.CourseDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;

public class InstructorManageCoursesController implements UserAwareController {

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, String> creditsColumn;
    @FXML private TextField searchField;

    private final CourseDAO courseDAO = new CourseDAO();
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private User instructor;

    @Override
    public void initUserData(User user) {
        this.instructor = user;
        courseNameColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCourseName()));
        instructorColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getInstructorName()));
        creditsColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDescription())); // Treating description as "credits"
        refreshTable();
    }

    @Override
    public void initializeWithUser(User user) {

    }

    private void refreshTable() {
        try {
            courseList.setAll(courseDAO.getCoursesByInstructor(instructor.getUserId()));
            courseTable.setItems(courseList);
        } catch (SQLException e) {
            showError("Failed to load courses: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddCourse() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Course");
        dialog.setHeaderText("Enter course name and description separated by comma:");
        dialog.setContentText("Format: Course Name, Description");

        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",", 2);
            if (parts.length == 2) {
                try {
                    courseDAO.addCourse(parts[0].trim(), parts[1].trim(), instructor.getUserId());
                    refreshTable();
                } catch (SQLException e) {
                    showError("Add failed: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleEditCourse() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a course to edit.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getCourseName() + ", " + selected.getDescription());
        dialog.setTitle("Edit Course");
        dialog.setHeaderText("Update course name and description:");
        dialog.setContentText("Format: Course Name, Description");

        dialog.showAndWait().ifPresent(input -> {
            String[] parts = input.split(",", 2);
            if (parts.length == 2) {
                try {
                    courseDAO.updateCourse(selected.getCourseId(), parts[0].trim(), parts[1].trim());
                    refreshTable();
                } catch (SQLException e) {
                    showError("Update failed: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleDeleteCourse() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a course to delete.");
            return;
        }

        try {
            courseDAO.deleteCourse(selected.getCourseId());
            refreshTable();
        } catch (SQLException e) {
            showError("Delete failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}