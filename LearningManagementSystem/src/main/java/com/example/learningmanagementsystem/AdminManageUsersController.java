package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AdminManageUsersController implements UserAwareController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Number> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> fullNameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField roleField;
    @FXML private Pagination pagination;
    @FXML private Button backButton;

    private final UserDAO userDAO = new UserDAO();
    private ObservableList<User> users = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 10;
    private User admin; // To store the logged-in admin user

    @Override
    public void initUserData(User user) {
        this.admin = user;
        System.out.println("AdminManageUsersController initialized with user: " +
                (user != null ? user.getUsername() : "null"));
    }

    @Override
    public void initializeWithUser(User user) {
        // Optional
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> data.getValue().userIdProperty());
        usernameColumn.setCellValueFactory(data -> data.getValue().usernameProperty());
        fullNameColumn.setCellValueFactory(data -> data.getValue().fullNameProperty());
        roleColumn.setCellValueFactory(data -> data.getValue().roleProperty());

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> populateForm(newVal));
        loadUsers();
    }

    private void loadUsers() {
        users.clear();
        try {
            users.addAll(userDAO.getAllUsers());
            int pageCount = (int) Math.ceil((double) users.size() / ROWS_PER_PAGE);
            pagination.setPageCount(pageCount);
            pagination.setPageFactory(this::createPage);
        } catch (SQLException e) {
            showAlert("Error loading users: " + e.getMessage());
        }
    }

    private TableView<User> createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, users.size());
        userTable.setItems(FXCollections.observableArrayList(users.subList(fromIndex, toIndex)));
        return userTable;
    }

    private void populateForm(User user) {
        if (user != null) {
            usernameField.setText(user.getUsername());
            fullNameField.setText(user.getFullName());
            roleField.setText(user.getRole());
        }
    }

    @FXML
    private void handleAddUser() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String role = roleField.getText().trim().toLowerCase();

        if (username.isEmpty() || fullName.isEmpty() || role.isEmpty()) {
            showAlert("All fields are required to add a user.");
            return;
        }

        if (userDAO.usernameExists(username)) {
            showAlert("Username already exists.");
            return;
        }

        User newUser = new User(username, "default123", fullName, username + "@lms.com", role);
        boolean success = userDAO.registerUser(newUser);
        if (success) {
            showAlert("User added successfully.");
            loadUsers();
            clearForm();
        } else {
            showAlert("Failed to add user.");
        }
    }

    @FXML
    private void handleUpdateUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a user to update.");
            return;
        }

        String fullName = fullNameField.getText().trim();
        String role = roleField.getText().trim().toLowerCase();

        try {
            selected.setFullName(fullName);
            selected.setRole(role);
            userDAO.updateUser(selected);
            showAlert("User updated.");
            loadUsers();
            clearForm();
        } catch (SQLException e) {
            showAlert("Update failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a user to delete.");
            return;
        }

        try {
            userDAO.deleteUser(selected.getUserId());
            showAlert("User deleted.");
            loadUsers();
            clearForm();
        } catch (SQLException e) {
            showAlert("Deletion failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            String fxmlPath = "/com/example/learningmanagementsystem/admin-home.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                showAlert("Error: FXML file not found: " + fxmlPath);
                return;
            }
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAware) {
                userAware.initUserData(admin);
            }
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error: Failed to load admin home screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleAbout() {
        showAlert("Learning Management System Admin Panel\nDeveloped in JavaFX.");
    }

    private void clearForm() {
        usernameField.clear();
        fullNameField.clear();
        roleField.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}