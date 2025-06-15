package com.webtech.berlin_GymProgressTracker;

import com.webtech.berlin_GymProgressTracker.model.Exercise;
import com.webtech.berlin_GymProgressTracker.repository.ExerciseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BerlinGymProgressTrackerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @BeforeEach
    void setup() {
        exerciseRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testCreateExercise() throws Exception {
        mockMvc.perform(post("/exercises")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Bankdrücken\",\"weight\":80,\"reps\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Bankdrücken"))
                .andExpect(jsonPath("$.weight").value(80))
                .andExpect(jsonPath("$.reps").value(10));
    }

    @Test
    void testGetExercises() throws Exception {
        Exercise ex = new Exercise("Kniebeuge", 100, 8);
        exerciseRepository.save(ex);

        mockMvc.perform(get("/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Kniebeuge"));
    }

    @Test
    void testCreateExerciseWithInvalidData() throws Exception {
        // Name fehlt
        mockMvc.perform(post("/exercises")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"weight\":50,\"reps\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").doesNotExist());
    }
}