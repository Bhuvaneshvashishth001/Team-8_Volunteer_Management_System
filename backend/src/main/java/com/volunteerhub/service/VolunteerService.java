package com.volunteerhub.service;

import com.volunteerhub.dto.ApiDtos.UserRequest;
import com.volunteerhub.model.Role;
import com.volunteerhub.model.User;
import com.volunteerhub.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VolunteerService {
    private final UserRepository users;

    public VolunteerService(UserRepository users) {
        this.users = users;
    }

    public List<User> volunteers() {
        return users.findByRole(Role.VOLUNTEER);
    }

    public User createOrUpdate(UserRequest request) {
        User user = users.findByEmail(request.email()).orElseGet(User::new);
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(parseRole(request.role()));
        user.setSkills(request.skills());
        user.setAvailability(request.availability());
        user.setLocation(request.location());
        return users.save(user);
    }

    public User update(String id, UserRequest request) {
        User user = get(id);
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(parseRole(request.role()));
        user.setSkills(request.skills());
        user.setAvailability(request.availability());
        user.setLocation(request.location());
        return users.save(user);
    }

    public User get(String id) {
        return users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Volunteer not found"));
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return Role.VOLUNTEER;
        }
        return Role.valueOf(role.trim().toUpperCase());
    }
}
