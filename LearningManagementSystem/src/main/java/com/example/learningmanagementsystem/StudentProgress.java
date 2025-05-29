package com.example.learningmanagementsystem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class StudentProgress {
    private final SimpleStringProperty studentName;
    private final SimpleDoubleProperty test1;
    private final SimpleDoubleProperty midterm;
    private final SimpleDoubleProperty test2;
    private final SimpleDoubleProperty finalExam;
    private final SimpleDoubleProperty average;
    private final int studentId;

    public StudentProgress(int studentId, String studentName, double test1, double midterm, double test2, double finalExam) {
        this.studentId = studentId;
        this.studentName = new SimpleStringProperty(studentName);
        this.test1 = new SimpleDoubleProperty(test1);
        this.midterm = new SimpleDoubleProperty(midterm);
        this.test2 = new SimpleDoubleProperty(test2);
        this.finalExam = new SimpleDoubleProperty(finalExam);
        this.average = new SimpleDoubleProperty((test1 + midterm + test2 + finalExam) / 4.0);
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName.get();
    }

    public SimpleStringProperty studentNameProperty() {
        return studentName;
    }

    public double getTest1() {
        return test1.get();
    }

    public SimpleDoubleProperty test1Property() {
        return test1;
    }

    public double getMidterm() {
        return midterm.get();
    }

    public SimpleDoubleProperty midtermProperty() {
        return midterm;
    }

    public double getTest2() {
        return test2.get();
    }

    public SimpleDoubleProperty test2Property() {
        return test2;
    }

    public double getFinalExam() {
        return finalExam.get();
    }

    public SimpleDoubleProperty finalExamProperty() {
        return finalExam;
    }

    public double getAverage() {
        return average.get();
    }

    public SimpleDoubleProperty averageProperty() {
        return average;
    }

    public void setTest1(double value) {
        test1.set(value);
        updateAverage();
    }

    public void setMidterm(double value) {
        midterm.set(value);
        updateAverage();
    }

    public void setTest2(double value) {
        test2.set(value);
        updateAverage();
    }

    public void setFinalExam(double value) {
        finalExam.set(value);
        updateAverage();
    }

    private void updateAverage() {
        average.set((test1.get() + midterm.get() + test2.get() + finalExam.get()) / 4.0);
    }
}
