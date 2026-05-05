package com.volunteerhub.repository;

import com.volunteerhub.model.ApplicationStatus;
import com.volunteerhub.model.TaskApplication;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskApplicationRepository extends MongoRepository<TaskApplication, String> {
    List<TaskApplication> findByVolunteerId(String volunteerId);

    List<TaskApplication> findByShiftId(String shiftId);

    List<TaskApplication> findByStatus(ApplicationStatus status);

    List<TaskApplication> findByVolunteerIdAndStatusIn(String volunteerId, Collection<ApplicationStatus> statuses);

    Optional<TaskApplication> findByVolunteerIdAndShiftId(String volunteerId, String shiftId);
}
