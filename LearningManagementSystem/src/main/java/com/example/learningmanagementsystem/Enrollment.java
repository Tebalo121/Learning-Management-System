package com.example.learningmanagementsystem;

import javafx.beans.property.*;

public class Enrollment {
    private final IntegerProperty enrollmentId = new SimpleIntegerProperty();
    private final StringProperty studentName = new SimpleStringProperty();
    private final StringProperty courseName = new SimpleStringProperty();
    private final FloatProperty test1 = new SimpleFloatProperty();
    private final FloatProperty midterm = new SimpleFloatProperty();
    private final FloatProperty testAssignment = new SimpleFloatProperty();
    private final FloatProperty finalExam = new SimpleFloatProperty();
    private final FloatProperty progress = new SimpleFloatProperty();
    private final StringProperty status = new SimpleStringProperty();

    public Enrollment(int enrollmentId, String studentName, String courseName,
                      float test1, float midterm, float testAssignment, float finalExam,
                      float progress, String status) {
        this.enrollmentId.set(enrollmentId);
        this.studentName.set(studentName);
        this.courseName.set(courseName);
        this.test1.set(test1);
        this.midterm.set(midterm);
        this.testAssignment.set(testAssignment);
        this.finalExam.set(finalExam);
        this.progress.set(progress);
        this.status.set(status);
    }

    // Getters
    public int getEnrollmentId() { return enrollmentId.get(); }
    public String getStudentName() { return studentName.get(); }
    public String getCourseName() { return courseName.get(); }
    public float getTest1() { return test1.get(); }
    public float getMidterm() { return midterm.get(); }
    public float getTestAssignment() { return testAssignment.get(); }
    public float getFinalExam() { return finalExam.get(); }
    public float getProgress() { return progress.get(); }
    public String getStatus() { return status.get(); }

    // Property accessors
    public IntegerProperty enrollmentIdProperty() { return enrollmentId; }
    public StringProperty studentNameProperty() { return studentName; }
    public StringProperty courseNameProperty() { return courseName; }
    public FloatProperty test1Property() { return test1; }
    public FloatProperty midtermProperty() { return midterm; }
    public FloatProperty testAssignmentProperty() { return testAssignment; }
    public FloatProperty finalExamProperty() { return finalExam; }
    public FloatProperty progressProperty() { return progress; }
    public StringProperty statusProperty() { return status; }
}