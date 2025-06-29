package com.webtech.berlin_GymProgressTracker.repository;

import com.webtech.berlin_GymProgressTracker.model.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetRepository extends JpaRepository<Set, Long> {
    // Spring Data JPA stellt automatisch CRUD-Operationen bereit
    // Hier können bei Bedarf benutzerdefinierte Abfragen hinzugefügt werden
}

