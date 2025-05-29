package com.example.learningmanagementsystem;

public class ProgressRecord {
    private final String studentName;
    private final double progressPercentage;

    public ProgressRecord(String studentName, double progressPercentage) {
        this.studentName = studentName;
        this.progressPercentage = progressPercentage;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getProgressPercentage() {
        return String.format("%.2f", progressPercentage);
    }
}