package com.webtech.berlin_GymProgressTracker.controller;

import com.webtech.berlin_GymProgressTracker.model.Exercise;
import com.webtech.berlin_GymProgressTracker.model.Set;
import com.webtech.berlin_GymProgressTracker.repository.ExerciseRepository;
import com.webtech.berlin_GymProgressTracker.repository.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = {
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

    // Übung aktualisieren (hauptsächlich Name)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExercise(@PathVariable Long id, @Valid @RequestBody Exercise updatedExercise, BindingResult result) {
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

        Optional<Exercise> existingExercise = exerciseRepository.findById(id);
        if (existingExercise.isPresent()) {
            Exercise exercise = existingExercise.get();
            exercise.setName(updatedExercise.getName());
            Exercise savedExercise = exerciseRepository.save(exercise);
            return ResponseEntity.ok(savedExercise);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Set zu einer Übung hinzufügen
    @PostMapping("/{exerciseId}/sets")
    public ResponseEntity<?> addSetToExercise(@PathVariable Long exerciseId, @Valid @RequestBody Set newSet, BindingResult result) {
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

        Optional<Exercise> optionalExercise = exerciseRepository.findById(exerciseId);
        if (optionalExercise.isPresent()) {
            Exercise exercise = optionalExercise.get();
            newSet.setExercise(exercise);
            try {
                Set savedSet = setRepository.save(newSet);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedSet);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message", "Fehler beim Speichern des Sets"
                ));
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Set aktualisieren
    @PutMapping("/{exerciseId}/sets/{setId}")
    public ResponseEntity<?> updateSet(@PathVariable Long exerciseId, @PathVariable Long setId, @Valid @RequestBody Set updatedSet, BindingResult result) {
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

        Optional<Set> existingSet = setRepository.findById(setId);
        if (existingSet.isPresent()) {
            Set set = existingSet.get();
            // Überprüfen ob das Set zur richtigen Übung gehört
            if (!set.getExercise().getId().equals(exerciseId)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Set gehört nicht zu dieser Übung"
                ));
            }
            
            set.setWeight(updatedSet.getWeight());
            set.setReps(updatedSet.getReps());
            Set savedSet = setRepository.save(set);
            return ResponseEntity.ok(savedSet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Set löschen
    @DeleteMapping("/{exerciseId}/sets/{setId}")
    public ResponseEntity<Void> deleteSet(@PathVariable Long exerciseId, @PathVariable Long setId) {
        Optional<Set> existingSet = setRepository.findById(setId);
        if (existingSet.isPresent()) {
            Set set = existingSet.get();
            // Überprüfen ob das Set zur richtigen Übung gehört
            if (!set.getExercise().getId().equals(exerciseId)) {
                return ResponseEntity.badRequest().build();
            }
            
            setRepository.deleteById(setId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Übung löschen (bleibt bestehen)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        if (exerciseRepository.existsById(id)) {
            exerciseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

