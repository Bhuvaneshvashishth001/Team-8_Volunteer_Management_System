package com.volunteerhub.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "badges")
public class Badge {
    @Id
    private String id;
    private String name;
    private String description;
    private double requiredHours;
    private int points;

    public Badge() {
    }

    public Badge(String name, String description, double requiredHours, int points) {
        this.name = name;
        this.description = description;
        this.requiredHours = requiredHours;
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRequiredHours() {
        return requiredHours;
    }

    public void setRequiredHours(double requiredHours) {
        this.requiredHours = requiredHours;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
