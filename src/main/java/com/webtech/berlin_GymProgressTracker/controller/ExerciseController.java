package com.webtech.berlin_GymProgressTracker.controller;

import com.webtech.berlin_GymProgressTracker.model.Exercise;
import com.webtech.berlin_GymProgressTracker.repository.ExerciseRepository;
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
        "http://localhost:8081",
        "https://meinwebtechprojektfront.onrender.com"
})
@RestController
@RequestMapping("/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @GetMapping
    public List<Exercise> getExercises() {
        return exerciseRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createExercise(@Valid @RequestBody Exercise exercise, BindingResult result) {
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

        try {
            Exercise savedExercise = exerciseRepository.save(exercise);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedExercise);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "Fehler beim Speichern der Übung"
            ));
        }
    }
}

