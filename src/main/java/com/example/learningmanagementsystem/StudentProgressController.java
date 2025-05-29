package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.ProgressDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class StudentProgressController implements UserAwareController {

    @FXML private Label welcomeLabel;
    @FXML private ProgressBar overallProgressBar;
    @FXML private Label progressPercentageLabel;
    @FXML private ListView<String> courseProgressList;

    private User user;
    private final ProgressDAO progressDAO = new ProgressDAO();

    @Override
    public void initUserData(User user) {
        this.user = user;
        welcomeLabel.setText("Progress for: " + user.getFullName());
        loadProgress();
    }

    @Override
    public void initializeWithUser(User user) {}

    private void loadProgress() {
        try {
            Map<String, Double> courseProgress = progressDAO.getCourseProgressForStudent(user.getUserId());

            double total = 0;
            int count = 0;
            courseProgressList.getItems().clear();

            for (Map.Entry<String, Double> entry : courseProgress.entrySet()) {
                String course = entry.getKey();
                double progress = entry.getValue();
                courseProgressList.getItems().add(course + ": " + String.format("%.2f", progress) + "%");
                total += progress;
                count++;
            }

            double avg = count == 0 ? 0 : total / count;
            overallProgressBar.setProgress(avg / 100);
            progressPercentageLabel.setText("Overall: " + String.format("%.2f", avg) + "%");

        } catch (SQLException e) {
            showAlert("Failed to load progress: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/learningmanagementsystem/student-home.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserAwareController) {
                ((UserAwareController) controller).initUserData(user);
            }

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load student home screen.");
        }
    }


    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}