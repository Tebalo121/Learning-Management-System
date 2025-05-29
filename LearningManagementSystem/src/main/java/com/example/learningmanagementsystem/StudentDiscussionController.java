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

public class StudentDiscussionController implements UserAwareController {
    private User student;

    @FXML private TableView<DiscussionTopic> topicsTable;
    @FXML private TableColumn<DiscussionTopic, String> titleColumn;
    @FXML private TableColumn<DiscussionTopic, String> descriptionColumn;
    @FXML private TableColumn<DiscussionTopic, LocalDateTime> createdAtColumn;
    @FXML private TextArea replyField;
    @FXML private Button submitReplyButton;
    @FXML private Button backButton;

    private ObservableList<DiscussionTopic> topicList = FXCollections.observableArrayList();
    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";

    @Override
    public void initUserData(User user) {
        this.student = user;
        System.out.println("StudentDiscussionController initialized with user: " +
                (user != null ? user.getUsername() : "null"));
        initializeTable();
        loadTopics();
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
        topicsTable.setItems(topicList);
    }

    private void initializeTable() {
        topicsTable.setItems(topicList);
    }

    private void loadTopics() {
        topicList.clear();
        if (student == null) {
            showAlert("Error", "No student logged in.");
            return;
        }
        String query = "SELECT id, title, description, created_at FROM discussion_topics";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                topicList.add(new DiscussionTopic(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load discussion topics: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmitReply() {
        DiscussionTopic selectedTopic = topicsTable.getSelectionModel().getSelectedItem();
        if (selectedTopic == null) {
            showAlert("Error", "Please select a topic to reply to.");
            return;
        }
        String replyText = replyField.getText().trim();
        if (replyText.isEmpty()) {
            showAlert("Error", "Please provide a reply.");
            return;
        }

        InstructorSetDiscussionTopicsController.submitReply(selectedTopic.getId(), student.getUserId(), replyText);
        showAlert("Success", "Reply submitted successfully!");
        replyField.clear();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_FXML_PATH + "student_home.fxml"));
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