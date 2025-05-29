package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.CourseDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private final CourseDAO courseDAO = new CourseDAO();
    private User user;

    @Override
    public void initUserData(User user) {
        this.user = user;
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
        } catch (SQLException e) {
            System.err.println("❌ Failed to load courses from DB: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        try {
            AdminHomeController.switchSceneStatic("admin-home.fxml", user, courseTable);
        } catch (Exception e) {
            System.err.println("❌ Failed to return to admin home.");
            e.printStackTrace();
        }
    }
}