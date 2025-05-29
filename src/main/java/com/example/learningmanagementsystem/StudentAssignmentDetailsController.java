package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.SubmissionDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.SQLException;

public class StudentAssignmentDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label dueDateLabel;
    @FXML private TextArea answerTextArea;
    @FXML private Label fileLabel;

    private Assignment assignment;
    private User student;
    private File selectedFile;

    // Called externally by controller
    public void initAssignmentDetails(Assignment assignment, User student) {
        initData(student, assignment);
    }

    // Shared internal initialization
    public void initData(User student, Assignment assignment) {
        this.student = student;
        this.assignment = assignment;

        titleLabel.setText(assignment.getTitle());
        descriptionLabel.setText(assignment.getDescription());
        dueDateLabel.setText("Due: " + assignment.getDueDate());
    }

    @FXML
    private void handleUploadFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select File");
        selectedFile = chooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleSubmit() {
        String content = answerTextArea.getText().trim();
        String filePath = null;

        if (content.isEmpty() && selectedFile == null) {
            showAlert("Please write an answer or upload a file.");
            return;
        }

        if (selectedFile != null) {
            try {
                Path dest = Paths.get("uploads", selectedFile.getName());
                Files.createDirectories(dest.getParent());
                Files.copy(selectedFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                filePath = dest.toString();
            } catch (IOException e) {
                showAlert("Failed to upload file: " + e.getMessage());
                return;
            }
        }

        try {
            SubmissionDAO dao = new SubmissionDAO();
            dao.submitAssignment(student.getUserId(), assignment.getAssignmentId(), content, filePath);
            showAlert("Assignment submitted successfully!");
        } catch (SQLException e) {
            showAlert("Submission failed: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}