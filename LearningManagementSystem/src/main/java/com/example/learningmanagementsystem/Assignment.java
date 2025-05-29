package com.example.learningmanagementsystem;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Assignment {
    private final SimpleIntegerProperty assignmentId;
    private final StringProperty courseName;
    private final StringProperty title;
    private final StringProperty dueDate;
    private final StringProperty status;
    private final StringProperty description; // New field

    // Full constructor
    public Assignment(int assignmentId, String courseName, String title, String dueDate, String description, String status) {
        this.assignmentId = new SimpleIntegerProperty(assignmentId);
        this.courseName = new SimpleStringProperty(courseName);
        this.title = new SimpleStringProperty(title);
        this.dueDate = new SimpleStringProperty(dueDate);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleStringProperty(status);
    }

    // Constructor without assignmentId
    public Assignment(String courseName, String title, String dueDate, String description, String status) {
        this(-1, courseName, title, dueDate, description, status);
    }

    public Assignment(String courseName, String title, String dueDate, SimpleIntegerProperty assignmentId, StringProperty courseName1, StringProperty title1, StringProperty dueDate1, StringProperty status, StringProperty description) {
        this.assignmentId = assignmentId;
        this.courseName = courseName1;
        this.title = title1;
        this.dueDate = dueDate1;
        this.status = status;
        this.description = description;
    }

    // Getters
    public int getAssignmentId() {
        return assignmentId.get();
    }

    public String getCourseName() {
        return courseName.get();
    }

    public String getTitle() {
        return title.get();
    }

    public String getDueDate() {
        return dueDate.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getDescription() {
        return description.get();
    }

    // Setters
    public void setStatus(String status) {
        this.status.set(status);
    }

    // Property methods
    public StringProperty courseNameProperty() {
        return courseName;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty dueDateProperty() {
        return dueDate;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty descriptionProperty() {
        return description;
    }
}