package com.volunteerhub.repository;

import com.volunteerhub.model.Shift;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShiftRepository extends MongoRepository<Shift, String> {
    List<Shift> findByTaskId(String taskId);
}
