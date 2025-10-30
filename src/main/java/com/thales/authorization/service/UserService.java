package com.thales.authorization.service;

import com.thales.authorization.dto.RegisterDTO;
import com.thales.authorization.entities.Roles;
import com.thales.authorization.entities.User;
import com.thales.repository.RolesRepository;
import com.thales.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder encoder;



    @Transactional


    public void registerUser(RegisterDTO dto) {

        // normaliza nomes (trim, remove duplicatas mantendo ordem)
        var distinctNames = new LinkedHashSet<String>();
        for (String name : dto.roles()) {
            if (name != null) {
                String n = name.trim();
                if (!n.isEmpty()) distinctNames.add(n);
            }
        }

        // para cada nome: busca; se não existir, cria e salva
        List<Roles> roleEntities = new ArrayList<>();
        for (String name : distinctNames) {
            Roles role = rolesRepository.findByRole(name)
                    .orElseGet(() -> {
                        Roles r = new Roles();
                        r.setRole(name); // coluna "roles" na sua tabela
                        return rolesRepository.save(r);
                    });
            roleEntities.add(role);
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(encoder.encode(dto.password()));

        var roles = rolesRepository.findByRoleIn(dto.roles());
        user.setRoles(new ArrayList<>(roles));


        userRepository.save(user);
    }
}
