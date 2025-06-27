package com.webtech.berlin_GymProgressTracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn; // Neu hinzugefügt
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonBackReference; // Neu hinzugefügt

@Entity
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name darf nicht leer sein")
    private String name;

    @NotNull(message = "Gewicht darf nicht leer sein")
    @Positive(message = "Gewicht muss positiv sein")
    private Integer weight;

    @NotNull(message = "Wiederholungen dürfen nicht leer sein")
    @Positive(message = "Wiederholungen müssen positiv sein")
    private Integer reps;

    @ManyToOne
    @JoinColumn(name = "training_session_id", nullable = false)
    @JsonBackReference
    private TrainingSession trainingSession;

    public Exercise() {}

    public Exercise(String name, Integer weight, Integer reps, TrainingSession trainingSession) {
        this.name = name;
        this.weight = weight;
        this.reps = reps;
        this.trainingSession = trainingSession;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public TrainingSession getTrainingSession() {
        return trainingSession;
    }

    public void setTrainingSession(TrainingSession trainingSession) {
        this.trainingSession = trainingSession;
    }
}