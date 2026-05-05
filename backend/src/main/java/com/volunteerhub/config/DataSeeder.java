package com.volunteerhub.config;

import com.volunteerhub.model.ApplicationStatus;
import com.volunteerhub.model.Badge;
import com.volunteerhub.model.Event;
import com.volunteerhub.model.Role;
import com.volunteerhub.model.Shift;
import com.volunteerhub.model.TaskApplication;
import com.volunteerhub.model.User;
import com.volunteerhub.model.VolunteerTask;
import com.volunteerhub.repository.BadgeRepository;
import com.volunteerhub.repository.EventRepository;
import com.volunteerhub.repository.ShiftRepository;
import com.volunteerhub.repository.TaskApplicationRepository;
import com.volunteerhub.repository.UserRepository;
import com.volunteerhub.repository.VolunteerTaskRepository;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seed(
            UserRepository users,
            EventRepository events,
            VolunteerTaskRepository tasks,
            ShiftRepository shifts,
            TaskApplicationRepository applications,
            BadgeRepository badges) {
        return args -> {
            if (users.count() > 0) {
                return;
            }

            Badge first = badges.save(new Badge("First Shift", "Complete your first verified shift", 1, 50));
            badges.save(new Badge("Community Builder", "Contribute at least 10 verified hours", 10, 150));
            badges.save(new Badge("Impact Champion", "Contribute at least 25 verified hours", 25, 350));

            User organizer = new User("Asha Organizer", "asha@ngo.local", Role.ORGANIZER);
            organizer.setLocation("Chennai");
            users.save(organizer);

            User ravi = new User("Ravi Volunteer", "ravi@ngo.local", Role.VOLUNTEER);
            ravi.setLocation("Chennai");
            ravi.setAvailability("Weekends and evenings");
            ravi.setSkills(Set.of("first-aid", "logistics", "crowd-control", "translation"));
            ravi.getBadgeIds().add(first.getId());
            users.save(ravi);

            User meera = new User("Meera Volunteer", "meera@ngo.local", Role.VOLUNTEER);
            meera.setLocation("Bengaluru");
            meera.setAvailability("Weekdays");
            meera.setSkills(Set.of("registration", "data-entry", "teaching", "child-care"));
            users.save(meera);

            Event flood = events.save(new Event(
                    "Flood Relief Supply Drive",
                    "Disaster Relief",
                    "Chennai",
                    "Coordinate donated supplies, field teams, and volunteer support.",
                    13.0827,
                    80.2707,
                    "RELIEF2026"));

            Event school = events.save(new Event(
                    "Weekend Learning Camp",
                    "Education",
                    "Bengaluru",
                    "Run learning sessions and student registration for underserved children.",
                    12.9716,
                    77.5946,
                    "LEARN2026"));

            VolunteerTask kits = tasks.save(new VolunteerTask(
                    flood.getId(),
                    "Relief Kit Packing",
                    "Pack food, water, and medical kits for distribution vans.",
                    "Logistics",
                    "Chennai",
                    "Disaster Relief",
                    Set.of("logistics", "first-aid"),
                    12));

            VolunteerTask desk = tasks.save(new VolunteerTask(
                    flood.getId(),
                    "Volunteer Desk Registration",
                    "Register incoming volunteers and route them to teams.",
                    "Operations",
                    "Chennai",
                    "Disaster Relief",
                    Set.of("registration", "data-entry", "communication"),
                    6));

            VolunteerTask tutoring = tasks.save(new VolunteerTask(
                    school.getId(),
                    "Student Learning Mentor",
                    "Support small student groups with activities and reading practice.",
                    "Education",
                    "Bengaluru",
                    "Education",
                    Set.of("teaching", "child-care"),
                    10));

            Shift morning = shifts.save(new Shift(kits.getId(), tomorrowAt(9), tomorrowAt(13), 8));
            shifts.save(new Shift(kits.getId(), tomorrowAt(14), tomorrowAt(18), 8));
            shifts.save(new Shift(desk.getId(), tomorrowAt(10), tomorrowAt(15), 5));
            shifts.save(new Shift(tutoring.getId(), LocalDateTime.now().plusDays(2).withHour(9).withMinute(0), LocalDateTime.now().plusDays(2).withHour(12).withMinute(0), 6));

            TaskApplication approved = new TaskApplication(ravi.getId(), morning.getId());
            approved.setStatus(ApplicationStatus.APPROVED);
            approved.setTeam("Packing Team A");
            applications.save(approved);
        };
    }

    private LocalDateTime tomorrowAt(int hour) {
        return LocalDateTime.now().plusDays(1).withHour(hour).withMinute(0).withSecond(0).withNano(0);
    }
}
