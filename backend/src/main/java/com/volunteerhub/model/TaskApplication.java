package com.volunteerhub.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "applications")
public class TaskApplication {
    @Id
    private String id;
    private String volunteerId;
    private String shiftId;
    private ApplicationStatus status = ApplicationStatus.PENDING;
    private String team;
    private LocalDateTime appliedAt = LocalDateTime.now();

    public TaskApplication() {
    }

    public TaskApplication(String volunteerId, String shiftId) {
        this.volunteerId = volunteerId;
        this.shiftId = shiftId;
    }

    public String getId() {
        return id;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}
