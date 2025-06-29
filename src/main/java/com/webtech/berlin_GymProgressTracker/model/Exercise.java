package com.webtech.berlin_GymProgressTracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name darf nicht leer sein")
    private String name;

    // Gewicht und Wiederholungen wurden in die Set-Entität verschoben
    
    @ManyToOne
    @JoinColumn(name = "training_session_id", nullable = false)
    @JsonBackReference
    private TrainingSession trainingSession;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Set> sets = new ArrayList<>();

    // Standard-Konstruktor für JPA
    public Exercise() {}

    // Konstruktor für neue Übungen (Sets werden später hinzugefügt)
    public Exercise(String name, TrainingSession trainingSession) {
        this.name = name;
        this.trainingSession = trainingSession;
    }

    // Getter und Setter
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

    public TrainingSession getTrainingSession() {
        return trainingSession;
    }

    public void setTrainingSession(TrainingSession trainingSession) {
        this.trainingSession = trainingSession;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    // Hilfsmethode zum Hinzufügen eines Sets
    public void addSet(Set set) {
        sets.add(set);
        set.setExercise(this);
    }

    // Hilfsmethode zum Entfernen eines Sets
    public void removeSet(Set set) {
        sets.remove(set);
        set.setExercise(null);
    }
}

