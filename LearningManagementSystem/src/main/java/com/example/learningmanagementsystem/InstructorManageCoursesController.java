package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.CourseDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class InstructorManageCoursesController implements UserAwareController {
    private static final int ITEMS_PER_PAGE = 5;
    private List<Course> allCourses;
    private User instructor;

    @FXML private VBox coursesContainer;
    @FXML private Pagination pagination;
    @FXML private Button backButton;

    private static final String BASE_FXML_PATH = "/com/example/learningmanagementsystem/";

    @Override
    public void initUserData(User user) {
        this.instructor = user;
        System.out.println("InstructorManageCoursesController initialized with user: " +
                (user != null ? user.getUsername() : "null"));
        loadCourses();
    }

    @Override
    public void initializeWithUser(User user) {
        // Optional
    }

    @FXML
    public void initialize() {
        pagination.setPageFactory(this::createPage);
    }

    private void loadCourses() {
        if (instructor == null) {
            showAlert("Error", "No instructor logged in.");
            return;
        }
        try {
            CourseDAO courseDAO = new CourseDAO();
            allCourses = courseDAO.getCoursesByInstructor(instructor.getUserId());

            int pageCount = ( int ) Math.ceil(( double ) allCourses.size() / ITEMS_PER_PAGE);
            if (pageCount == 0) pageCount = 1;

            pagination.setPageCount(pageCount);
            pagination.setCurrentPageIndex(0);
            pagination.setPageFactory(this::createPage);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load courses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createPage(int pageIndex) {
        VBox pageBox = new VBox(10);
        pageBox.setPadding(new Insets(10));

        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, allCourses.size());

        if (fromIndex >= allCourses.size()) {
            return pageBox;
        }

        for (int i = fromIndex; i < toIndex; i++) {
            Course course = allCourses.get(i);
            pageBox.getChildren().add(createCourseCard(course));
        }

        return pageBox;
    }

    private HBox createCourseCard(Course course) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5; -fx-border-radius: 5;");

        VBox infoBox = new VBox(5);
        infoBox.setPrefWidth(400);

        Text nameText = new Text(course.getCourseName());
        nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Text descText = new Text(course.getDescription());
        descText.setWrappingWidth(400);
        descText.setStyle("-fx-font-size: 14px;");

        infoBox.getChildren().addAll(nameText, descText);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button viewButton = new Button("View");
        viewButton.setOnAction(e -> handleViewCourse(course));
        viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> handleEditCourse(course));
        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> handleDeleteCourse(course));
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(viewButton, editButton, deleteButton);

        card.getChildren().addAll(infoBox, buttonBox);
        return card;
    }

    @FXML
    private void handleAddCourse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_FXML_PATH + "course_edit_dialog.fxml"));
            Parent root = loader.load();

            CourseEditDialogController controller = loader.getController();
            controller.setMode(true, instructor.getUserId()); // true for add mode

            Stage stage = new Stage();
            stage.setTitle("Add New Course");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh after dialog closes
            loadCourses();

        } catch (IOException e) {
            showAlert("Error", "Failed to load course dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadCourses();
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

    private void handleViewCourse(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_FXML_PATH + "course_view.fxml"));
            if (loader.getLocation() == null) {
                showAlert("Error", "FXML file not found: course_view.fxml");
                return;
            }
            Parent root = loader.load();
            CourseViewController controller = loader.getController();
            controller.setCourseData(course, instructor);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load course view screen: " + e.getMessage());
        }
    }

    private void handleEditCourse(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(BASE_FXML_PATH + "course_edit_dialog.fxml"));
            Parent root = loader.load();

            CourseEditDialogController controller = loader.getController();
            controller.setMode(false, instructor.getUserId()); // false for edit mode
            controller.setCourseData(course);

            Stage stage = new Stage();
            stage.setTitle("Edit Course");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh after dialog closes
            loadCourses();

        } catch (IOException e) {
            showAlert("Error", "Failed to load course dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteCourse(Course course) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Course");
        alert.setContentText("Are you sure you want to delete '" + course.getCourseName() + "'?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    CourseDAO courseDAO = new CourseDAO();
                    courseDAO.deleteCourse(course.getCourseId());
                    loadCourses();
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to delete course: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}