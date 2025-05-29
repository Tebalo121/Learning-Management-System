package com.example.learningmanagementsystem;

import javafx.beans.property.*;

public class DiscussionTopic {
    private final IntegerProperty id;
    private final StringProperty title;
    private final StringProperty description;
    private final ObjectProperty<java.time.LocalDateTime> createdAt;

    public DiscussionTopic(int id, String title, String description, java.time.LocalDateTime createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty descriptionProperty() { return description; }
    public ObjectProperty<java.time.LocalDateTime> createdAtProperty() { return createdAt; }

    public int getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getDescription() { return description.get(); }
    public java.time.LocalDateTime getCreatedAt() { return createdAt.get(); }
}