package com.example.user_service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.user_service.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByRole(String role);

    User findByEmail(String username);

    boolean existsByUsername(String username);

    User findByUsername(String username);

    List<User> findAllById(Iterable<Long> ids);

}
