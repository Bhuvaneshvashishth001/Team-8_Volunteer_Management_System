package com.volunteerhub.dto;

import com.volunteerhub.model.ApplicationStatus;
import com.volunteerhub.model.Badge;
import com.volunteerhub.model.Event;
import com.volunteerhub.model.Shift;
import com.volunteerhub.model.TaskApplication;
import com.volunteerhub.model.User;
import com.volunteerhub.model.VolunteerTask;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record UserRequest(
            @NotBlank String name,
            @Email String email,
            String role,
            Set<String> skills,
            String availability,
            String location) {
    }

    public record EventRequest(
            @NotBlank String name,
            @NotBlank String cause,
            @NotBlank String location,
            String description,
            double venueLatitude,
            double venueLongitude,
            @NotBlank String checkInCode) {
    }

    public record TaskRequest(
            @NotBlank String eventId,
            @NotBlank String title,
            String description,
            @NotBlank String department,
            @NotBlank String location,
            @NotBlank String cause,
            Set<String> requiredSkills,
            int targetVolunteers) {
    }

    public record ShiftRequest(
            @NotBlank String taskId,
            @NotNull LocalDateTime startTime,
            @NotNull LocalDateTime endTime,
            int capacity) {
    }

    public record ApplyRequest(@NotBlank String volunteerId, @NotBlank String shiftId) {
    }

    public record TeamRequest(String team) {
    }

    public record CheckInRequest(Double latitude, Double longitude, String code) {
    }

    public record FeedbackRequest(
            @NotBlank String fromUserId,
            @NotBlank String toUserId,
            @NotBlank String applicationId,
            @Min(1) @Max(5) int rating,
            String comment) {
    }

    public record NotificationRequest(String eventId, String audience, @NotBlank String message) {
    }

    public record ShiftSummary(Shift shift, int approvedCount, int pendingCount, int remainingSlots) {
    }

    public record RecommendationDto(
            VolunteerTask task,
            Event event,
            List<ShiftSummary> shifts,
            int matchScore,
            Set<String> matchedSkills,
            String reason) {
    }

    public record ApplicationView(
            TaskApplication application,
            User volunteer,
            VolunteerTask task,
            Event event,
            Shift shift,
            Object attendance) {
    }

    public record DepartmentCoverage(String department, int approved, int needed, int pending) {
    }

    public record Dashboard(User volunteer, List<Badge> earnedBadges, List<ApplicationView> applications) {
    }

    public record Bootstrap(
            List<User> volunteers,
            List<Event> events,
            List<VolunteerTask> tasks,
            List<Shift> shifts,
            List<TaskApplication> applications,
            List<Badge> badges) {
    }

    public record StatusUpdate(ApplicationStatus status) {
    }
}
