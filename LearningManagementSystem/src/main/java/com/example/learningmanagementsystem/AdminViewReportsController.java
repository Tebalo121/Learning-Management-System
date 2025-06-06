package com.example.learningmanagementsystem;

import com.example.learningmanagementsystem.dao.AdminReportDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class AdminViewReportsController implements UserAwareController {

    @FXML private VBox chartContainer;
    @FXML private Button backButton;
    private final AdminReportDAO reportDAO = new AdminReportDAO();
    private User admin;

    @Override
    public void initUserData(User user) {
        this.admin = user;
    }

    @Override
    public void initializeWithUser(User user) {
        initUserData(user);
    }

    @FXML
    public void initialize() {
        loadCharts();
    }

    private void loadCharts() {
        chartContainer.getChildren().clear();

        try {
            // PieChart: User Roles
            PieChart pieChart = new PieChart();
            pieChart.setTitle("User Role Distribution");

            Map<String, Number> roleData = reportDAO.getUserRoleDistribution();
            roleData.forEach((role, count) ->
                    pieChart.getData().add(new PieChart.Data(role, count.doubleValue()))
            );

            // BarChart: Course Description Length
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Course");
            yAxis.setLabel("Description Length");

            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Course Description Lengths");

            XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
            barSeries.setName("Characters in Description");

            Map<String, Number> courseStats = reportDAO.getCourseDescriptionLengthStats();
            courseStats.forEach((course, value) ->
                    barSeries.getData().add(new XYChart.Data<>(course, value))
            );
            barChart.getData().add(barSeries);

            // LineChart: Assignment Count per Course
            CategoryAxis lineXAxis = new CategoryAxis();
            NumberAxis lineYAxis = new NumberAxis();
            lineXAxis.setLabel("Course");
            lineYAxis.setLabel("Assignment Count");

            LineChart<String, Number> lineChart = new LineChart<>(lineXAxis, lineYAxis);
            lineChart.setTitle("Assignments per Course");

            XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
            lineSeries.setName("Total Assignments");

            Map<String, Number> assignmentStats = reportDAO.getAssignmentCountPerCourse();
            assignmentStats.forEach((course, count) ->
                    lineSeries.getData().add(new XYChart.Data<>(course, count))
            );
            lineChart.getData().add(lineSeries);

            chartContainer.getChildren().addAll(pieChart, barChart, lineChart);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load charts: " + e.getMessage());
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