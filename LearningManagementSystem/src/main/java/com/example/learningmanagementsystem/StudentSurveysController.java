package com.example.learningmanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class StudentSurveysController implements UserAwareController {
    private User student;

    @FXML private TableView<Survey> surveyTable;
    @FXML private TableColumn<Survey, String> titleColumn;
    @FXML private TableColumn<Survey, String> descriptionColumn;
    @FXML private TableColumn<Survey, LocalDateTime> deadlineColumn;
    @FXML private TextArea responseField;
    @FXML private CheckBox anonymousCheckBox;
    @FXML private Button submitResponseButton;
    @FXML private Button backButton;

    private ObservableList<Survey> surveyList = FXCollections.observableArrayList();
    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";

    @Override
    public void initUserData(User user) {
        this.student = user;
        System.out.println("StudentSurveysController initialized with user: " +
                (user != null ? user.getUsername() : "null"));
        initializeTable();
        loadSurveys();
    }

    @Override
    public void initializeWithUser(User user) {
        // Optional
    }

    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        deadlineColumn.setCellValueFactory(cellData -> cellData.getValue().deadlineProperty());
        surveyTable.setItems(surveyList);
    }

    private void initializeTable() {
        surveyTable.setItems(surveyList);
    }

    private void loadSurveys() {
        surveyList.clear();
        if (student == null) {
            showAlert("Error", "No student logged in.");
            return;
        }
        String query = "SELECT id, title, description, is_anonymous, deadline, created_at FROM surveys";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                surveyList.add(new Survey(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getBoolean("is_anonymous"),
                        rs.getTimestamp("deadline") != null ? rs.getTimestamp("deadline").toLocalDateTime() : null,
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load surveys: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmitResponse() {
        Survey selectedSurvey = surveyTable.getSelectionModel().getSelectedItem();
        if (selectedSurvey == null) {
            showAlert("Error", "Please select a survey to respond to.");
            return;
        }
        String responseText = responseField.getText().trim();
        if (responseText.isEmpty()) {
            showAlert("Error", "Please provide a response.");
            return;
        }
        boolean isAnonymous = anonymousCheckBox.isSelected();

        String query = "INSERT INTO responses (survey_id, student_id, response_text) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selectedSurvey.getId());
            stmt.setObject(2, isAnonymous ? null : student.getUserId());
            stmt.setString(3, responseText);
            stmt.executeUpdate();
            showAlert("Success", "Response submitted successfully!");
            responseField.clear();
            anonymousCheckBox.setSelected(true);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to submit response: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_FXML_PATH + "student_home.fxml"));
            if (loader.getLocation() == null) {
                showAlert("Error", "FXML file not found: student_home.fxml");
                return;
            }
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAware) {
                userAware.initUserData(student);
            }
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return to home screen: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}