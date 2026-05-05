package com.volunteerhub.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "attendance")
public class Attendance {
    @Id
    private String id;
    private String applicationId;
    private LocalDateTime checkedInAt;
    private LocalDateTime checkedOutAt;
    private boolean locationVerified;
    private boolean qrVerified;
    private boolean organizerVerified;
    private boolean hoursAwarded;
    private double hours;

    public Attendance() {
    }

    public Attendance(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getId() {
        return id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public LocalDateTime getCheckedOutAt() {
        return checkedOutAt;
    }

    public void setCheckedOutAt(LocalDateTime checkedOutAt) {
        this.checkedOutAt = checkedOutAt;
    }

    public boolean isLocationVerified() {
        return locationVerified;
    }

    public void setLocationVerified(boolean locationVerified) {
        this.locationVerified = locationVerified;
    }

    public boolean isQrVerified() {
        return qrVerified;
    }

    public void setQrVerified(boolean qrVerified) {
        this.qrVerified = qrVerified;
    }

    public boolean isOrganizerVerified() {
        return organizerVerified;
    }

    public void setOrganizerVerified(boolean organizerVerified) {
        this.organizerVerified = organizerVerified;
    }

    public boolean isHoursAwarded() {
        return hoursAwarded;
    }

    public void setHoursAwarded(boolean hoursAwarded) {
        this.hoursAwarded = hoursAwarded;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }
}
