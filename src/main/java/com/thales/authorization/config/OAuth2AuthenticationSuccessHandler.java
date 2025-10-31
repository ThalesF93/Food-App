package com.thales.authorization.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thales.authorization.entities.Roles;
import com.thales.authorization.entities.User;
import com.thales.repository.RolesRepository;
import com.thales.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenConfig tokenConfig;
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder encoder; ;


    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {


        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = auth.getPrincipal();
        Map<String, Object> attrs = principal.getAttributes();

        String provider = auth.getAuthorizedClientRegistrationId(); // "google", "github", "facebook"
        String providerIdKey = provider.equals("google") ? "sub" : "id";
        String providerId = String.valueOf(attrs.getOrDefault(providerIdKey, UUID.randomUUID().toString()));

        // gera username único e estável
        String rawName = (String) attrs.get("name");
        String usernameBase = (rawName != null && !rawName.isBlank())
                ? rawName.trim().replaceAll("\\s+", "_").toLowerCase()
                : (provider + "_" + providerId);

        // cria ou busca o usuário local
        User user = userRepository.findByUsername(usernameBase).orElseGet(() -> {
            User u = new User();
            u.setUsername(usernameBase);
            u.setPassword(encoder.encode(UUID.randomUUID().toString())); // senha aleatória
            return u;
        });

        // garante o papel ROLE_USER
        Roles defaultRole = rolesRepository.findByRole("USER")
                .orElseGet(() -> {
                    Roles r = new Roles();
                    r.setRole("USER");
                    return rolesRepository.save(r);
                });

        if (user.getRoles().stream().noneMatch(r -> r.getRole().equals(defaultRole.getRole()))) {
            user.getRoles().add(defaultRole);
        }

        user = userRepository.save(user);

        // gera o token passando o próprio objeto User
        String token = tokenConfig.generateToken(user);

        // resposta HTTP
        response.setHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(mapper.writeValueAsString(
                Map.of(
                        "access_token", token,
                        "token_type", "Bearer",
                        "user", Map.of("username", user.getUsername())
                )
        ));
    }
}
