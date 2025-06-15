package com.webtech.berlin_GymProgressTracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Übungsname darf nicht leer sein")
    private String name;
    
    @NotNull(message = "Gewicht muss angegeben werden")
    @Min(value = 0, message = "Gewicht muss positiv sein")
    private Integer weight;
    
    @NotNull(message = "Wiederholungen müssen angegeben werden")
    @Min(value = 1, message = "Mindestens 1 Wiederholung erforderlich")
    private Integer reps;

    public Exercise() {
        // Standardkonstruktor für JPA
    }

    public Exercise(String name, Integer weight, Integer reps) {
        this.name = name;
        this.weight = weight;
        this.reps = reps;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getReps() {
        return reps;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }
}

