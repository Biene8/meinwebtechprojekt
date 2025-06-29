package com.webtech.berlin_GymProgressTracker.controller;

import com.webtech.berlin_GymProgressTracker.model.Set;
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
@RequestMapping("/sets")
public class SetController {

    @Autowired
    private SetRepository setRepository;

    // Alle Sätze abrufen
    @GetMapping
    public List<Set> getAllSets() {
        return setRepository.findAll();
    }

    // Spezifischen Satz abrufen
    @GetMapping("/{id}")
    public ResponseEntity<Set> getSet(@PathVariable Long id) {
        return setRepository.findById(id)
                .map(set -> ResponseEntity.ok().body(set))
                .orElse(ResponseEntity.notFound().build());
    }

    // Satz aktualisieren
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSet(@PathVariable Long id, @Valid @RequestBody Set setDetails, BindingResult result) {
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

        return setRepository.findById(id).map(set -> {
            try {
                // Nur Gewicht und Wiederholungen aktualisieren
                set.setWeight(setDetails.getWeight());
                set.setReps(setDetails.getReps());

                Set savedSet = setRepository.save(set);
                return ResponseEntity.ok().body(savedSet);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message", "Fehler beim Aktualisieren des Satzes",
                        "error", e.getMessage()
                ));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    // Satz löschen
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSet(@PathVariable Long id) {
        return setRepository.findById(id).map(set -> {
            try {
                // Set aus der Exercise entfernen (bidirektionale Beziehung)
                if (set.getExercise() != null) {
                    set.getExercise().removeSet(set);
                }

                setRepository.delete(set);
                return ResponseEntity.ok().body(Map.of(
                        "message", "Satz erfolgreich gelöscht"
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "message", "Fehler beim Löschen des Satzes",
                        "error", e.getMessage()
                ));
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}