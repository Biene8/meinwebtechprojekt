package com.webtech.berlin_GymProgressTracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webtech.berlin_GymProgressTracker.model.Exercise;
import com.webtech.berlin_GymProgressTracker.model.Set;
import com.webtech.berlin_GymProgressTracker.model.TrainingSession;
import com.webtech.berlin_GymProgressTracker.repository.ExerciseRepository;
import com.webtech.berlin_GymProgressTracker.repository.SetRepository;
import com.webtech.berlin_GymProgressTracker.repository.TrainingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Gym Progress Tracker Application Tests")
class BerlinGymProgressTrackerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        // Clean up database before each test
        setRepository.deleteAll();
        exerciseRepository.deleteAll();
        trainingSessionRepository.deleteAll();
    }

    @Test
    @DisplayName("Application context should load successfully")
    void contextLoads() {
        // This test ensures that the Spring application context loads without errors
    }

    @Nested
    @DisplayName("Training Session Tests")
    class TrainingSessionTests {

        @Test
        @DisplayName("Should create a new training session")
        void testCreateTrainingSession() throws Exception {
            mockMvc.perform(post("/training-sessions")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.startTime").exists())
                    .andExpect(jsonPath("$.endTime").doesNotExist());
        }

        @Test
        @DisplayName("Should get all training sessions")
        void testGetAllTrainingSessions() throws Exception {
            // Create test data
            TrainingSession session1 = new TrainingSession();
            session1.setStartTime(LocalDateTime.now().minusHours(2));
            trainingSessionRepository.save(session1);

            TrainingSession session2 = new TrainingSession();
            session2.setStartTime(LocalDateTime.now().minusHours(1));
            trainingSessionRepository.save(session2);

            mockMvc.perform(get("/training-sessions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").exists())
                    .andExpect(jsonPath("$[1].id").exists());
        }

        @Test
        @DisplayName("Should get specific training session by ID")
        void testGetTrainingSessionById() throws Exception {
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            mockMvc.perform(get("/training-sessions/" + savedSession.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(savedSession.getId()))
                    .andExpect(jsonPath("$.startTime").exists());
        }

        @Test
        @DisplayName("Should return 404 for non-existent training session")
        void testGetNonExistentTrainingSession() throws Exception {
            mockMvc.perform(get("/training-sessions/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should end a training session")
        void testEndTrainingSession() throws Exception {
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now().minusHours(1));
            TrainingSession savedSession = trainingSessionRepository.save(session);

            mockMvc.perform(put("/training-sessions/" + savedSession.getId() + "/end")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Training erfolgreich beendet"))
                    .andExpect(jsonPath("$.session.endTime").exists());
        }

        @Test
        @DisplayName("Should delete a training session")
        void testDeleteTrainingSession() throws Exception {
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            mockMvc.perform(delete("/training-sessions/" + savedSession.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Trainingseinheit erfolgreich gelöscht"));

            // Verify deletion
            assertFalse(trainingSessionRepository.existsById(savedSession.getId()));
        }
    }

    @Nested
    @DisplayName("Exercise Tests")
    class ExerciseTests {

        @Test
        @DisplayName("Should add exercise to training session")
        void testAddExerciseToSession() throws Exception {
            // Create training session first
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            String exerciseJson = """
                {
                    "name": "Bankdrücken",
                    "sets": [
                        {
                            "weight": 100,
                            "reps": 5
                        }
                    ]
                }
                """;

            mockMvc.perform(post("/training-sessions/" + savedSession.getId() + "/exercises")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(exerciseJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Bankdrücken"))
                    .andExpect(jsonPath("$.sets", hasSize(1)))
                    .andExpect(jsonPath("$.sets[0].weight").value(100))
                    .andExpect(jsonPath("$.sets[0].reps").value(5));
        }

        @Test
        @DisplayName("Should validate exercise data")
        void testAddExerciseWithInvalidData() throws Exception {
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            String invalidExerciseJson = """
                {
                    "name": "",
                    "sets": []
                }
                """;

            mockMvc.perform(post("/training-sessions/" + savedSession.getId() + "/exercises")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidExerciseJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validierungsfehler"));
        }

        @Test
        @DisplayName("Should update exercise name")
        void testUpdateExercise() throws Exception {
            // Create test data
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            Exercise exercise = new Exercise();
            exercise.setName("Bankdrücken");
            exercise.setTrainingSession(savedSession);
            Exercise savedExercise = exerciseRepository.save(exercise);

            String updateJson = """
                {
                    "name": "Schrägbankdrücken"
                }
                """;

            mockMvc.perform(put("/exercises/" + savedExercise.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Schrägbankdrücken"));
        }

        @Test
        @DisplayName("Should delete exercise")
        void testDeleteExercise() throws Exception {
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            Exercise exercise = new Exercise();
            exercise.setName("Kniebeuge");
            exercise.setTrainingSession(savedSession);
            Exercise savedExercise = exerciseRepository.save(exercise);

            mockMvc.perform(delete("/exercises/" + savedExercise.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Übung erfolgreich gelöscht"));

            assertFalse(exerciseRepository.existsById(savedExercise.getId()));
        }

        @Test
        @DisplayName("Should add set to existing exercise")
        void testAddSetToExercise() throws Exception {
            // Create test data
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            Exercise exercise = new Exercise();
            exercise.setName("Bizeps Curls");
            exercise.setTrainingSession(savedSession);
            Exercise savedExercise = exerciseRepository.save(exercise);

            String setJson = """
                {
                    "weight": 25,
                    "reps": 12
                }
                """;

            mockMvc.perform(post("/exercises/" + savedExercise.getId() + "/sets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(setJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.weight").value(25))
                    .andExpect(jsonPath("$.reps").value(12));
        }
    }

    @Nested
    @DisplayName("Set Tests")
    class SetTests {

        @Test
        @DisplayName("Should update set data")
        void testUpdateSet() throws Exception {
            // Create test data
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            Exercise exercise = new Exercise();
            exercise.setName("Deadlift");
            exercise.setTrainingSession(savedSession);
            Exercise savedExercise = exerciseRepository.save(exercise);

            Set set = new Set();
            set.setWeight(120);
            set.setReps(5);
            set.setExercise(savedExercise);
            Set savedSet = setRepository.save(set);

            String updateJson = """
                {
                    "weight": 130,
                    "reps": 3
                }
                """;

            mockMvc.perform(put("/sets/" + savedSet.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.weight").value(130))
                    .andExpect(jsonPath("$.reps").value(3));
        }

        @Test
        @DisplayName("Should delete set")
        void testDeleteSet() throws Exception {
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            Exercise exercise = new Exercise();
            exercise.setName("Squats");
            exercise.setTrainingSession(savedSession);
            Exercise savedExercise = exerciseRepository.save(exercise);

            Set set = new Set();
            set.setWeight(80);
            set.setReps(10);
            set.setExercise(savedExercise);
            Set savedSet = setRepository.save(set);

            mockMvc.perform(delete("/sets/" + savedSet.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Satz erfolgreich gelöscht"));

            assertFalse(setRepository.existsById(savedSet.getId()));
        }

        @Test
        @DisplayName("Should validate set data with negative values")
        void testSetValidation() throws Exception {
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            Exercise exercise = new Exercise();
            exercise.setName("Test Exercise");
            exercise.setTrainingSession(savedSession);
            Exercise savedExercise = exerciseRepository.save(exercise);

            String invalidSetJson = """
                {
                    "weight": -10,
                    "reps": -5
                }
                """;

            mockMvc.perform(post("/exercises/" + savedExercise.getId() + "/sets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidSetJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validierungsfehler"));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should complete full workout flow")
        void testCompleteWorkoutFlow() throws Exception {
            // 1. Start training session
            MvcResult sessionResult = mockMvc.perform(post("/training-sessions")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn();

            String sessionResponse = sessionResult.getResponse().getContentAsString();
            TrainingSession createdSession = objectMapper.readValue(sessionResponse, TrainingSession.class);

            // 2. Add first exercise with sets
            String exercise1Json = """
                {
                    "name": "Bankdrücken",
                    "sets": [
                        {"weight": 80, "reps": 10},
                        {"weight": 85, "reps": 8}
                    ]
                }
                """;

            mockMvc.perform(post("/training-sessions/" + createdSession.getId() + "/exercises")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(exercise1Json))
                    .andExpect(status().isCreated());

            // 3. Add second exercise
            String exercise2Json = """
                {
                    "name": "Kniebeuge",
                    "sets": [
                        {"weight": 100, "reps": 12}
                    ]
                }
                """;

            mockMvc.perform(post("/training-sessions/" + createdSession.getId() + "/exercises")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(exercise2Json))
                    .andExpect(status().isCreated());

            // 4. Verify session has exercises
            mockMvc.perform(get("/training-sessions/" + createdSession.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.exercises", hasSize(2)))
                    .andExpect(jsonPath("$.exercises[0].name").value("Bankdrücken"))
                    .andExpect(jsonPath("$.exercises[0].sets", hasSize(2)))
                    .andExpect(jsonPath("$.exercises[1].name").value("Kniebeuge"))
                    .andExpect(jsonPath("$.exercises[1].sets", hasSize(1)));

            // 5. End training session
            mockMvc.perform(put("/training-sessions/" + createdSession.getId() + "/end")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Training erfolgreich beendet"));

            // 6. Verify session is ended
            mockMvc.perform(get("/training-sessions/" + createdSession.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.endTime").exists());
        }

        @Test
        @DisplayName("Should handle CORS preflight requests")
        void testCorsSupport() throws Exception {
            mockMvc.perform(options("/training-sessions")
                            .header("Origin", "https://meinwebtechprojektfront-uxql.onrender.com")
                            .header("Access-Control-Request-Method", "POST")
                            .header("Access-Control-Request-Headers", "Content-Type"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle malformed JSON")
        void testMalformedJson() throws Exception {
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            String malformedJson = "{ invalid json }";

            mockMvc.perform(post("/training-sessions/" + savedSession.getId() + "/exercises")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(malformedJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle missing content type")
        void testMissingContentType() throws Exception {
            TrainingSession session = new TrainingSession();
            session.setStartTime(LocalDateTime.now());
            TrainingSession savedSession = trainingSessionRepository.save(session);

            mockMvc.perform(post("/training-sessions/" + savedSession.getId() + "/exercises")
                            .content("{}"))
                    .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        @DisplayName("Should handle non-existent endpoints")
        void testNonExistentEndpoint() throws Exception {
            mockMvc.perform(get("/non-existent-endpoint"))
                    .andExpect(status().isNotFound());
        }
    }
}

