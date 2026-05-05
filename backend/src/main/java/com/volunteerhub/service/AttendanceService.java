package com.volunteerhub.service;

import com.volunteerhub.model.ApplicationStatus;
import com.volunteerhub.model.Attendance;
import com.volunteerhub.model.Badge;
import com.volunteerhub.model.Event;
import com.volunteerhub.model.Shift;
import com.volunteerhub.model.TaskApplication;
import com.volunteerhub.model.User;
import com.volunteerhub.model.VolunteerTask;
import com.volunteerhub.repository.AttendanceRepository;
import com.volunteerhub.repository.BadgeRepository;
import com.volunteerhub.repository.EventRepository;
import com.volunteerhub.repository.ShiftRepository;
import com.volunteerhub.repository.TaskApplicationRepository;
import com.volunteerhub.repository.UserRepository;
import com.volunteerhub.repository.VolunteerTaskRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AttendanceService {
    private static final double CHECK_IN_RADIUS_KM = 1.0;

    private final AttendanceRepository attendance;
    private final TaskApplicationRepository applications;
    private final ShiftRepository shifts;
    private final VolunteerTaskRepository tasks;
    private final EventRepository events;
    private final UserRepository users;
    private final BadgeRepository badges;

    public AttendanceService(AttendanceRepository attendance, TaskApplicationRepository applications,
                             ShiftRepository shifts, VolunteerTaskRepository tasks, EventRepository events,
                             UserRepository users, BadgeRepository badges) {
        this.attendance = attendance;
        this.applications = applications;
        this.shifts = shifts;
        this.tasks = tasks;
        this.events = events;
        this.users = users;
        this.badges = badges;
    }

    public Attendance checkIn(String applicationId, Double latitude, Double longitude, String code) {
        TaskApplication application = application(applicationId);
        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Application must be approved before check-in");
        }
        Event event = eventFor(application);
        boolean qrVerified = code != null && event.getCheckInCode() != null
                && event.getCheckInCode().equalsIgnoreCase(code.trim());
        boolean locationVerified = latitude != null && longitude != null
                && distanceKm(latitude, longitude, event.getVenueLatitude(), event.getVenueLongitude()) <= CHECK_IN_RADIUS_KM;
        if (!qrVerified && !locationVerified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-in requires a valid QR code or venue geolocation");
        }

        Attendance record = attendance.findByApplicationId(applicationId).orElseGet(() -> new Attendance(applicationId));
        record.setCheckedInAt(LocalDateTime.now());
        record.setQrVerified(qrVerified);
        record.setLocationVerified(locationVerified);
        return attendance.save(record);
    }

    public Attendance checkOut(String applicationId) {
        Attendance record = attendance.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Volunteer has not checked in"));
        record.setCheckedOutAt(LocalDateTime.now());
        record.setHours(calculateHours(record));
        return attendance.save(record);
    }

    public Attendance verify(String attendanceId) {
        Attendance record = attendance.findById(attendanceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found"));
        if (record.getCheckedInAt() == null || record.getCheckedOutAt() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Check-in and check-out are required before verification");
        }
        record.setOrganizerVerified(true);
        record.setHours(calculateHours(record));

        if (!record.isHoursAwarded()) {
            TaskApplication application = application(record.getApplicationId());
            User volunteer = users.findById(application.getVolunteerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Volunteer not found"));
            volunteer.setTotalHours(roundOne(volunteer.getTotalHours() + record.getHours()));
            volunteer.setPoints(volunteer.getPoints() + Math.max(25, (int) Math.round(record.getHours() * 20)));
            badges.findAll().stream()
                    .filter(badge -> volunteer.getTotalHours() >= badge.getRequiredHours())
                    .map(Badge::getId)
                    .forEach(volunteer.getBadgeIds()::add);
            users.save(volunteer);
            record.setHoursAwarded(true);
        }
        return attendance.save(record);
    }

    private TaskApplication application(String id) {
        return applications.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    private Event eventFor(TaskApplication application) {
        Shift shift = shifts.findById(application.getShiftId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found"));
        VolunteerTask task = tasks.findById(shift.getTaskId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return events.findById(task.getEventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    private double calculateHours(Attendance record) {
        if (record.getCheckedInAt() == null || record.getCheckedOutAt() == null) {
            return 0;
        }
        long minutes = Math.max(15, Duration.between(record.getCheckedInAt(), record.getCheckedOutAt()).toMinutes());
        return roundOne(minutes / 60.0);
    }

    private double roundOne(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
