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
import java.time.LocalDateTime;

public class InstructorSetDiscussionTopicsController implements UserAwareController {
    private User instructor;

    @FXML private TextField topicTitleField;
    @FXML private TextArea topicDescriptionField;
    @FXML private Button addTopicButton;
    @FXML private TableView<DiscussionTopic> topicsTable;
    @FXML private TableColumn<DiscussionTopic, String> titleColumn;
    @FXML private TableColumn<DiscussionTopic, String> descriptionColumn;
    @FXML private TableColumn<DiscussionTopic, LocalDateTime> createdAtColumn;
    @FXML private TableColumn<DiscussionTopic, Number> replyCountColumn;
    @FXML private Button viewRepliesButton;
    @FXML private TextArea repliesArea;
    @FXML private Button backButton;

    private ObservableList<DiscussionTopic> topicList = FXCollections.observableArrayList();

    @Override
    public void initUserData(User user) {
        this.instructor = user;
        System.out.println("InstructorSetDiscussionTopicsController initialized with user: " +
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
        replyCountColumn.setCellValueFactory(cellData -> {
            int topicId = cellData.getValue().getId();
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM discussion_replies WHERE topic_id = ?")) {
                stmt.setInt(1, topicId);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return new SimpleIntegerProperty(rs.getInt(1));
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleIntegerProperty(0);
            }
        });
        topicsTable.setItems(topicList);
    }

    private void initializeTable() {
        topicsTable.setItems(topicList);
    }

    private void loadTopics() {
        topicList.clear();
        if (instructor == null) {
            showAlert("Error", "No instructor logged in.");
            return;
        }
        String query = "SELECT id, title, description, created_at FROM discussion_topics WHERE instructor_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, instructor.getUserId());
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
    private void handleAddTopic() {
        if (instructor == null) {
            showAlert("Error", "No instructor logged in.");
            return;
        }
        String title = topicTitleField.getText().trim();
        String description = topicDescriptionField.getText().trim();

        if (title.isEmpty()) {
            showAlert("Error", "Please provide a topic title.");
            return;
        }

        String query = "INSERT INTO discussion_topics (title, description, instructor_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, description.isEmpty() ? null : description);
            stmt.setInt(3, instructor.getUserId());
            stmt.executeUpdate();
            showAlert("Success", "Discussion topic added successfully!");
            topicTitleField.clear();
            topicDescriptionField.clear();
            loadTopics();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add discussion topic: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewReplies() {
        DiscussionTopic selectedTopic = topicsTable.getSelectionModel().getSelectedItem();
        if (selectedTopic == null) {
            showAlert("Error", "Please select a topic to view replies.");
            return;
        }

        repliesArea.clear();
        String query = "SELECT u.username, r.reply_text, r.submitted_at " +
                "FROM discussion_replies r JOIN users u ON r.student_id = u.user_id " +
                "WHERE r.topic_id = ? ORDER BY r.submitted_at";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selectedTopic.getId());
            ResultSet rs = stmt.executeQuery();
            StringBuilder replies = new StringBuilder();
            while (rs.next()) {
                replies.append(rs.getString("username"))
                        .append(" (").append(rs.getTimestamp("submitted_at")).append("):\n")
                        .append(rs.getString("reply_text")).append("\n\n");
            }
            repliesArea.setText(replies.length() > 0 ? replies.toString() : "No replies yet.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load replies: " + e.getMessage());
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

    // Placeholder method for student reply submission (to be called from student UI)
    public static void submitReply(int topicId, int studentId, String replyText) {
        if (replyText == null || replyText.trim().isEmpty()) {
            System.out.println("Error: Reply text cannot be empty.");
            return;
        }
        String query = "INSERT INTO discussion_replies (topic_id, student_id, reply_text) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, topicId);
            stmt.setInt(2, studentId);
            stmt.setString(3, replyText.trim());
            stmt.executeUpdate();
            System.out.println("Reply submitted successfully for topic ID: " + topicId);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to submit reply: " + e.getMessage());
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