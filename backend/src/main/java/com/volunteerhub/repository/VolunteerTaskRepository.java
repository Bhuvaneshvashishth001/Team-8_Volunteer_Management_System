package com.volunteerhub.repository;

import com.volunteerhub.model.VolunteerTask;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VolunteerTaskRepository extends MongoRepository<VolunteerTask, String> {
    List<VolunteerTask> findByEventId(String eventId);
}
