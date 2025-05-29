package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.CourseDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminViewCoursesController implements UserAwareController {

    @FXML
    private TableView<Course> courseTable;

    @FXML
    private TableColumn<Course, Integer> courseIdColumn;

    @FXML
    private TableColumn<Course, String> courseNameColumn;

    @FXML
    private TableColumn<Course, String> instructorColumn;

    @FXML
    private Button backButton;

    @FXML
    private Pagination pagination;

    private final CourseDAO courseDAO = new CourseDAO();
    private User admin;

    @Override
    public void initUserData(User user) {
        this.admin = user;
        loadCoursesFromDB();
    }

    @Override
    public void initializeWithUser(User user) {
        initUserData(user);
    }

    @FXML
    private void initialize() {
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        instructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructorName"));
    }

    private void loadCoursesFromDB() {
        try {
            List<Course> courseList = courseDAO.getAllCourses();
            ObservableList<Course> observableCourses = FXCollections.observableArrayList(courseList);
            courseTable.setItems(observableCourses);
            int pageCount = Math.max(1, (int) Math.ceil((double) courseList.size() / 10)); // 10 courses per page
            pagination.setPageCount(pageCount);
            pagination.setPageFactory(pageIndex -> {
                int fromIndex = pageIndex * 10;
                int toIndex = Math.min(fromIndex + 10, courseList.size());
                courseTable.setItems(FXCollections.observableArrayList(courseList.subList(fromIndex, toIndex)));
                return courseTable;
            });
        } catch (SQLException e) {
            showAlert("Failed to load courses from DB: " + e.getMessage());
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}