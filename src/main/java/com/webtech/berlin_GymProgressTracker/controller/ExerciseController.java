package com.webtech.berlin_GymProgressTracker.controller;

import com.webtech.berlin_GymProgressTracker.model.Exercise;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://meinwebtechprojektfront.onrender.com"  // Render-Frontend-URL
})

@RestController
public class ExerciseController {

    @GetMapping("/")
    public List<Exercise> getExercises() {
        return List.of(
                new Exercise("Bankdrücken", 80, 10),
                new Exercise("Kniebeuge", 100, 8),
                new Exercise("Kreuzheben", 120, 6)
        );
    }
}
