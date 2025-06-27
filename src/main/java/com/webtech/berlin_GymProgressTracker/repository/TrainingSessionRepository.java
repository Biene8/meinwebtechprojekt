package com.webtech.berlin_GymProgressTracker.repository;

import com.webtech.berlin_GymProgressTracker.model.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
}