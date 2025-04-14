package com.webtech.berlin_GymProgressTracker.model;


public class Exercise {
    private String name;
    private int weight;
    private int reps;

    public Exercise(String name, int weight, int reps) {
        this.name = name;
        this.weight = weight;
        this.reps = reps;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public int getReps() {
        return reps;
    }
}