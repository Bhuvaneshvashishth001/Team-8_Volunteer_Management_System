package com.volunteerhub.repository;

import com.volunteerhub.model.NotificationMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<NotificationMessage, String> {
}
