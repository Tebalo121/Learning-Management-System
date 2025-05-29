package com.example.learningmanagementsystem;

import javafx.beans.property.*;
import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Properties for JavaFX binding
    private final IntegerProperty userId = new SimpleIntegerProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();

    // Default constructor
    public User() {}

    // Constructor without ID (used for registration)
    public User(String username, String password, String fullName, String email, String role) {
        this.username.set(username);
        this.password.set(password);
        this.fullName.set(fullName);
        this.email.set(email);
        this.role.set(role);
    }

    // Constructor with ID (used for loading from DB)
    public User(int userId, String username, String password, String fullName, String email, String role) {
        this.userId.set(userId);
        this.username.set(username);
        this.password.set(password);
        this.fullName.set(fullName);
        this.email.set(email);
        this.role.set(role);
    }

    // Standard getters
    public int getUserId() { return userId.get(); }
    public String getUsername() { return username.get(); }
    public String getPassword() { return password.get(); }
    public String getFullName() { return fullName.get(); }
    public String getEmail() { return email.get(); }
    public String getRole() { return role.get(); }

    // Standard setters
    public void setUserId(int userId) { this.userId.set(userId); }
    public void setUsername(String username) { this.username.set(username); }
    public void setPassword(String password) { this.password.set(password); }
    public void setFullName(String fullName) { this.fullName.set(fullName); }
    public void setEmail(String email) { this.email.set(email); }
    public void setRole(String role) { this.role.set(role); }

    // Property accessors for JavaFX
    public IntegerProperty userIdProperty() { return userId; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty fullNameProperty() { return fullName; }
    public StringProperty emailProperty() { return email; }
    public StringProperty roleProperty() { return role; }

    // Validation
    public boolean isValid() {
        return getUsername() != null && !getUsername().isEmpty() &&
                getPassword() != null && !getPassword().isEmpty() &&
                getFullName() != null && !getFullName().isEmpty() &&
                getEmail() != null && !getEmail().isEmpty() &&
                getRole() != null && !getRole().isEmpty();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role='" + getRole() + '\'' +
                '}';
    }
}