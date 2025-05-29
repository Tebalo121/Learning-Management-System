package com.example.learningmanagementsystem;

import javafx.beans.property.*;

public class Question {
    private final IntegerProperty id;
    private final IntegerProperty surveyId;
    private final StringProperty questionText;
    private final StringProperty questionType; // 'multiple_choice', 'open_ended', 'rating'
    private final ObjectProperty<String[]> options; // For multiple-choice
    private final IntegerProperty maxRating; // For rating questions

    public Question(int id, int surveyId, String questionText, String questionType, String[] options, Integer maxRating) {
        this.id = new SimpleIntegerProperty(id);
        this.surveyId = new SimpleIntegerProperty(surveyId);
        this.questionText = new SimpleStringProperty(questionText);
        this.questionType = new SimpleStringProperty(questionType);
        this.options = new SimpleObjectProperty<>(options);
        this.maxRating = new SimpleIntegerProperty(maxRating != null ? maxRating : 0);
    }

    public IntegerProperty idProperty() { return id; }
    public IntegerProperty surveyIdProperty() { return surveyId; }
    public StringProperty questionTextProperty() { return questionText; }
    public StringProperty questionTypeProperty() { return questionType; }
    public ObjectProperty<String[]> optionsProperty() { return options; }
    public IntegerProperty maxRatingProperty() { return maxRating; }

    public int getId() { return id.get(); }
    public int getSurveyId() { return surveyId.get(); }
    public String getQuestionText() { return questionText.get(); }
    public String getQuestionType() { return questionType.get(); }
    public String[] getOptions() { return options.get(); }
    public int getMaxRating() { return maxRating.get(); }
}