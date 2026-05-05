package com.volunteerhub.repository;

import com.volunteerhub.model.Feedback;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findByApplicationId(String applicationId);
}
