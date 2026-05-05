package com.volunteerhub.model;

import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
public class VolunteerTask {
    @Id
    private String id;
    private String eventId;
    private String title;
    private String description;
    private String department;
    private String location;
    private String cause;
    private Set<String> requiredSkills = new HashSet<>();
    private int targetVolunteers;

    public VolunteerTask() {
    }

    public VolunteerTask(String eventId, String title, String description, String department,
                         String location, String cause, Set<String> requiredSkills, int targetVolunteers) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.department = department;
        this.location = location;
        this.cause = cause;
        setRequiredSkills(requiredSkills);
        this.targetVolunteers = targetVolunteers;
    }

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Set<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(Set<String> requiredSkills) {
        this.requiredSkills = new HashSet<>();
        if (requiredSkills != null) {
            requiredSkills.stream()
                    .filter(skill -> skill != null && !skill.isBlank())
                    .map(skill -> skill.trim().toLowerCase())
                    .forEach(this.requiredSkills::add);
        }
    }

    public int getTargetVolunteers() {
        return targetVolunteers;
    }

    public void setTargetVolunteers(int targetVolunteers) {
        this.targetVolunteers = targetVolunteers;
    }
}
