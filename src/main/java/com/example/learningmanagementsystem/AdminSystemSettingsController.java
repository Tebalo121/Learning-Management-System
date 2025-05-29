package com.example.learningmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminSystemSettingsController implements UserAwareController {
    private User user;

    @FXML
    private CheckBox announcementCheckBox;

    @FXML
    private CheckBox registrationCheckBox;

    @Override
    public void initUserData(User user) {
        this.user = user;
    }

    @Override
    public void initializeWithUser(User user) {
        // Optional: load user-specific settings if needed
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/learningmanagementsystem/admin_home.fxml"));
            Parent root = loader.load();

            AdminHomeController controller = loader.getController();
            controller.initUserData(user);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Home");
            stage.show();

            // Close current window using any known node (here: announcementCheckBox)
            Stage currentStage = (Stage) announcementCheckBox.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}