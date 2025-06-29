package com.webtech.berlin_GymProgressTracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@jakarta.persistence.Table(name = "exercise_set") // Vermeidet Konflikte mit SQL-Keyword 'SET'
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Gewicht darf nicht leer sein")
    @Positive(message = "Gewicht muss positiv sein")
    private Integer weight;

    @NotNull(message = "Wiederholungen dürfen nicht leer sein")
    @Positive(message = "Wiederholungen müssen positiv sein")
    private Integer reps;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    @JsonBackReference
    private Exercise exercise;

    // Standard-Konstruktor für JPA
    public Set() {}

    // Konstruktor für neue Sets
    public Set(Integer weight, Integer reps, Exercise exercise) {
        this.weight = weight;
        this.reps = reps;
        this.exercise = exercise;
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }
}

