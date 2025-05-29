package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class RegistrationController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleComboBox;

    private final UserDAO userDao = new UserDAO();

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("student", "instructor", "admin");
        roleComboBox.setValue("student");
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled.");
            return;
        }

        User newUser = new User(username, password, fullName, email, role);
        if (userDao.registerUser(newUser)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User registered successfully!");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "User could not be registered.");
        }
    }

    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        fullNameField.clear();
        emailField.clear();
        roleComboBox.setValue("student");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
