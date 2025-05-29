package com.example.learningmanagementsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Announcement {
    private final StringProperty title;
    private final StringProperty message;
    private final StringProperty date;

    public Announcement(String title, String message, String date) {
        this.title = new SimpleStringProperty(title);
        this.message = new SimpleStringProperty(message);
        this.date = new SimpleStringProperty(date);
    }

    public StringProperty titleProperty() { return title; }
    public StringProperty messageProperty() { return message; }
    public StringProperty dateProperty() { return date; }
}