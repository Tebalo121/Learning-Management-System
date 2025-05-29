package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.AssignmentDAO;
import com.example.learningmanagementsystem.dao.CourseDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class InstructorCreateAssignmentsController implements UserAwareController {
    @FXML private ComboBox<Course> courseComboBox;
    @FXML private TextField assignmentTitleField;
    @FXML private TextArea assignmentDescriptionField;
    @FXML private DatePicker dueDatePicker;
    @FXML private TableView<Assignment> assignmentTable;
    @FXML private TableColumn<Assignment, String> courseCol;
    @FXML private TableColumn<Assignment, String> titleCol;
    @FXML private TableColumn<Assignment, String> dueDateCol;

    private final CourseDAO courseDAO = new CourseDAO();
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();
    private final ObservableList<Assignment> assignmentList = FXCollections.observableArrayList();
    private User instructor;

    @Override
    public void initUserData(User user) {
        this.instructor = user;
        loadCourses();
        setupAssignmentTable();
        loadAssignments();
    }

    @Override
    public void initializeWithUser(User user) {

    }

    private void loadCourses() {
        try {
            courseComboBox.setItems(FXCollections.observableArrayList(
                    courseDAO.getCoursesByInstructor(instructor.getUserId())
            ));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load courses: " + e.getMessage());
        }
    }

    private void setupAssignmentTable() {
        courseCol.setCellValueFactory(cellData -> cellData.getValue().courseNameProperty());
        titleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        dueDateCol.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());
        assignmentTable.setItems(assignmentList);
    }

    private void loadAssignments() {
        try {
            assignmentList.setAll(assignmentDAO.getAssignmentsByInstructor(instructor.getUserId()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load assignments: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateAssignment() {
        Course selectedCourse = courseComboBox.getValue();
        String title = assignmentTitleField.getText().trim();
        String description = assignmentDescriptionField.getText().trim();
        LocalDate dueDate = dueDatePicker.getValue();

        if (selectedCourse == null || title.isEmpty() || dueDate == null) {
            showAlert(Alert.AlertType.ERROR, "All fields are required.");
            return;
        }

        try {
            assignmentDAO.createAssignment(selectedCourse.getCourseId(), title, dueDate, description);
            showAlert(Alert.AlertType.INFORMATION, "Assignment created successfully.");
            clearForm();
            loadAssignments();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
        Stage stage = (Stage) assignmentTitleField.getScene().getWindow();
        stage.close();
    }

    private void clearForm() {
        courseComboBox.getSelectionModel().clearSelection();
        assignmentTitleField.clear();
        assignmentDescriptionField.clear();
        dueDatePicker.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.showAndWait();
    }
}