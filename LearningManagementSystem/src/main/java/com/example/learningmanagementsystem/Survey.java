package com.example.learningmanagementsystem;

import javafx.beans.property.*;

public class Survey {
    private final IntegerProperty id;
    private final StringProperty title;
    private final StringProperty description;
    private final BooleanProperty isAnonymous;
    private final ObjectProperty<java.time.LocalDateTime> deadline;
    private final ObjectProperty<java.time.LocalDateTime> createdAt;

    public Survey(int id, String title, String description, boolean isAnonymous, java.time.LocalDateTime deadline, java.time.LocalDateTime createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.isAnonymous = new SimpleBooleanProperty(isAnonymous);
        this.deadline = new SimpleObjectProperty<>(deadline);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty descriptionProperty() { return description; }
    public BooleanProperty isAnonymousProperty() { return isAnonymous; }
    public ObjectProperty<java.time.LocalDateTime> deadlineProperty() { return deadline; }
    public ObjectProperty<java.time.LocalDateTime> createdAtProperty() { return createdAt; }

    public int getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getDescription() { return description.get(); }
    public boolean getIsAnonymous() { return isAnonymous.get(); }
    public java.time.LocalDateTime getDeadline() { return deadline.get(); }
    public java.time.LocalDateTime getCreatedAt() { return createdAt.get(); }
}