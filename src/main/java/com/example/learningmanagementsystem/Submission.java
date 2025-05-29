package com.example.learningmanagementsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyles;

import static jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyles.title;

public class Submission {
    private final int submissionId;
    private final StringProperty studentName;
    private final StringProperty assignmentTitle;
    private final StringProperty submissionDate;
    private final StringProperty grade;

    public Submission(int submissionId, String studentName, String assignmentTitle, String submissionDate, String grade) {
        this.submissionId = submissionId;
        this.studentName = new SimpleStringProperty(studentName);
        this.assignmentTitle = new SimpleStringProperty(assignmentTitle);
        this.submissionDate = new SimpleStringProperty(submissionDate);
        this.grade = new SimpleStringProperty(grade);
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public String getStudentName() {
        return studentName.get();
    }

    public String getTitle() {
        return assignmentTitle.get();  // <- this fixes the error
    }

    public String getSubmissionDate() {
        return submissionDate.get();
    }

    public String getGrade() {
        return grade.get();
    }

    public StringProperty studentNameProperty() { return studentName; }
    public StringProperty assignmentTitleProperty() { return assignmentTitle; }
    public StringProperty submissionDateProperty() { return submissionDate; }
    public StringProperty gradeProperty() { return grade; }
}