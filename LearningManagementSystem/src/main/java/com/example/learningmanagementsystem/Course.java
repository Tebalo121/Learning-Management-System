package com.example.learningmanagementsystem;

import javafx.beans.property.*;

public class Course {
    private final IntegerProperty courseId;
    private final StringProperty courseName;
    private final StringProperty instructorName;
    private final StringProperty description;

    public Course(int courseId, String courseName, String instructorName, String description) {
        this.courseId = new SimpleIntegerProperty(courseId);
        this.courseName = new SimpleStringProperty(courseName);
        this.instructorName = new SimpleStringProperty(instructorName);
        this.description = new SimpleStringProperty(description);
    }

    public int getCourseId() {
        return courseId.get();
    }

    public IntegerProperty courseIdProperty() {
        return courseId;
    }

    public String getCourseName() {
        return courseName.get();
    }

    public StringProperty courseNameProperty() {
        return courseName;
    }

    public String getInstructorName() {
        return instructorName.get();
    }

    public StringProperty instructorNameProperty() {
        return instructorName;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    @Override
    public String toString() {
        return getCourseName() + " (" + getInstructorName() + ")";
    }
}