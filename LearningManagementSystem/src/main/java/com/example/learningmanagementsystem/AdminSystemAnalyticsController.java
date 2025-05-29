package com.example.learningmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AdminSystemAnalyticsController implements UserAwareController {
    @FXML private Label welcomeLabel;
    @FXML private TableView<?> analyticsTable; // Replace ? with Analytics model class
    private User user;

    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";

    @Override
    public void initUserData(User user) {
        this.user = user;
        if (user != null) {
            welcomeLabel.setText("Welcome Admin " + user.getFullName());
        } else {
            welcomeLabel.setText("Welcome Admin");
            System.err.println("⚠️ Warning: user is null in AdminSystemAnalyticsController");
        }
    }

    @Override
    public void initializeWithUser(User user) {
        // Reserved for future use
    }

    @FXML
    private void handleBack() {
        log("Returning to Admin Dashboard");
        switchScene("admin_home.fxml");
    }

    private void switchScene(String fxmlName) {
        try {
            String fullPath = BASE_FXML_PATH + fxmlName;
            URL fxmlResource = getClass().getResource(fullPath);
            if (fxmlResource == null) {
                throw new IOException("FXML file not found at path: " + fullPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlResource);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAware) {
                userAware.initUserData(user);
            }

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("❌ Failed to load FXML: " + fxmlName);
            e.printStackTrace();
        }
    }

    private void log(String action) {
        if (user != null) {
            System.out.println(action + " for: " + user.getUsername());
        } else {
            System.out.println(action + " (user not initialized)");
        }
    }
}