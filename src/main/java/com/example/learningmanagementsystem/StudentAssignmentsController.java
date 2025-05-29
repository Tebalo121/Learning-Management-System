package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.AssignmentDAO;
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

public class StudentAssignmentsController implements UserAwareController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private TableView<Assignment> assignmentTable;
    @FXML
    private TableColumn<Assignment, String> colTitle;
    @FXML
    private TableColumn<Assignment, String> colDueDate;
    @FXML
    private TableColumn<Assignment, String> colStatus;

    private final AssignmentDAO assignmentDAO = new AssignmentDAO();
    private User user;

    @Override
    public void initUserData(User user) {
        this.user = user;
        welcomeLabel.setText("Assignments for: " + user.getFullName());

        colTitle.setCellValueFactory(data -> data.getValue().titleProperty());
        colDueDate.setCellValueFactory(data -> data.getValue().dueDateProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());

        loadAssignments();
    }

    private void loadAssignments() {
        ObservableList<Assignment> list = FXCollections.observableArrayList();
        try {
            list.addAll(assignmentDAO.getAllAssignments());
        } catch (SQLException e) {
            showError("Failed to load assignments: " + e.getMessage());
        }
        assignmentTable.setItems(list);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override
    public void initializeWithUser(User user) {
        initUserData(user);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("student-home.fxml"));
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

    @FXML
    private void handleViewDetails() {
        Assignment selected = assignmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an assignment to view details.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/learningmanagementsystem/student_assignment_details.fxml"));
            Parent root = loader.load();

            StudentAssignmentDetailsController controller = loader.getController();
            controller.initAssignmentDetails(selected, user);

            Stage stage = (Stage) assignmentTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Assignment Details");
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading assignment details: " + e.getMessage()).showAndWait();
        }
    }
}