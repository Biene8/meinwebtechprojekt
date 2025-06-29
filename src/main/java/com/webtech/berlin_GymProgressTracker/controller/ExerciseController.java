package com.webtech.berlin_GymProgressTracker.controller;

import com.webtech.berlin_GymProgressTracker.model.Exercise;
import com.webtech.berlin_GymProgressTracker.model.Set;
import com.webtech.berlin_GymProgressTracker.repository.ExerciseRepository;
import com.webtech.berlin_GymProgressTracker.repository.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {
        "http://localhost:8080",
        "http://localhost:8081",
        "https://meinwebtechprojektfront-uxql.onrender.com"
})
@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private SetRepository setRepository;

    @GetMapping
    public List<Exercise> getExercises() {
        return exerciseRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExercise(@PathVariable Long id) {
        return exerciseRepository.findById(id)
                .map(exercise -> ResponseEntity.ok().body(exercise))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExercise(@PathVariable Long id, @Valid @RequestBody Exercise exerciseDetails, BindingResult result) {
        // Validierungsfehler prüfen
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

        return exerciseRepository.findById(id).map(exercise -> {
            try {
                exercise.setName(exerciseDetails.getName());
                Exercise savedExercise = exerciseRepository.save(exercise);
                return ResponseEntity.ok().body(savedExercise);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message", "Fehler beim Aktualisieren der Übung",
                        "error", e.getMessage()
                ));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExercise(@PathVariable Long id) {
        return exerciseRepository.findById(id).map(exercise -> {
            try {
                exerciseRepository.delete(exercise);
                return ResponseEntity.ok().body(Map.of(
                        "message", "Übung erfolgreich gelöscht"
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message", "Fehler beim Löschen der Übung",
                        "error", e.getMessage()
                ));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{exerciseId}/sets")
    public ResponseEntity<?> addSetToExercise(@PathVariable Long exerciseId, @Valid @RequestBody Set set, BindingResult result) {
        // Validierungsfehler prüfen
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

        return exerciseRepository.findById(exerciseId).map(exercise -> {
            try {
                set.setExercise(exercise);

                exercise.addSet(set);

                Exercise savedExercise = exerciseRepository.save(exercise);

                Set savedSet = savedExercise.getSets().stream()
                        .filter(s -> s.getWeight().equals(set.getWeight()) && s.getReps().equals(set.getReps()))
                        .reduce((first, second) -> second) // Nimmt das zuletzt hinzugefügte
                        .orElse(set);

                return ResponseEntity.status(HttpStatus.CREATED).body(savedSet);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message", "Fehler beim Hinzufügen des Satzes",
                        "error", e.getMessage()
                ));
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}