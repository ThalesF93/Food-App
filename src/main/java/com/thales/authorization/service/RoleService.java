package com.thales.authorization.service;

import com.thales.authorization.entities.Roles;
import com.thales.repository.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RolesRepository rolesRepository;

    public void saveRole(Roles role){
        rolesRepository.save(role);
    }
}
