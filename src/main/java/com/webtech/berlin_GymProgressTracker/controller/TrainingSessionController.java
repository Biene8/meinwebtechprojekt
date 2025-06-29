package com.webtech.berlin_GymProgressTracker.controller;

import com.webtech.berlin_GymProgressTracker.model.Exercise;
import com.webtech.berlin_GymProgressTracker.model.Set;
import com.webtech.berlin_GymProgressTracker.model.TrainingSession;
import com.webtech.berlin_GymProgressTracker.repository.TrainingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {
        "http://localhost:8080",
        "http://localhost:8081",
        "https://meinwebtechprojektfront-uxql.onrender.com"
})
@RestController
@RequestMapping("/training-sessions")
public class TrainingSessionController {

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @GetMapping
    public List<TrainingSession> getAllTrainingSessions() {
        return trainingSessionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingSession> getTrainingSession(@PathVariable Long id) {
        return trainingSessionRepository.findById(id)
                .map(session -> ResponseEntity.ok().body(session))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TrainingSession> startTrainingSession() {
        TrainingSession session = new TrainingSession();
        session.setStartTime(LocalDateTime.now());
        TrainingSession savedSession = trainingSessionRepository.save(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSession);
    }

    @PostMapping("/{sessionId}/exercises")
    public ResponseEntity<?> addExerciseToSession(@PathVariable Long sessionId, @Valid @RequestBody Exercise exercise, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Validierungsfehler",
                    "errors", errors
            ));
        }

        return trainingSessionRepository.findById(sessionId).map(trainingSession -> {
            try {
                exercise.setTrainingSession(trainingSession);

                if (exercise.getSets() != null) {
                    for (Set set : exercise.getSets()) {
                        set.setExercise(exercise);
                    }
                }

                trainingSession.addExercise(exercise);

                TrainingSession savedSession = trainingSessionRepository.save(trainingSession);

                Exercise savedExercise = savedSession.getExercises().stream()
                        .filter(e -> e.getName().equals(exercise.getName()))
                        .reduce((first, second) -> second)
                        .orElse(exercise);

                return ResponseEntity.status(HttpStatus.CREATED).body(savedExercise);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message", "Fehler beim Speichern der Übung",
                        "error", e.getMessage()
                ));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<?> endTrainingSession(@PathVariable Long id) {
        return trainingSessionRepository.findById(id).map(session -> {
            try {
                session.setEndTime(LocalDateTime.now());
                TrainingSession savedSession = trainingSessionRepository.save(session);
                return ResponseEntity.ok().body(Map.of(
                        "message", "Training erfolgreich beendet",
                        "session", savedSession
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message", "Fehler beim Beenden des Trainings",
                        "error", e.getMessage()
                ));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrainingSession(@PathVariable Long id) {
        return trainingSessionRepository.findById(id).map(session -> {
            try {
                trainingSessionRepository.delete(session);
                return ResponseEntity.ok().body(Map.of(
                        "message", "Trainingseinheit erfolgreich gelöscht"
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message", "Fehler beim Löschen der Trainingseinheit",
                        "error", e.getMessage()
                ));
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}