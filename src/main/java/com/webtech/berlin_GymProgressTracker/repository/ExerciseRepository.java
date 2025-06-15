package com.webtech.berlin_GymProgressTracker.repository;

import com.webtech.berlin_GymProgressTracker.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}