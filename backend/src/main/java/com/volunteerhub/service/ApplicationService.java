package com.volunteerhub.service;

import com.volunteerhub.dto.ApiDtos.ApplicationView;
import com.volunteerhub.model.ApplicationStatus;
import com.volunteerhub.model.Attendance;
import com.volunteerhub.model.Event;
import com.volunteerhub.model.Shift;
import com.volunteerhub.model.TaskApplication;
import com.volunteerhub.model.User;
import com.volunteerhub.model.VolunteerTask;
import com.volunteerhub.repository.AttendanceRepository;
import com.volunteerhub.repository.EventRepository;
import com.volunteerhub.repository.ShiftRepository;
import com.volunteerhub.repository.TaskApplicationRepository;
import com.volunteerhub.repository.UserRepository;
import com.volunteerhub.repository.VolunteerTaskRepository;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ApplicationService {
    private final TaskApplicationRepository applications;
    private final UserRepository users;
    private final ShiftRepository shifts;
    private final VolunteerTaskRepository tasks;
    private final EventRepository events;
    private final AttendanceRepository attendance;
    private final MatchingService matching;

    public ApplicationService(TaskApplicationRepository applications, UserRepository users, ShiftRepository shifts,
                              VolunteerTaskRepository tasks, EventRepository events, AttendanceRepository attendance,
                              MatchingService matching) {
        this.applications = applications;
        this.users = users;
        this.shifts = shifts;
        this.tasks = tasks;
        this.events = events;
        this.attendance = attendance;
        this.matching = matching;
    }

    public TaskApplication apply(String volunteerId, String shiftId) {
        users.findById(volunteerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Volunteer not found"));
        Shift desiredShift = shifts.findById(shiftId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found"));

        applications.findByVolunteerIdAndShiftId(volunteerId, shiftId)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "You already applied for this shift");
                });

        boolean overlaps = applications.findByVolunteerIdAndStatusIn(volunteerId, Set.of(ApplicationStatus.PENDING, ApplicationStatus.APPROVED))
                .stream()
                .map(TaskApplication::getShiftId)
                .flatMap(id -> shifts.findById(id).stream())
                .anyMatch(existing -> overlaps(existing, desiredShift));
        if (overlaps) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Scheduling engine blocked double-booking");
        }

        if (matching.shiftSummary(desiredShift).remainingSlots() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Shift is already full");
        }

        return applications.save(new TaskApplication(volunteerId, shiftId));
    }

    public TaskApplication approve(String applicationId, String team) {
        TaskApplication application = get(applicationId);
        Shift shift = shifts.findById(application.getShiftId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found"));
        if (matching.shiftSummary(shift).remainingSlots() <= 0 && application.getStatus() != ApplicationStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No remaining slots on this shift");
        }
        application.setStatus(ApplicationStatus.APPROVED);
        application.setTeam(team == null || team.isBlank() ? "General Team" : team);
        return applications.save(application);
    }

    public TaskApplication reject(String applicationId) {
        TaskApplication application = get(applicationId);
        application.setStatus(ApplicationStatus.REJECTED);
        return applications.save(application);
    }

    public List<ApplicationView> allViews() {
        return applications.findAll().stream().map(this::view).toList();
    }

    public List<ApplicationView> viewsForVolunteer(String volunteerId) {
        return applications.findByVolunteerId(volunteerId).stream().map(this::view).toList();
    }

    public ApplicationView view(TaskApplication application) {
        User volunteer = users.findById(application.getVolunteerId()).orElse(null);
        Shift shift = shifts.findById(application.getShiftId()).orElse(null);
        VolunteerTask task = shift == null ? null : tasks.findById(shift.getTaskId()).orElse(null);
        Event event = task == null ? null : events.findById(task.getEventId()).orElse(null);
        Attendance existingAttendance = attendance.findByApplicationId(application.getId()).orElse(null);
        return new ApplicationView(application, volunteer, task, event, shift, existingAttendance);
    }

    public TaskApplication get(String id) {
        return applications.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    private boolean overlaps(Shift first, Shift second) {
        return first.getStartTime().isBefore(second.getEndTime()) && second.getStartTime().isBefore(first.getEndTime());
    }
}
