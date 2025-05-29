package com.example.learningmanagementsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Grade {
    private final StringProperty courseName;
    private final StringProperty assignmentName;
    private final StringProperty gradeValue;

    public Grade(String courseName, String assignmentName, String gradeValue) {
        this.courseName = new SimpleStringProperty(courseName);
        this.assignmentName = new SimpleStringProperty(assignmentName);
        this.gradeValue = new SimpleStringProperty(gradeValue);
    }

    public StringProperty courseNameProperty() {
        return courseName;
    }

    public StringProperty assignmentNameProperty() {
        return assignmentName;
    }

    public StringProperty gradeValueProperty() {
        return gradeValue;
    }
}