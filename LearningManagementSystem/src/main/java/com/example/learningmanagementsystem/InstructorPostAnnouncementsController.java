package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.AnnouncementDAO;
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

public class InstructorPostAnnouncementsController implements UserAwareController {
    @FXML private TextField announcementTitleField;
    @FXML private TextArea announcementTextField;
    @FXML private TableView<Announcement> announcementTable;
    @FXML private TableColumn<Announcement, String> titleColumn;
    @FXML private TableColumn<Announcement, String> messageColumn;
    @FXML private TableColumn<Announcement, String> dateColumn;
    @FXML private Button postAnnouncementButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    private final AnnouncementDAO announcementDAO = new AnnouncementDAO();
    private final ObservableList<Announcement> announcements = FXCollections.observableArrayList();
    private User instructor;
    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";

    @Override
    public void initUserData(User user) {
        this.instructor = user;
        setupTable();
        loadAnnouncements();
    }

    @Override
    public void initializeWithUser(User user) {
        // Optional
    }

    private void setupTable() {
        titleColumn.setCellValueFactory(cell -> cell.getValue().titleProperty());
        messageColumn.setCellValueFactory(cell -> cell.getValue().messageProperty());
        dateColumn.setCellValueFactory(cell -> cell.getValue().dateProperty());
        announcementTable.setItems(announcements);
    }

    private void loadAnnouncements() {
        try {
            announcements.setAll(announcementDAO.getAnnouncementsByInstructor(instructor.getUserId()));
        } catch (SQLException e) {
            showAlert("Error", "Failed to load announcements: " + e.getMessage());
        }
    }

    @FXML
    private void handlePostAnnouncement() {
        String title = announcementTitleField.getText().trim();
        String text = announcementTextField.getText().trim();

        if (title.isEmpty() || text.isEmpty()) {
            showAlert("Error", "Title and Message cannot be empty.");
            return;
        }

        try {
            announcementDAO.postAnnouncement(instructor.getUserId(), title, text);
            showAlert("Success", "Announcement posted!");
            announcementTitleField.clear();
            announcementTextField.clear();
            loadAnnouncements();
        } catch (SQLException e) {
            showAlert("Error", "Failed to post announcement: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        announcementTitleField.clear();
        announcementTextField.clear();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_FXML_PATH + "instructor-home.fxml"));
            if (loader.getLocation() == null) {
                showAlert("Error", "FXML file not found: instructor-home.fxml");
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}