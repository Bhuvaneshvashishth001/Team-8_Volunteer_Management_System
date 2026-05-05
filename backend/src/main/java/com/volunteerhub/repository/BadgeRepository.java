package com.volunteerhub.repository;

import com.volunteerhub.model.Badge;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BadgeRepository extends MongoRepository<Badge, String> {
}
