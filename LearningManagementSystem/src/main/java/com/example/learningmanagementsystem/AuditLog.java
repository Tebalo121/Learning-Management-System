package com.example.learningmanagementsystem;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;

public class AuditLog {
    private final ObjectProperty<LocalDateTime> timestamp;
    private final StringProperty username;
    private final StringProperty action;
    private final StringProperty details;

    public AuditLog(LocalDateTime timestamp, String username, String action, String details) {
        this.timestamp = new SimpleObjectProperty<>(timestamp);
        this.username = new SimpleStringProperty(username);
        this.action = new SimpleStringProperty(action);
        this.details = new SimpleStringProperty(details);
    }

    // Getters
    public LocalDateTime getTimestamp() {
        return timestamp.get();
    }

    public String getUsername() {
        return username.get();
    }

    public String getAction() {
        return action.get();
    }

    public String getDetails() {
        return details.get();
    }

    // Setters
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp.set(timestamp);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setAction(String action) {
        this.action.set(action);
    }

    public void setDetails(String details) {
        this.details.set(details);
    }

    // JavaFX Property Methods
    public ObjectProperty<LocalDateTime> timestampProperty() {
        return timestamp;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty actionProperty() {
        return action;
    }

    public StringProperty detailsProperty() {
        return details;
    }
}