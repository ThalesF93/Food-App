package com.thales.repository;

import com.thales.authorization.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByRole(String roles); // "ROLE_USER", "ROLE_ADMIN"
    List<Roles> findByRoleIn(List<String> roles);

//    List<Roles> findRoleByUsername(String username);
}
