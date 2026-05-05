package com.volunteerhub.repository;

import com.volunteerhub.model.Role;
import com.volunteerhub.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByRole(Role role);

    Optional<User> findByEmail(String email);
}
