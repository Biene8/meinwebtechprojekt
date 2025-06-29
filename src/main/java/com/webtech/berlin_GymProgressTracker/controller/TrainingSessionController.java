package com.webtech.berlin_GymProgressTracker.controller;

import com.webtech.berlin_GymProgressTracker.model.TrainingSession;
import com.webtech.berlin_GymProgressTracker.model.Exercise;
import com.webtech.berlin_GymProgressTracker.model.Set;
import com.webtech.berlin_GymProgressTracker.repository.TrainingSessionRepository;
import com.webtech.berlin_GymProgressTracker.repository.ExerciseRepository;
import com.webtech.berlin_GymProgressTracker.repository.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {
        "http://localhost:8081",
        "https://meinwebtechprojektfront-uxql.onrender.com"
})
@RestController
@RequestMapping("/training-sessions")
public class TrainingSessionController {

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private SetRepository setRepository;

    // Neue Trainingseinheit starten
    @PostMapping
    public ResponseEntity<TrainingSession> startNewTrainingSession() {
        TrainingSession session = new TrainingSession();
        TrainingSession savedSession = trainingSessionRepository.save(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSession);
    }

    // Trainingseinheit beenden
    @PutMapping("/{sessionId}/end")
    public ResponseEntity<TrainingSession> endTrainingSession(@PathVariable Long sessionId) {
        Optional<TrainingSession> optionalSession = trainingSessionRepository.findById(sessionId);
        if (optionalSession.isPresent()) {
            TrainingSession session = optionalSession.get();
            session.setEndTime(LocalDateTime.now());
            TrainingSession updatedSession = trainingSessionRepository.save(session);
            return ResponseEntity.ok(updatedSession);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Alle Trainingseinheiten abrufen
    @GetMapping
    public ResponseEntity<List<TrainingSession>> getAllTrainingSessions() {
        List<TrainingSession> sessions = trainingSessionRepository.findAll();
        return ResponseEntity.ok(sessions);
    }

    // Spezifische Trainingseinheit abrufen
    @GetMapping("/{sessionId}")
    public ResponseEntity<TrainingSession> getTrainingSessionById(@PathVariable Long sessionId) {
        Optional<TrainingSession> optionalSession = trainingSessionRepository.findById(sessionId);
        if (optionalSession.isPresent()) {
            return ResponseEntity.ok(optionalSession.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Übung zu Trainingseinheit hinzufügen (mit erstem Set)
    @PostMapping("/{sessionId}/exercises")
    public ResponseEntity<?> addExerciseToTrainingSession(@PathVariable Long sessionId, @Valid @RequestBody Map<String, Object> exerciseData, BindingResult result) {
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

        Optional<TrainingSession> optionalSession = trainingSessionRepository.findById(sessionId);
        if (optionalSession.isPresent()) {
            TrainingSession session = optionalSession.get();
            
            try {
                // Übung erstellen
                Exercise exercise = new Exercise();
                exercise.setName((String) exerciseData.get("name"));
                exercise.setTrainingSession(session);
                Exercise savedExercise = exerciseRepository.save(exercise);

                // Ersten Set hinzufügen, falls Gewicht und Wiederholungen angegeben
                if (exerciseData.containsKey("weight") && exerciseData.containsKey("reps")) {
                    Set firstSet = new Set();
                    firstSet.setWeight((Integer) exerciseData.get("weight"));
                    firstSet.setReps((Integer) exerciseData.get("reps"));
                    firstSet.setExercise(savedExercise);
                    setRepository.save(firstSet);
                }

                return ResponseEntity.status(HttpStatus.CREATED).body(savedExercise);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message", "Fehler beim Speichern der Übung"
                ));
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Trainingseinheit löschen (NEU)
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteTrainingSession(@PathVariable Long sessionId) {
        Optional<TrainingSession> optionalSession = trainingSessionRepository.findById(sessionId);
        if (optionalSession.isPresent()) {
            // Cascade-Löschung erfolgt automatisch durch JPA-Konfiguration
            trainingSessionRepository.deleteById(sessionId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

