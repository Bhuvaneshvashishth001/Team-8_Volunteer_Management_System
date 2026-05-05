package com.volunteerhub.service;

import com.volunteerhub.dto.ApiDtos.RecommendationDto;
import com.volunteerhub.dto.ApiDtos.ShiftSummary;
import com.volunteerhub.model.ApplicationStatus;
import com.volunteerhub.model.Event;
import com.volunteerhub.model.Shift;
import com.volunteerhub.model.User;
import com.volunteerhub.model.VolunteerTask;
import com.volunteerhub.repository.EventRepository;
import com.volunteerhub.repository.ShiftRepository;
import com.volunteerhub.repository.TaskApplicationRepository;
import com.volunteerhub.repository.UserRepository;
import com.volunteerhub.repository.VolunteerTaskRepository;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MatchingService {
    private final UserRepository users;
    private final VolunteerTaskRepository tasks;
    private final EventRepository events;
    private final ShiftRepository shifts;
    private final TaskApplicationRepository applications;

    public MatchingService(UserRepository users, VolunteerTaskRepository tasks, EventRepository events,
                           ShiftRepository shifts, TaskApplicationRepository applications) {
        this.users = users;
        this.tasks = tasks;
        this.events = events;
        this.shifts = shifts;
        this.applications = applications;
    }

    public List<RecommendationDto> recommendations(String volunteerId, String skill, String location, String cause) {
        User volunteer = users.findById(volunteerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Volunteer not found"));

        return tasks.findAll().stream()
                .filter(task -> blank(skill) || task.getRequiredSkills().contains(skill.toLowerCase(Locale.ROOT)))
                .filter(task -> blank(location) || contains(task.getLocation(), location))
                .filter(task -> blank(cause) || contains(task.getCause(), cause))
                .map(task -> buildRecommendation(volunteer, task))
                .sorted(Comparator.comparing(RecommendationDto::matchScore).reversed())
                .toList();
    }

    public ShiftSummary shiftSummary(Shift shift) {
        int approved = (int) applications.findByShiftId(shift.getId()).stream()
                .filter(app -> app.getStatus() == ApplicationStatus.APPROVED)
                .count();
        int pending = (int) applications.findByShiftId(shift.getId()).stream()
                .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
                .count();
        return new ShiftSummary(shift, approved, pending, Math.max(shift.getCapacity() - approved, 0));
    }

    private RecommendationDto buildRecommendation(User volunteer, VolunteerTask task) {
        Event event = events.findById(task.getEventId()).orElse(null);
        Set<String> matched = new HashSet<>(volunteer.getSkills());
        matched.retainAll(task.getRequiredSkills());

        int skillScore = task.getRequiredSkills().isEmpty()
                ? 45
                : (int) Math.round((matched.size() * 70.0) / task.getRequiredSkills().size());
        int locationScore = contains(task.getLocation(), volunteer.getLocation()) ? 15 : 0;
        int causeScore = event != null && contains(task.getCause(), event.getCause()) ? 5 : 0;
        int availabilityScore = volunteer.getAvailability() == null || volunteer.getAvailability().isBlank() ? 0 : 10;
        int score = Math.min(100, skillScore + locationScore + causeScore + availabilityScore);

        String reason = matched.isEmpty()
                ? "Low skill overlap, but open capacity is available."
                : "Matches " + String.join(", ", matched) + ".";
        List<ShiftSummary> summaries = shifts.findByTaskId(task.getId()).stream()
                .map(this::shiftSummary)
                .sorted(Comparator.comparing(summary -> summary.shift().getStartTime()))
                .toList();
        return new RecommendationDto(task, event, summaries, score, matched, reason);
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private boolean contains(String source, String query) {
        return source != null && query != null
                && source.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }
}
