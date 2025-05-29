package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.AnnouncementDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class InstructorPostAnnouncementsController implements UserAwareController {
    @FXML private TextField announcementTitleField;
    @FXML private TextArea announcementTextField;
    @FXML private TableView<Announcement> announcementTable;
    @FXML private TableColumn<Announcement, String> titleColumn;
    @FXML private TableColumn<Announcement, String> messageColumn;
    @FXML private TableColumn<Announcement, String> dateColumn;

    private final AnnouncementDAO announcementDAO = new AnnouncementDAO();
    private final ObservableList<Announcement> announcements = FXCollections.observableArrayList();
    private User instructor;

    @Override
    public void initUserData(User user) {
        this.instructor = user;
        setupTable();
        loadAnnouncements();
    }

    @Override
    public void initializeWithUser(User user) {

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
            showAlert("Failed to load announcements: " + e.getMessage());
        }
    }

    @FXML
    private void handlePostAnnouncement() {
        String title = announcementTitleField.getText().trim();
        String text = announcementTextField.getText().trim();

        if (title.isEmpty() || text.isEmpty()) {
            showAlert("Title and Message cannot be empty.");
            return;
        }

        try {
            announcementDAO.postAnnouncement(instructor.getUserId(), title, text);
            showAlert("Announcement posted!");
            announcementTitleField.clear();
            announcementTextField.clear();
            loadAnnouncements();
        } catch (SQLException e) {
            showAlert("Failed to post announcement: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        announcementTitleField.clear();
        announcementTextField.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}