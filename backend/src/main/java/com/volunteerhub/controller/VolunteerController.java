package com.volunteerhub.controller;

import com.volunteerhub.dto.ApiDtos.Bootstrap;
import com.volunteerhub.dto.ApiDtos.Dashboard;
import com.volunteerhub.dto.ApiDtos.RecommendationDto;
import com.volunteerhub.dto.ApiDtos.UserRequest;
import com.volunteerhub.model.Badge;
import com.volunteerhub.model.Role;
import com.volunteerhub.model.User;
import com.volunteerhub.repository.BadgeRepository;
import com.volunteerhub.repository.EventRepository;
import com.volunteerhub.repository.ShiftRepository;
import com.volunteerhub.repository.TaskApplicationRepository;
import com.volunteerhub.repository.UserRepository;
import com.volunteerhub.repository.VolunteerTaskRepository;
import com.volunteerhub.service.ApplicationService;
import com.volunteerhub.service.MatchingService;
import com.volunteerhub.service.VolunteerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VolunteerController {
    private final VolunteerService volunteerService;
    private final MatchingService matchingService;
    private final ApplicationService applicationService;
    private final UserRepository users;
    private final EventRepository events;
    private final VolunteerTaskRepository tasks;
    private final ShiftRepository shifts;
    private final TaskApplicationRepository applications;
    private final BadgeRepository badges;

    public VolunteerController(VolunteerService volunteerService, MatchingService matchingService,
                               ApplicationService applicationService, UserRepository users,
                               EventRepository events, VolunteerTaskRepository tasks, ShiftRepository shifts,
                               TaskApplicationRepository applications, BadgeRepository badges) {
        this.volunteerService = volunteerService;
        this.matchingService = matchingService;
        this.applicationService = applicationService;
        this.users = users;
        this.events = events;
        this.tasks = tasks;
        this.shifts = shifts;
        this.applications = applications;
        this.badges = badges;
    }

    @GetMapping("/bootstrap")
    public Bootstrap bootstrap() {
        return new Bootstrap(
                users.findByRole(Role.VOLUNTEER),
                events.findAll(),
                tasks.findAll(),
                shifts.findAll(),
                applications.findAll(),
                badges.findAll());
    }

    @GetMapping("/volunteers")
    public List<User> volunteers() {
        return volunteerService.volunteers();
    }

    @PostMapping("/volunteers")
    public User createVolunteer(@Valid @RequestBody UserRequest request) {
        return volunteerService.createOrUpdate(request);
    }

    @PutMapping("/volunteers/{id}")
    public User updateVolunteer(@PathVariable String id, @Valid @RequestBody UserRequest request) {
        return volunteerService.update(id, request);
    }

    @GetMapping("/volunteers/{id}/recommendations")
    public List<RecommendationDto> recommendations(
            @PathVariable String id,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String cause) {
        return matchingService.recommendations(id, skill, location, cause);
    }

    @GetMapping("/volunteers/{id}/dashboard")
    public Dashboard dashboard(@PathVariable String id) {
        User volunteer = volunteerService.get(id);
        List<Badge> earned = badges.findAllById(volunteer.getBadgeIds());
        return new Dashboard(volunteer, earned, applicationService.viewsForVolunteer(id));
    }
}
