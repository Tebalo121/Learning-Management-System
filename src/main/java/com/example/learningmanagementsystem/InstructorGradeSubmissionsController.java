package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.SubmissionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;

public class InstructorGradeSubmissionsController implements UserAwareController {
    @FXML private TableView<Submission> submissionTable;
    @FXML private TableColumn<Submission, String> studentNameColumn;
    @FXML private TableColumn<Submission, String> assignmentTitleColumn;
    @FXML private TableColumn<Submission, String> submissionDateColumn;
    @FXML private TableColumn<Submission, String> gradeColumn;
    @FXML private TextField gradeField;

    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final ObservableList<Submission> submissionList = FXCollections.observableArrayList();
    private User instructor;

    @Override
    public void initUserData(User user) {
        this.instructor = user;
        setupTable();
        loadSubmissions();
    }

    @Override
    public void initializeWithUser(User user) {

    }

    private void setupTable() {
        studentNameColumn.setCellValueFactory(cell -> cell.getValue().studentNameProperty());
        assignmentTitleColumn.setCellValueFactory(cell -> cell.getValue().assignmentTitleProperty());
        submissionDateColumn.setCellValueFactory(cell -> cell.getValue().submissionDateProperty());
        gradeColumn.setCellValueFactory(cell -> cell.getValue().gradeProperty());
        submissionTable.setItems(submissionList);
    }

    private void loadSubmissions() {
        try {
            submissionList.setAll(submissionDAO.getSubmissionsByInstructor(instructor.getUserId()));
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleGradeSubmission() {
        Submission selected = submissionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a submission to grade.");
            return;
        }

        String grade = gradeField.getText().trim();
        if (grade.isEmpty()) {
            showAlert("Error", "Grade cannot be empty.");
            return;
        }

        try {
            submissionDAO.updateGrade(selected.getSubmissionId(), grade);
            showAlert("Success", "Grade saved successfully.");
            loadSubmissions();
            gradeField.clear();
        } catch (SQLException e) {
            showAlert("Error", "Failed to update grade: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewSubmission() {
        Submission selected = submissionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Viewing submission for: " + selected.getTitle());
            // Optionally show file content or open new window
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}