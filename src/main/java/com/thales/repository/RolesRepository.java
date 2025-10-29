package com.thales.repository;

import com.thales.authorization.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByName(String name); // "ROLE_USER", "ROLE_ADMIN"
}
