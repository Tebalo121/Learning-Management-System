package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;

    private final UserDAO userDao = new UserDAO();

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("Student", "Instructor", "Admin");
        roleComboBox.setValue("Student");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        try {
            User user = userDao.authenticateUser(username, password, role);
            if (user != null) {
                redirectToHome(user);
            } else {
                errorLabel.setText("Invalid credentials or role mismatch");
            }
        } catch (Exception e) {
            errorLabel.setText("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterLink(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("registration-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Control)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Registration");
        } catch (IOException e) {
            errorLabel.setText("Failed to load registration form");
            e.printStackTrace();
        }
    }

    private void redirectToHome(User user) throws IOException {
        String fxmlFile = switch (user.getRole().toLowerCase()) {
            case "admin" -> "admin-home.fxml";
            case "instructor" -> "instructor-home.fxml";
            case "student" -> "student-home.fxml";
            default -> throw new IllegalArgumentException("Unknown role: " + user.getRole());
        };

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        Object controller = loader.getController();
        if (controller instanceof UserAwareController userAware) {
            userAware.initUserData(user); // Correct method call
        } else {
            System.err.println("Controller for " + fxmlFile + " does not implement UserAwareController");
        }

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(user.getRole() + " Dashboard");
    }
}