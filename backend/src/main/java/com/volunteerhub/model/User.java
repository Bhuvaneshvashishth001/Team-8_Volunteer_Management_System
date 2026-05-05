package com.volunteerhub.model;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private Role role;
    private Set<String> skills = new HashSet<>();
    private String availability;
    private String location;
    private double totalHours;
    private int points;
    private Set<String> badgeIds = new HashSet<>();

    public User() {
    }

    public User(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = normalize(skills);
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Set<String> getBadgeIds() {
        return badgeIds;
    }

    public void setBadgeIds(Set<String> badgeIds) {
        this.badgeIds = badgeIds == null ? new HashSet<>() : badgeIds;
    }

    private Set<String> normalize(Set<String> input) {
        Set<String> normalized = new HashSet<>();
        if (input != null) {
            input.stream()
                    .filter(item -> item != null && !item.isBlank())
                    .map(item -> item.trim().toLowerCase())
                    .forEach(normalized::add);
        }
        return normalized;
    }
}
