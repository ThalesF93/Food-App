package com.thales.repository;

import com.thales.authorization.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Evita N+1: carrega usuário já com roles
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);
}
