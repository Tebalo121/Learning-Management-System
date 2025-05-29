package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.CourseDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.SQLException;

public class StudentCoursesController implements UserAwareController {
    @FXML private Label welcomeLabel;
    @FXML private ListView<String> courseListView;

    private final CourseDAO courseDAO = new CourseDAO();
    private User user;

    @Override
    public void initUserData(User user) {
        this.user = user;
        welcomeLabel.setText("Available Courses for: " + user.getFullName());
        loadAllCourses();
    }

    private void loadAllCourses() {
        courseListView.getItems().clear();
        try {
            var courses = courseDAO.getAllCourses();
            if (courses.isEmpty()) {
                courseListView.getItems().add("No courses available.");
            } else {
                courses.forEach(course -> courseListView.getItems().add(
                        course.getCourseName() + " - " + course.getInstructorName()));
            }
        } catch (SQLException e) {
            showError("Error loading courses: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override
    public void initializeWithUser(User user) {
        initUserData(user);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/learningmanagementsystem/student-home.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserAwareController) {
                ((UserAwareController) controller).initUserData(user);
            }

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
