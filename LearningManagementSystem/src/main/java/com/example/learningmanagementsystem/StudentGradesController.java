package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.SubmissionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class StudentGradesController implements UserAwareController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<Grade> gradesTable;
    @FXML private TableColumn<Grade, String> colCourse;
    @FXML private TableColumn<Grade, String> colAssignment;
    @FXML private TableColumn<Grade, String> colGrade;

    private User user;
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    @Override
    public void initUserData(User user) {
        this.user = user;
        welcomeLabel.setText("Grades for: " + user.getFullName());

        colCourse.setCellValueFactory(data -> data.getValue().courseNameProperty());
        colAssignment.setCellValueFactory(data -> data.getValue().assignmentNameProperty());
        colGrade.setCellValueFactory(data -> data.getValue().gradeValueProperty());

        loadGradesFromDatabase();
    }

    private void loadGradesFromDatabase() {
        List<Grade> grades = submissionDAO.getGradesForStudent(user.getUserId());
        gradesTable.setItems(FXCollections.observableArrayList(grades));
    }

    @Override
    public void initializeWithUser(User user) {
        initUserData(user);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/learningmanagementsystem/student-home.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAware) {
                userAware.initUserData(user);
            }

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Student Home");
        } catch (IOException e) {
            System.err.println("❌ Failed to load student-home.fxml");
            e.printStackTrace();
        }
    }
}