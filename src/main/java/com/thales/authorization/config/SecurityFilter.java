package com.thales.authorization.config;

import com.thales.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final TokenConfig tokenConfig;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String authorizedHeader = request.getHeader("Authorization");
        if (Strings.isNotEmpty(authorizedHeader) && authorizedHeader.startsWith("Bearer ")) {
            String token = authorizedHeader.substring("Bearer ".length());
            Optional<JwtUserData> optionalJwtUserData = tokenConfig.validateToken(token);
            if (optionalJwtUserData.isPresent()){
                JwtUserData userData = optionalJwtUserData.get();
                userRepository.findById(userData.id()).ifPresent(user -> {
                    UsernamePasswordAuthenticationToken userAuthencation =
                            new UsernamePasswordAuthenticationToken(user, null, userData.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(userAuthencation);
                });
            }
        }
        filterChain.doFilter(request, response);

    }
}
