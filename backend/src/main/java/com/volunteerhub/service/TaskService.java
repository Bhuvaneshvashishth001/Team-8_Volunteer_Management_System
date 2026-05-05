package com.volunteerhub.service;

import com.volunteerhub.dto.ApiDtos.EventRequest;
import com.volunteerhub.dto.ApiDtos.ShiftRequest;
import com.volunteerhub.dto.ApiDtos.TaskRequest;
import com.volunteerhub.model.Event;
import com.volunteerhub.model.Shift;
import com.volunteerhub.model.VolunteerTask;
import com.volunteerhub.repository.EventRepository;
import com.volunteerhub.repository.ShiftRepository;
import com.volunteerhub.repository.VolunteerTaskRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {
    private final EventRepository events;
    private final VolunteerTaskRepository tasks;
    private final ShiftRepository shifts;

    public TaskService(EventRepository events, VolunteerTaskRepository tasks, ShiftRepository shifts) {
        this.events = events;
        this.tasks = tasks;
        this.shifts = shifts;
    }

    public Event createEvent(EventRequest request) {
        return events.save(new Event(
                request.name(),
                request.cause(),
                request.location(),
                request.description(),
                request.venueLatitude(),
                request.venueLongitude(),
                request.checkInCode()));
    }

    public VolunteerTask createTask(TaskRequest request) {
        events.findById(request.eventId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return tasks.save(new VolunteerTask(
                request.eventId(),
                request.title(),
                request.description(),
                request.department(),
                request.location(),
                request.cause(),
                request.requiredSkills(),
                request.targetVolunteers()));
    }

    public Shift createShift(ShiftRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shift end time must be after start time");
        }
        tasks.findById(request.taskId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return shifts.save(new Shift(request.taskId(), request.startTime(), request.endTime(), request.capacity()));
    }

    public List<VolunteerTask> search(String skill, String location, String cause) {
        return tasks.findAll().stream()
                .filter(task -> blank(skill) || task.getRequiredSkills().contains(skill.toLowerCase(Locale.ROOT)))
                .filter(task -> blank(location) || contains(task.getLocation(), location))
                .filter(task -> blank(cause) || contains(task.getCause(), cause))
                .sorted(Comparator.comparing(VolunteerTask::getTitle))
                .toList();
    }

    public Event event(String id) {
        return events.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    public VolunteerTask task(String id) {
        return tasks.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    public Shift shift(String id) {
        return shifts.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found"));
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private boolean contains(String source, String query) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }
}
