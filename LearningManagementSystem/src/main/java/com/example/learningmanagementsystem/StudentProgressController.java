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
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class StudentProgressController implements UserAwareController {

    // UI Components
    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> courseComboBox;
    @FXML private TableView<Enrollment> progressTable;
    @FXML private TableColumn<Enrollment, String> courseColumn;
    @FXML private TableColumn<Enrollment, Float> test1Column;
    @FXML private TableColumn<Enrollment, Float> midtermColumn;
    @FXML private TableColumn<Enrollment, Float> testAssignmentColumn;
    @FXML private TableColumn<Enrollment, Float> finalExamColumn;
    @FXML private TableColumn<Enrollment, Float> progressColumn;
    @FXML private TableColumn<Enrollment, String> statusColumn;
    @FXML private TableColumn<Enrollment, ProgressBar> progressBarColumn;
    @FXML private Button exportButton;
    @FXML private Button backButton;

    // Data
    private ObservableList<Enrollment> enrollments = FXCollections.observableArrayList();
    private ObservableList<String> courses = FXCollections.observableArrayList();
    private User user;

    @Override
    public void initUserData(User user) {
        this.user = user;
        if (user != null) {
            welcomeLabel.setText("Welcome " + user.getFullName());
            System.out.println("StudentProgressController initialized with user: " + user.getUsername());
            if (courseComboBox == null || progressTable == null) {
                System.err.println("❌ UI components not initialized: courseComboBox=" + courseComboBox + ", progressTable=" + progressTable);
                showAlert("Error", "UI components failed to initialize. Check FXML configuration.");
                return;
            }
            initializeTable();
            loadCourses();
            loadProgressData();
        } else {
            welcomeLabel.setText("Welcome (Unknown)");
            System.err.println("⚠️ Warning: user is null in StudentProgressController");
            showAlert("Error", "No user data provided.");
        }
    }

    @Override
    public void initializeWithUser(User user) {
        // Reserved for future use
    }

    private void initializeTable() {
        if (courseColumn == null || test1Column == null || midtermColumn == null ||
                testAssignmentColumn == null || finalExamColumn == null ||
                progressColumn == null || statusColumn == null || progressBarColumn == null) {
            System.err.println("❌ One or more TableColumns are null. Check FXML file: student_progress.fxml");
            showAlert("Error", "Table columns failed to initialize. Check FXML configuration.");
            return;
        }
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        test1Column.setCellValueFactory(new PropertyValueFactory<>("test1"));
        midtermColumn.setCellValueFactory(new PropertyValueFactory<>("midterm"));
        testAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("testAssignment"));
        finalExamColumn.setCellValueFactory(new PropertyValueFactory<>("finalExam"));
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        progressBarColumn.setCellFactory(column -> new TableCell<Enrollment, ProgressBar>() {
            private final ProgressBar progressBar = new ProgressBar(0);
            {
                progressBar.setStyle("-fx-accent: #4CAF50;");
                progressBar.setPrefWidth(140);
            }
            @Override
            protected void updateItem(ProgressBar item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Enrollment enrollment = getTableRow().getItem();
                    progressBar.setProgress(enrollment.getProgress() / 100.0);
                    setGraphic(progressBar);
                }
            }
        });
        progressTable.setItems(enrollments);
        System.out.println("Table initialized successfully");
    }

    private void loadCourses() {
        courses.clear();
        courses.add("All Courses");
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT DISTINCT course_name FROM enrollments WHERE student_name = ?")) {
            stmt.setString(1, user.getFullName());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String courseName = rs.getString("course_name");
                if (courseName != null && !courses.contains(courseName)) {
                    courses.add(courseName);
                }
            }
            courseComboBox.setItems(courses);
            courseComboBox.getSelectionModel().selectFirst();
            System.out.println("Loaded " + (courses.size() - 1) + " courses for filtering");
        } catch (SQLException e) {
            handleSQLException(e, "load courses");
        }
    }

    private void loadProgressData() {
        enrollments.clear();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT enrollment_id, student_name, course_name, test1, midterm, test_assignment, final_exam, progress, status " +
                             "FROM enrollments WHERE student_name = ?")) {
            stmt.setString(1, user.getFullName());
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
            progressTable.setItems(enrollments);
            progressTable.refresh();
            System.out.println("Loaded " + enrollments.size() + " enrollment records for student: " + user.getFullName());
        } catch (SQLException e) {
            handleSQLException(e, "load progress data");
        }
    }

    @FXML
    private void handleFilter() {
        String selectedCourse = courseComboBox.getValue();
        if (selectedCourse == null || selectedCourse.equals("All Courses")) {
            progressTable.setItems(enrollments);
        } else {
            ObservableList<Enrollment> filtered = enrollments.filtered(e -> Objects.equals(e.getCourseName(), selectedCourse));
            progressTable.setItems(filtered);
        }
        progressTable.refresh();
        System.out.println("Filtered by course: " + (selectedCourse != null ? selectedCourse : "All Courses"));
    }

    @FXML
    private void handleExport() {
        try (FileWriter writer = new FileWriter("student_progress_" + user.getUsername() + ".csv")) {
            writer.write("Course,Test 1,Midterm,Test/Assignment,Final Exam,Progress (%),Status\n");
            for (Enrollment e : progressTable.getItems()) {
                writer.write(String.format("%s,%.1f,%.1f,%.1f,%.1f,%.1f,%s\n",
                        e.getCourseName(), e.getTest1(), e.getMidterm(), e.getTestAssignment(),
                        e.getFinalExam(), e.getProgress(), e.getStatus()));
            }
            showAlert("Success", "Progress exported to student_progress_" + user.getUsername() + ".csv");
            System.out.println("Exported progress to CSV for user: " + user.getUsername());
        } catch (IOException e) {
            handleIOException(e, "export to CSV");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/learningmanagementsystem/student_home.fxml"));
            Parent root = loader.load();
            UserAwareController controller = loader.getController();
            if (user != null) {
                controller.initUserData(user);
            }
            Stage stage = (Stage) progressTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Navigated back to student dashboard");
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
}