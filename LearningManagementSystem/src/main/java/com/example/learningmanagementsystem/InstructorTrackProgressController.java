package com.example.learningmanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class InstructorTrackProgressController implements UserAwareController {

    // UI Components
    @FXML private Label welcomeLabel;
    @FXML private TableView<Enrollment> progressTable;
    @FXML private TableColumn<Enrollment, String> studentColumn;
    @FXML private TableColumn<Enrollment, String> courseColumn;
    @FXML private TableColumn<Enrollment, Float> test1Column;
    @FXML private TableColumn<Enrollment, Float> midtermColumn;
    @FXML private TableColumn<Enrollment, Float> testAssignmentColumn;
    @FXML private TableColumn<Enrollment, Float> finalExamColumn;
    @FXML private TableColumn<Enrollment, Float> progressColumn;
    @FXML private TableColumn<Enrollment, String> statusColumn;
    @FXML private TextField filterField;
    @FXML private TextField test1Field;
    @FXML private TextField midtermField;
    @FXML private TextField testAssignmentField;
    @FXML private TextField finalExamField;
    @FXML private Button saveMarksButton;
    @FXML private Button backButton;

    // Data
    private ObservableList<Enrollment> allProgress = FXCollections.observableArrayList();
    private ObservableList<Enrollment> filteredProgress = FXCollections.observableArrayList();
    private User user;
    private Enrollment selectedEnrollment = null;

    @Override
    public void initUserData(User user) {
        this.user = user;
        if (user != null) {
            welcomeLabel.setText("Welcome Instructor " + user.getFullName());
            System.out.println("InstructorTrackProgressController initialized with user: " + user.getUsername());
            if (progressTable == null || filterField == null) {
                System.err.println("❌ UI components not initialized: progressTable=" + progressTable + ", filterField=" + filterField);
                showAlert("Error", "UI components failed to initialize. Check FXML configuration.");
                return;
            }
            initializeTable();
            loadProgressData();
        } else {
            welcomeLabel.setText("Welcome Instructor (Unknown)");
            System.err.println("⚠️ Warning: user is null in InstructorTrackProgressController");
            showAlert("Error", "No user data provided.");
        }
    }

    @Override
    public void initializeWithUser(User user) {
        // Reserved for future use
    }

    private void initializeTable() {
        if (studentColumn == null || courseColumn == null || test1Column == null ||
                midtermColumn == null || testAssignmentColumn == null || finalExamColumn == null ||
                progressColumn == null || statusColumn == null) {
            System.err.println("❌ One or more TableColumns are null. Check FXML file: instructor_track_progress.fxml");
            showAlert("Error", "Table columns failed to initialize. Check FXML configuration.");
            return;
        }
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        test1Column.setCellValueFactory(new PropertyValueFactory<>("test1"));
        midtermColumn.setCellValueFactory(new PropertyValueFactory<>("midterm"));
        testAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("testAssignment"));
        finalExamColumn.setCellValueFactory(new PropertyValueFactory<>("finalExam"));
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        progressTable.setItems(filteredProgress);

        // Add selection listener to populate mark fields
        progressTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEnrollment = newSelection;
                populateMarkFields(newSelection);
            } else {
                clearMarkFields();
            }
        });
        System.out.println("Table initialized successfully");
    }

    private void loadProgressData() {
        allProgress.clear();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT e.enrollment_id, e.student_name, e.course_name, e.test1, e.midterm, " +
                             "e.test_assignment, e.final_exam, e.progress, e.status " +
                             "FROM enrollments e " +
                             "JOIN courses c ON e.course_name = c.course_name " +
                             "WHERE c.instructor_id = ?")) {
            stmt.setInt(1, user.getUserId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                allProgress.add(new Enrollment(
                        rs.getInt("enrollment_id"),
                        rs.getString("student_name"),
                        rs.getString("course_name"),
                        rs.getFloat("test1"),
                        rs.getFloat("midterm"),
                        rs.getFloat("test_assignment"),
                        rs.getFloat("final_exam"),
                        rs.getFloat("progress"),
                        rs.getString("status")
                ));
            }
            filteredProgress.setAll(allProgress);
            progressTable.setItems(filteredProgress);
            progressTable.refresh();
            System.out.println("Loaded " + allProgress.size() + " enrollment records for instructor ID: " + user.getUserId());
        } catch (SQLException e) {
            handleSQLException(e, "load progress data");
        }
    }

    @FXML
    private void handleFilter() {
        String filterText = filterField.getText().toLowerCase().trim();
        if (filterText.isEmpty()) {
            filteredProgress.setAll(allProgress);
        } else {
            filteredProgress.setAll(allProgress.filtered(enrollment ->
                    enrollment.getStudentName().toLowerCase().contains(filterText) ||
                            enrollment.getCourseName().toLowerCase().contains(filterText)));
        }
        progressTable.setItems(filteredProgress);
        progressTable.refresh();
        System.out.println("Filtered to " + filteredProgress.size() + " records");
    }

    @FXML
    private void handleSaveMarks() {
        if (selectedEnrollment == null) {
            showAlert("Error", "Please select an enrollment to update marks.");
            return;
        }
        try {
            float test1 = parseMark(test1Field.getText());
            float midterm = parseMark(midtermField.getText());
            float testAssignment = parseMark(testAssignmentField.getText());
            float finalExam = parseMark(finalExamField.getText());
            float progress = calculateProgress(test1, midterm, testAssignment, finalExam);
            String status = progress >= 50 ? "Pass" : "Fail";

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE enrollments SET test1 = ?, midterm = ?, test_assignment = ?, final_exam = ?, progress = ?, status = ? " +
                                 "WHERE enrollment_id = ?")) {
                stmt.setFloat(1, test1);
                stmt.setFloat(2, midterm);
                stmt.setFloat(3, testAssignment);
                stmt.setFloat(4, finalExam);
                stmt.setFloat(5, progress);
                stmt.setString(6, status);
                stmt.setInt(7, selectedEnrollment.getEnrollmentId());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    showAlert("Error", "No enrollment found with ID: " + selectedEnrollment.getEnrollmentId());
                    System.err.println("No rows updated for enrollment_id: " + selectedEnrollment.getEnrollmentId());
                    return;
                }

                int index = allProgress.indexOf(selectedEnrollment);
                if (index >= 0) {
                    Enrollment updatedEnrollment = new Enrollment(
                            selectedEnrollment.getEnrollmentId(),
                            selectedEnrollment.getStudentName(),
                            selectedEnrollment.getCourseName(),
                            test1, midterm, testAssignment, finalExam,
                            progress, status
                    );
                    allProgress.set(index, updatedEnrollment);
                    filteredProgress.setAll(allProgress);
                    progressTable.setItems(filteredProgress);
                    progressTable.refresh();
                    clearMarkFields();
                    log("Updated marks for enrollment ID: " + selectedEnrollment.getEnrollmentId());
                    showAlert("Success", "Marks updated successfully for " + selectedEnrollment.getStudentName());
                } else {
                    showAlert("Error", "Failed to update table: Enrollment not found in list.");
                    System.err.println("Enrollment not found in allProgress list for ID: " + selectedEnrollment.getEnrollmentId());
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "save marks for enrollment ID: " + (selectedEnrollment != null ? selectedEnrollment.getEnrollmentId() : "null"));
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Marks must be valid numbers between 0 and 100.");
        }
    }

    private float parseMark(String text) throws NumberFormatException {
        if (text == null || text.trim().isEmpty()) {
            return 0.0f;
        }
        float value = Float.parseFloat(text);
        if (value < 0 || value > 100) {
            throw new NumberFormatException("Mark must be between 0 and 100");
        }
        return value;
    }

    private float calculateProgress(float test1, float midterm, float testAssignment, float finalExam) {
        return (test1 * 0.20f + midterm * 0.30f + testAssignment * 0.20f + finalExam * 0.30f);
    }

    private void populateMarkFields(Enrollment enrollment) {
        test1Field.setText(String.valueOf(enrollment.getTest1()));
        midtermField.setText(String.valueOf(enrollment.getMidterm()));
        testAssignmentField.setText(String.valueOf(enrollment.getTestAssignment()));
        finalExamField.setText(String.valueOf(enrollment.getFinalExam()));
    }

    private void clearMarkFields() {
        selectedEnrollment = null;
        test1Field.clear();
        midtermField.clear();
        testAssignmentField.clear();
        finalExamField.clear();
        progressTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/learningmanagementsystem/instructor_home.fxml"));
            Parent root = loader.load();
            UserAwareController controller = loader.getController();
            if (user != null) {
                controller.initUserData(user);
            }
            Stage stage = (Stage) progressTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Navigated back to instructor dashboard");
        } catch (IOException e) {
            handleIOException(e, "navigate back");
        }
    }

    private void handleSQLException(SQLException e, String operation) {
        String message = "Failed to " + operation + ": " + e.getMessage() + ", SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode();
        System.err.println("❌ " + message);
        e.printStackTrace();
        showAlert("Database Error", message);
    }

    private void handleIOException(IOException e, String operation) {
        String message = "Failed to " + operation + ": " + e.getMessage();
        System.err.println("❌ " + message);
        showAlert("Navigation Error", message);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.contains("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void log(String action) {
        System.out.println(action + " for: " + (user != null ? user.getUsername() : "user not initialized"));
    }
}