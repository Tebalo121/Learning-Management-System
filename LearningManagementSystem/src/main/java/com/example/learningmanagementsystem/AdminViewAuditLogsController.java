package com.example.learningmanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdminViewAuditLogsController implements UserAwareController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<AuditLog> auditLogTable;
    @FXML private TableColumn<AuditLog, LocalDateTime> timestampColumn;
    @FXML private TableColumn<AuditLog, String> usernameColumn;
    @FXML private TableColumn<AuditLog, String> actionColumn;
    @FXML private TableColumn<AuditLog, String> detailsColumn;
    @FXML private TextField filterField;
    @FXML private DatePicker dateFilter;
    @FXML private Button backButton;
    @FXML private Button refreshButton;
    @FXML private Button exportButton;
    @FXML private Pagination pagination;

    private User user;
    private ObservableList<AuditLog> auditLogs;
    private FilteredList<AuditLog> filteredData;
    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";
    private static final int ROWS_PER_PAGE = 10;
    private static final String AUDIT_LOG_CSV_PATH = "audit_logs.csv"; // Adjust path as needed

    @Override
    public void initUserData(User user) {
        this.user = user;
        if (user != null) {
            welcomeLabel.setText("Welcome Admin " + user.getFullName());
            log("Initialized audit logs view for user: " + user.getUsername());
        } else {
            welcomeLabel.setText("Welcome Admin");
            log("⚠️ Warning: user is null in AdminViewAuditLogsController");
        }
        initializeTable();
    }

    @Override
    public void initializeWithUser(User user) {
        // Reserved for future use
    }

    @FXML
    public void initialize() {
        // Initialize table columns
        timestampColumn.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        actionColumn.setCellValueFactory(cellData -> cellData.getValue().actionProperty());
        detailsColumn.setCellValueFactory(cellData -> cellData.getValue().detailsProperty());

        // Enable sorting
        auditLogTable.setSortPolicy(param -> true);
    }

    private void initializeTable() {
        // Fetch audit logs
        auditLogs = FXCollections.observableArrayList(fetchAuditLogs());
        filteredData = new FilteredList<>(auditLogs, p -> true);

        // Add listener to filterField for text-based filtering
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(auditLog -> {
                boolean textMatch = newValue == null || newValue.isEmpty() ||
                        auditLog.getUsername().toLowerCase().contains(newValue.toLowerCase()) ||
                        auditLog.getAction().toLowerCase().contains(newValue.toLowerCase()) ||
                        auditLog.getDetails().toLowerCase().contains(newValue.toLowerCase());
                return textMatch && applyDateFilter(auditLog);
            });
            updatePagination();
        });

        // Add listener to dateFilter for date-based filtering
        dateFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(auditLog -> applyDateFilter(auditLog) &&
                    (filterField.getText() == null || filterField.getText().isEmpty() ||
                            auditLog.getUsername().toLowerCase().contains(filterField.getText().toLowerCase()) ||
                            auditLog.getAction().toLowerCase().contains(filterField.getText().toLowerCase()) ||
                            auditLog.getDetails().toLowerCase().contains(filterField.getText().toLowerCase())));
            updatePagination();
        });

        // Wrap FilteredList in SortedList for sorting
        SortedList<AuditLog> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(auditLogTable.comparatorProperty());

        // Set table items
        auditLogTable.setItems(sortedData);

        // Initialize pagination
        pagination.setPageFactory(pageIndex -> {
            updateTableItems(pageIndex);
            return auditLogTable;
        });
        updatePagination();
    }

    private boolean applyDateFilter(AuditLog auditLog) {
        LocalDate selectedDate = dateFilter.getValue();
        return selectedDate == null || auditLog.getTimestamp().toLocalDate().equals(selectedDate);
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) filteredData.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
        pagination.setCurrentPageIndex(0);
        updateTableItems(0);
    }

    private void updateTableItems(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredData.size());
        auditLogTable.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
    }

    private List<AuditLog> fetchAuditLogs() {
        List<AuditLog> logs = new ArrayList<>();

        // Try loading from CSV file first (your real user data)
        File csvFile = new File(AUDIT_LOG_CSV_PATH);
        if (csvFile.exists()) {
            logs = loadAuditLogsFromCSV();
            if (!logs.isEmpty()) {
                log("Loaded " + logs.size() + " audit logs from CSV");
                return logs;
            } else {
                log("⚠️ CSV file is empty or invalid, falling back to simulated data");
            }
        } else {
            log("⚠️ CSV file not found at " + AUDIT_LOG_CSV_PATH + ", falling back to simulated data");
        }

        // Fallback: Simulated realistic LMS data with generic usernames
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 23, 8, 30), "instructor_001", "Course Creation", "Created course: MATH202 - Linear Algebra"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 23, 9, 0), "student_001", "Course Enrollment", "Enrolled in course: MATH202 - Linear Algebra"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 23, 10, 15), "admin_001", "User Management", "Created new user account: student_002"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 23, 11, 30), "student_001", "Assignment Submission", "Submitted assignment: MATH202 Homework 1"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 23, 14, 0), "instructor_001", "Grade Update", "Graded assignment for student_001 in MATH202: 88%"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 24, 8, 45), "student_002", "Quiz Attempt", "Completed quiz: CS101 Quiz 1, Score: 75%"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 24, 9, 30), "instructor_002", "Course Update", "Updated syllabus for ENG202 - Creative Writing"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 24, 10, 0), "student_003", "Forum Post", "Posted in discussion: ENG202 Week 1 - Poetry"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 24, 12, 30), "admin_001", "System Configuration", "Updated grading policy for semester"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 24, 15, 15), "student_002", "Course Enrollment", "Enrolled in course: CS101 - Intro to Programming"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 25, 8, 0), "instructor_002", "Assignment Creation", "Created assignment: ENG202 Essay 1"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 25, 9, 45), "student_003", "Assignment Submission", "Submitted assignment: ENG202 Essay 1"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 25, 11, 0), "admin_002", "User Management", "Reset password for student_004"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 25, 13, 20), "instructor_001", "Grade Update", "Graded quiz for student_002 in CS101: 80%"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 26, 8, 30), "student_004", "Forum Post", "Posted in discussion: CS101 Week 2 - Loops"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 26, 10, 15), "instructor_001", "Course Content Upload", "Uploaded lecture notes: MATH202 Lecture 2"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 26, 12, 0), "student_001", "Quiz Attempt", "Completed quiz: MATH202 Quiz 1, Score: 90%"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 26, 14, 45), "admin_001", "System Maintenance", "Performed system update to version 2.1.3"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 27, 9, 0), "instructor_002", "Course Update", "Updated assignment due date for ENG202 Essay 1"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 27, 11, 15), "student_002", "Assignment Submission", "Submitted assignment: CS101 Lab 1"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 27, 13, 30), "instructor_002", "Forum Response", "Responded to student_003 in ENG202 discussion"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 28, 8, 15), "student_004", "Course Enrollment", "Enrolled in course: PHY202 - Mechanics"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 28, 10, 0), "instructor_001", "Grade Update", "Graded quiz for student_001 in MATH202: 85%"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 28, 12, 0), "admin_001", "Audit Log View", "Viewed audit logs for May 2025"));
        logs.add(new AuditLog(LocalDateTime.of(2025, 5, 28, 15, 45), "student_003", "Quiz Attempt", "Completed quiz: ENG202 Quiz 2, Score: 82%"));

        return logs;
    }

    private List<AuditLog> loadAuditLogsFromCSV() {
        List<AuditLog> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(AUDIT_LOG_CSV_PATH))) {
            String line = reader.readLine(); // Skip header
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4); // Split into timestamp, username, action, details
                if (parts.length == 4) {
                    try {
                        LocalDateTime timestamp = LocalDateTime.parse(parts[0].trim(), formatter);
                        String username = parts[1].trim();
                        String action = parts[2].trim();
                        String details = parts[3].trim().replace(";", ",");
                        logs.add(new AuditLog(timestamp, username, action, details));
                    } catch (Exception e) {
                        log("⚠️ Failed to parse CSV line: " + line + " - Error: " + e.getMessage());
                    }
                } else {
                    log("⚠️ Invalid CSV line format: " + line);
                }
            }
        } catch (IOException e) {
            log("❌ Failed to load audit logs from CSV: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load audit logs from CSV: " + e.getMessage());
        }
        return logs;
    }

    @FXML
    private void handleBack() {
        log("Returning to Admin Dashboard");
        switchScene("admin_home.fxml");
    }

    @FXML
    private void handleRefresh() {
        log("Refreshing audit logs");
        auditLogs.setAll(fetchAuditLogs());
        filteredData = new FilteredList<>(auditLogs, p -> true);
        updatePagination();
        auditLogTable.refresh();
    }

    @FXML
    private void handleExport() {
        log("Exporting audit logs to CSV");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Audit Logs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(auditLogTable.getScene().getWindow());
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Timestamp,Username,Action,Details\n");
                for (AuditLog log : auditLogs) {
                    writer.write(String.format("%s,%s,%s,%s\n",
                            log.getTimestamp().toString(),
                            log.getUsername(),
                            log.getAction(),
                            log.getDetails().replace(",", ";")));
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", "Audit logs exported successfully to " + file.getAbsolutePath());
            } catch (IOException e) {
                log("❌ Failed to export audit logs: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to export audit logs: " + e.getMessage());
            }
        }
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
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Learning Management System - Admin Dashboard");
            stage.show();
        } catch (IOException e) {
            log("❌ Failed to load FXML: " + fxmlName);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void log(String action) {
        String logMessage = String.format("[%s] %s%s",
                LocalDateTime.now(),
                action,
                user != null ? " for: " + user.getUsername() : " (user not initialized)");
        System.out.println(logMessage);
    }
}