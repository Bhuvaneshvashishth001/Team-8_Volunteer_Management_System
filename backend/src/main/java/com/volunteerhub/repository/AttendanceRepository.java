package com.volunteerhub.repository;

import com.volunteerhub.model.Attendance;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {
    Optional<Attendance> findByApplicationId(String applicationId);
}
