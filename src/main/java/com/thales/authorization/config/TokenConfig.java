package com.thales.authorization.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.thales.authorization.entities.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class TokenConfig {

    private final String secret = String.valueOf(123);
    Algorithm algorithm = Algorithm.HMAC256(secret);

    public String generateToken(User user){
        return JWT.create()
                .withClaim("roles", user.getRoles())
                .withClaim("id", user.getId())
                .withSubject(user.getUsername())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(86000))
                .sign(algorithm);
    }

    public Optional<JwtUserData> validateToken(String token){
        Algorithm algorithm1 = Algorithm.HMAC256(secret);
        DecodedJWT decode = JWT.require(algorithm1).build().verify(token);

        List<String> roles = decode.getClaim("roles").asList(String.class);
        Long id = decode.getClaim("id").asLong();

        return Optional.of(JwtUserData.builder()
                .id(id)
                .roles(roles)
                .username(decode.getSubject())
                .build());
    }


}
