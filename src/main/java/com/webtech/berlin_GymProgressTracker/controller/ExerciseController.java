package com.webtech.berlin_GymProgressTracker.controller;

import com.webtech.berlin_GymProgressTracker.model.Exercise;
import com.webtech.berlin_GymProgressTracker.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:5173",
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
    public Exercise createExercise(@RequestBody Exercise exercise) {
        return exerciseRepository.save(exercise);
    }
}