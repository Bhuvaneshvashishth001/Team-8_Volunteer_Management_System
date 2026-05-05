package com.volunteerhub.controller;

import com.volunteerhub.dto.ApiDtos.ApplyRequest;
import com.volunteerhub.model.Event;
import com.volunteerhub.model.Shift;
import com.volunteerhub.model.TaskApplication;
import com.volunteerhub.model.VolunteerTask;
import com.volunteerhub.repository.EventRepository;
import com.volunteerhub.repository.ShiftRepository;
import com.volunteerhub.service.ApplicationService;
import com.volunteerhub.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TaskController {
    private final TaskService taskService;
    private final ApplicationService applicationService;
    private final EventRepository events;
    private final ShiftRepository shifts;

    public TaskController(TaskService taskService, ApplicationService applicationService,
                          EventRepository events, ShiftRepository shifts) {
        this.taskService = taskService;
        this.applicationService = applicationService;
        this.events = events;
        this.shifts = shifts;
    }

    @GetMapping("/events")
    public List<Event> events() {
        return events.findAll();
    }

    @GetMapping("/tasks")
    public List<VolunteerTask> tasks(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String cause) {
        return taskService.search(skill, location, cause);
    }

    @GetMapping("/shifts")
    public List<Shift> shifts() {
        return shifts.findAll();
    }

    @PostMapping("/applications")
    public TaskApplication apply(@Valid @RequestBody ApplyRequest request) {
        return applicationService.apply(request.volunteerId(), request.shiftId());
    }
}
