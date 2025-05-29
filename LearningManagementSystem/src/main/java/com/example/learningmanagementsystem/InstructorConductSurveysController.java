package com.example.learningmanagementsystem;

import javafx.beans.property.SimpleIntegerProperty;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class InstructorConductSurveysController implements UserAwareController {
    private User instructor;

    @FXML private TextField surveyTitleField;
    @FXML private TextArea surveyDescriptionField;
    @FXML private CheckBox anonymousCheckBox;
    @FXML private DatePicker deadlinePicker;
    @FXML private Button createSurveyButton;
    @FXML private TableView<Survey> surveyTable;
    @FXML private TableColumn<Survey, String> titleColumn;
    @FXML private TableColumn<Survey, String> descriptionColumn;
    @FXML private TableColumn<Survey, LocalDateTime> createdAtColumn;
    @FXML private TableColumn<Survey, Number> responseCountColumn;
    @FXML private Button viewResponsesButton;
    @FXML private TextArea responsesArea;
    @FXML private Button backButton;

    private ObservableList<Survey> surveyList = FXCollections.observableArrayList();

    @Override
    public void initUserData(User user) {
        this.instructor = user;
        System.out.println("InstructorConductSurveysController initialized with user: " +
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
        createdAtColumn.setCellValueFactory(cellData -> cellData.getValue().createdAtProperty());
        responseCountColumn.setCellValueFactory(cellData -> {
            int surveyId = cellData.getValue().getId();
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM responses WHERE survey_id = ?")) {
                stmt.setInt(1, surveyId);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return new SimpleIntegerProperty(rs.getInt(1));
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleIntegerProperty(0);
            }
        });
        surveyTable.setItems(surveyList);
    }

    private void initializeTable() {
        surveyTable.setItems(surveyList);
    }

    private void loadSurveys() {
        surveyList.clear();
        if (instructor == null) {
            showAlert("Error", "No instructor logged in.");
            return;
        }
        String query = "SELECT id, title, description, is_anonymous, deadline, created_at FROM surveys WHERE instructor_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, instructor.getUserId());
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
    private void handleCreateSurvey() {
        if (instructor == null) {
            showAlert("Error", "No instructor logged in.");
            return;
        }
        String title = surveyTitleField.getText().trim();
        String description = surveyDescriptionField.getText().trim();
        boolean isAnonymous = anonymousCheckBox.isSelected();
        LocalDate deadlineDate = deadlinePicker.getValue();

        if (title.isEmpty() || description.isEmpty()) {
            showAlert("Error", "Please provide a title and question.");
            return;
        }

        LocalDateTime deadline = deadlineDate != null ? deadlineDate.atTime(LocalTime.MIDNIGHT) : null;

        String query = "INSERT INTO surveys (title, description, instructor_id, is_anonymous, deadline) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setInt(3, instructor.getUserId());
            stmt.setBoolean(4, isAnonymous);
            stmt.setObject(5, deadline != null ? java.sql.Timestamp.valueOf(deadline) : null);
            stmt.executeUpdate();
            showAlert("Success", "Survey created successfully!");
            surveyTitleField.clear();
            surveyDescriptionField.clear();
            anonymousCheckBox.setSelected(true);
            deadlinePicker.setValue(null);
            loadSurveys();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create survey: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewResponses() {
        Survey selectedSurvey = surveyTable.getSelectionModel().getSelectedItem();
        if (selectedSurvey == null) {
            showAlert("Error", "Please select a survey to view responses.");
            return;
        }

        responsesArea.clear();

        String query = "SELECT response_text FROM responses WHERE survey_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selectedSurvey.getId());
            ResultSet rs = stmt.executeQuery();
            StringBuilder responses = new StringBuilder();
            while (rs.next()) {
                responses.append(rs.getString("response_text")).append("\n");
            }
            responsesArea.setText(responses.length() > 0 ? responses.toString() : "No responses yet.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load responses: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            String fxmlPath = "/com/example/learningmanagementsystem/instructor-home.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                showAlert("Error", "FXML file not found: " + fxmlPath);
                return;
            }
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAware) {
                userAware.initUserData(instructor);
            }
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load instructor home screen: " + e.getMessage());
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