package com.example.learningmanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AdminManageEnrollmentsController implements UserAwareController, Initializable {

    // UI Components
    @FXML private Label welcomeLabel;
    @FXML private TableView<Enrollment> enrollmentsTable;
    @FXML private TableColumn<Enrollment, Integer> enrollmentIdColumn;
    @FXML private TableColumn<Enrollment, String> studentColumn;
    @FXML private TableColumn<Enrollment, String> courseColumn;
    @FXML private TableColumn<Enrollment, Float> test1Column;
    @FXML private TableColumn<Enrollment, Float> midtermColumn;
    @FXML private TableColumn<Enrollment, Float> testAssignmentColumn;
    @FXML private TableColumn<Enrollment, Float> finalExamColumn;
    @FXML private TableColumn<Enrollment, Float> progressColumn;
    @FXML private TableColumn<Enrollment, String> statusColumn;
    @FXML private ComboBox<String> studentComboBox;
    @FXML private ComboBox<String> courseComboBox;
    @FXML private TextField test1Field;
    @FXML private TextField midtermField;
    @FXML private TextField testAssignmentField;
    @FXML private TextField finalExamField;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;

    // Data
    private ObservableList<Enrollment> enrollments = FXCollections.observableArrayList();
    private ObservableList<String> studentNames = FXCollections.observableArrayList();
    private ObservableList<String> courseNames = FXCollections.observableArrayList();
    private User user;
    private Enrollment selectedEnrollment;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing AdminManageEnrollmentsController");
        System.out.println("studentComboBox: " + (studentComboBox != null ? "Initialized" : "Null"));
        System.out.println("courseComboBox: " + (courseComboBox != null ? "Initialized" : "Null"));
    }

    @Override
    public void initUserData(User user) {
        this.user = user;
        if (user != null) {
            welcomeLabel.setText("Welcome Admin " + user.getFullName());
            System.out.println("AdminManageEnrollmentsController initialized with user: " + user.getUsername());
            if (studentComboBox == null || courseComboBox == null) {
                System.err.println("❌ ComboBox not initialized: studentComboBox=" + studentComboBox + ", courseComboBox=" + courseComboBox);
                showAlert("Error", "UI components failed to initialize. Check FXML configuration.");
                return;
            }
            loadStudents();
            loadCourses();
            initializeTable();
            loadEnrollments();
        } else {
            welcomeLabel.setText("Welcome Admin (Unknown)");
            System.err.println("⚠️ Warning: user is null in AdminManageEnrollmentsController");
            showAlert("Error", "No user data provided.");
        }
    }

    @Override
    public void initializeWithUser(User user) {
        // Reserved for future use
    }

    private void initializeTable() {
        if (enrollmentIdColumn == null || studentColumn == null || courseColumn == null ||
                test1Column == null || midtermColumn == null || testAssignmentColumn == null ||
                finalExamColumn == null || progressColumn == null || statusColumn == null) {
            System.err.println("❌ One or more TableColumns are null. Check FXML file: admin_manage_enrollments.fxml");
            showAlert("Error", "Table columns failed to initialize. Check FXML configuration.");
            return;
        }
        enrollmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentId"));
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        test1Column.setCellValueFactory(new PropertyValueFactory<>("test1"));
        midtermColumn.setCellValueFactory(new PropertyValueFactory<>("midterm"));
        testAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("testAssignment"));
        finalExamColumn.setCellValueFactory(new PropertyValueFactory<>("finalExam"));
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        enrollmentsTable.setItems(enrollments);

        // Selection listener to populate fields
        enrollmentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEnrollment = newSelection;
                populateFields(newSelection);
            } else {
                clearFields();
            }
        });
        System.out.println("Table initialized successfully");
    }

    private void loadStudents() {
        studentNames.clear();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT full_name FROM users WHERE role = 'student'")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("full_name");
                if (name != null && !studentNames.contains(name)) {
                    studentNames.add(name);
                }
            }
            if (studentComboBox == null) {
                System.err.println("❌ studentComboBox is null in loadStudents");
                showAlert("Error", "Student ComboBox failed to initialize.");
                return;
            }
            studentComboBox.setItems(studentNames);
            System.out.println("Loaded " + studentNames.size() + " students");
        } catch (SQLException e) {
            handleSQLException(e, "load students");
        }
    }

    private void loadCourses() {
        courseNames.clear();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT course_name FROM courses")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("course_name");
                if (name != null && !courseNames.contains(name)) {
                    courseNames.add(name);
                }
            }
            if (courseComboBox == null) {
                System.err.println("❌ courseComboBox is null in loadCourses");
                showAlert("Error", "Course ComboBox failed to initialize.");
                return;
            }
            courseComboBox.setItems(courseNames);
            System.out.println("Loaded " + courseNames.size() + " courses");
        } catch (SQLException e) {
            handleSQLException(e, "load courses");
        }
    }

    private void loadEnrollments() {
        enrollments.clear();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT enrollment_id, student_name, course_name, test1, midterm, test_assignment, final_exam, progress, status " +
                             "FROM enrollments")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                enrollments.add(new Enrollment(
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
            enrollmentsTable.setItems(enrollments);
            enrollmentsTable.refresh();
            System.out.println("Loaded " + enrollments.size() + " enrollment records");
        } catch (SQLException e) {
            handleSQLException(e, "load enrollments");
        }
    }

    @FXML
    private void handleAddEnrollment() {
        try {
            String studentName = studentComboBox.getValue();
            String courseName = courseComboBox.getValue();
            if (studentName == null || courseName == null) {
                showAlert("Validation Error", "Please select a student and a course.");
                return;
            }
            if (!isValidStudent(studentName)) {
                showAlert("Error", "Student '" + studentName + "' does not exist.");
                return;
            }
            if (!isValidCourse(courseName)) {
                showAlert("Error", "Course '" + courseName + "' does not exist.");
                return;
            }
            int enrollmentId = Integer.parseInt(generateUniqueEnrollmentId());
            float test1 = parseMark(test1Field.getText());
            float midterm = parseMark(midtermField.getText());
            float testAssignment = parseMark(testAssignmentField.getText());
            float finalExam = parseMark(finalExamField.getText());
            float progress = calculateProgress(test1, midterm, testAssignment, finalExam);
            String status = progress >= 50 ? "Pass" : "Fail";

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO enrollments (enrollment_id, student_name, course_name, test1, midterm, test_assignment, final_exam, progress, status) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                stmt.setInt(1, enrollmentId);
                stmt.setString(2, studentName);
                stmt.setString(3, courseName);
                stmt.setFloat(4, test1);
                stmt.setFloat(5, midterm);
                stmt.setFloat(6, testAssignment);
                stmt.setFloat(7, finalExam);
                stmt.setFloat(8, progress);
                stmt.setString(9, status);
                stmt.executeUpdate();

                enrollments.add(new Enrollment(enrollmentId, studentName, courseName, test1, midterm, testAssignment, finalExam, progress, status));
                enrollmentsTable.refresh();
                clearFields();
                showAlert("Success", "Enrollment added successfully.");
                System.out.println("Added enrollment ID: " + enrollmentId);
            }
        } catch (SQLException e) {
            handleSQLException(e, "add enrollment");
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Marks must be valid numbers between 0 and 100.");
        }
    }

    @FXML
    private void handleUpdateEnrollment() {
        if (selectedEnrollment == null) {
            showAlert("Error", "Please select an enrollment to update.");
            return;
        }
        try {
            String studentName = studentComboBox.getValue();
            String courseName = courseComboBox.getValue();
            if (studentName == null || courseName == null) {
                showAlert("Validation Error", "Please select a student and a course.");
                return;
            }
            if (!isValidStudent(studentName)) {
                showAlert("Error", "Student '" + studentName + "' does not exist.");
                return;
            }
            if (!isValidCourse(courseName)) {
                showAlert("Error", "Course '" + courseName + "' does not exist.");
                return;
            }
            float test1 = parseMark(test1Field.getText());
            float midterm = parseMark(midtermField.getText());
            float testAssignment = parseMark(testAssignmentField.getText());
            float finalExam = parseMark(finalExamField.getText());
            float progress = calculateProgress(test1, midterm, testAssignment, finalExam);
            String status = progress >= 50 ? "Pass" : "Fail";

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE enrollments SET student_name = ?, course_name = ?, test1 = ?, midterm = ?, test_assignment = ?, final_exam = ?, progress = ?, status = ? " +
                                 "WHERE enrollment_id = ?")) {
                stmt.setString(1, studentName);
                stmt.setString(2, courseName);
                stmt.setFloat(3, test1);
                stmt.setFloat(4, midterm);
                stmt.setFloat(5, testAssignment);
                stmt.setFloat(6, finalExam);
                stmt.setFloat(7, progress);
                stmt.setString(8, status);
                stmt.setInt(9, selectedEnrollment.getEnrollmentId());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    showAlert("Error", "No enrollment found with ID: " + selectedEnrollment.getEnrollmentId());
                    System.err.println("No rows updated for enrollment_id: " + selectedEnrollment.getEnrollmentId());
                    return;
                }

                int index = enrollments.indexOf(selectedEnrollment);
                if (index >= 0) {
                    Enrollment updatedEnrollment = new Enrollment(
                            selectedEnrollment.getEnrollmentId(),
                            studentName,
                            courseName,
                            test1, midterm, testAssignment, finalExam,
                            progress, status
                    );
                    enrollments.set(index, updatedEnrollment);
                    enrollmentsTable.refresh();
                    clearFields();
                    showAlert("Success", "Enrollment updated successfully.");
                    System.out.println("Updated enrollment ID: " + selectedEnrollment.getEnrollmentId());
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "update enrollment ID: " + (selectedEnrollment != null ? selectedEnrollment.getEnrollmentId() : "null"));
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Marks must be valid numbers between 0 and 100.");
        }
    }

    @FXML
    private void handleDeleteEnrollment() {
        if (selectedEnrollment == null) {
            showAlert("Error", "Please select an enrollment to delete.");
            return;
        }
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM enrollments WHERE enrollment_id = ?")) {
            stmt.setInt(1, selectedEnrollment.getEnrollmentId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                showAlert("Error", "No enrollment found with ID: " + selectedEnrollment.getEnrollmentId());
                System.err.println("No rows deleted for enrollment_id: " + selectedEnrollment.getEnrollmentId());
                return;
            }
            enrollments.remove(selectedEnrollment);
            enrollmentsTable.refresh();
            clearFields();
            showAlert("Success", "Enrollment deleted successfully.");
            System.out.println("Deleted enrollment ID: " + selectedEnrollment.getEnrollmentId());
        } catch (SQLException e) {
            handleSQLException(e, "delete enrollment ID: " + (selectedEnrollment != null ? selectedEnrollment.getEnrollmentId() : "null"));
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/learningmanagementsystem/admin_home.fxml"));
            Parent root = loader.load();
            UserAwareController controller = loader.getController();
            if (user != null) {
                controller.initUserData(user);
            }
            Stage stage = (Stage) enrollmentsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Navigated back to admin dashboard");
        } catch (IOException e) {
            handleIOException(e, "navigate back");
        }
    }

    private String generateUniqueEnrollmentId() throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(enrollment_id) FROM enrollments")) {
            if (rs.next()) {
                return String.valueOf(rs.getInt(1) + 1);
            }
            return "1";
        }
    }

    private boolean isValidStudent(String fullName) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM users WHERE full_name = ? AND role = 'student'")) {
            stmt.setString(1, fullName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            handleSQLException(e, "validate student: " + fullName);
            return false;
        }
    }

    private boolean isValidCourse(String courseName) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM courses WHERE course_name = ?")) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            handleSQLException(e, "validate course: " + courseName);
            return false;
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

    private void populateFields(Enrollment enrollment) {
        studentComboBox.setValue(enrollment.getStudentName());
        courseComboBox.setValue(enrollment.getCourseName());
        test1Field.setText(String.valueOf(enrollment.getTest1()));
        midtermField.setText(String.valueOf(enrollment.getMidterm()));
        testAssignmentField.setText(String.valueOf(enrollment.getTestAssignment()));
        finalExamField.setText(String.valueOf(enrollment.getFinalExam()));
    }

    private void clearFields() {
        selectedEnrollment = null;
        studentComboBox.getSelectionModel().clearSelection();
        courseComboBox.getSelectionModel().clearSelection();
        test1Field.clear();
        midtermField.clear();
        testAssignmentField.clear();
        finalExamField.clear();
        enrollmentsTable.getSelectionModel().clearSelection();
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
}