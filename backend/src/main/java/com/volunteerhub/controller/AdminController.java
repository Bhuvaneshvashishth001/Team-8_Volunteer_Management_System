package com.volunteerhub.controller;

import com.volunteerhub.dto.ApiDtos.ApplicationView;
import com.volunteerhub.dto.ApiDtos.DepartmentCoverage;
import com.volunteerhub.dto.ApiDtos.EventRequest;
import com.volunteerhub.dto.ApiDtos.NotificationRequest;
import com.volunteerhub.dto.ApiDtos.ShiftRequest;
import com.volunteerhub.dto.ApiDtos.TaskRequest;
import com.volunteerhub.dto.ApiDtos.TeamRequest;
import com.volunteerhub.model.Event;
import com.volunteerhub.model.NotificationMessage;
import com.volunteerhub.model.Shift;
import com.volunteerhub.model.TaskApplication;
import com.volunteerhub.model.VolunteerTask;
import com.volunteerhub.repository.NotificationRepository;
import com.volunteerhub.service.AnalyticsService;
import com.volunteerhub.service.ApplicationService;
import com.volunteerhub.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final TaskService taskService;
    private final ApplicationService applicationService;
    private final AnalyticsService analyticsService;
    private final NotificationRepository notifications;

    public AdminController(TaskService taskService, ApplicationService applicationService,
                           AnalyticsService analyticsService, NotificationRepository notifications) {
        this.taskService = taskService;
        this.applicationService = applicationService;
        this.analyticsService = analyticsService;
        this.notifications = notifications;
    }

    @PostMapping("/events")
    public Event createEvent(@Valid @RequestBody EventRequest request) {
        return taskService.createEvent(request);
    }

    @PostMapping("/tasks")
    public VolunteerTask createTask(@Valid @RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    @PostMapping("/shifts")
    public Shift createShift(@Valid @RequestBody ShiftRequest request) {
        return taskService.createShift(request);
    }

    @GetMapping("/applications")
    public List<ApplicationView> applications() {
        return applicationService.allViews();
    }

    @PostMapping("/applications/{id}/approve")
    public TaskApplication approve(@PathVariable String id, @RequestBody(required = false) TeamRequest request) {
        return applicationService.approve(id, request == null ? null : request.team());
    }

    @PostMapping("/applications/{id}/reject")
    public TaskApplication reject(@PathVariable String id) {
        return applicationService.reject(id);
    }

    @GetMapping("/analytics")
    public List<DepartmentCoverage> analytics() {
        return analyticsService.coverage();
    }

    @PostMapping("/notifications")
    public NotificationMessage broadcast(@Valid @RequestBody NotificationRequest request) {
        NotificationMessage message = new NotificationMessage();
        message.setEventId(request.eventId());
        message.setAudience(request.audience() == null ? "ALL_VOLUNTEERS" : request.audience());
        message.setMessage(request.message());
        return notifications.save(message);
    }
}
