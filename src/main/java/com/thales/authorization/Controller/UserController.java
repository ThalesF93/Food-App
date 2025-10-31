package com.thales.authorization.Controller;

import com.thales.authorization.config.TokenConfig;
import com.thales.authorization.dto.RegisterDTO;
import com.thales.authorization.entities.User;
import com.thales.authorization.login.LoginRequest;
import com.thales.authorization.login.LoginResponse;
import com.thales.authorization.service.UserService;
import com.thales.repository.RolesRepository;
import com.thales.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
@Tag(name = "Users")
public class UserController {

    private final UserService userService;
    private final TokenConfig tokenConfig;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterDTO> register(@RequestBody @Valid RegisterDTO dto){

        userService.registerUser(dto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Login", description = "Get Token Jwt")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request){

        UsernamePasswordAuthenticationToken userLog = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication authentication = authenticationManager.authenticate(userLog);
        User userLogged = (User) authentication.getPrincipal();

//        { minha lógica para obter o token
//            User user = new User();
//            user.setUsername(request.username());
//            user.setPassword(request.password());
//            Optional<User> user1 = userService.findUserByUsername(user);
//            Optional<User> UserId = userService.findUserById(user1);
//            if (UserId.isEmpty()) {
//                throw new UsernameNotFoundException("User not found");
//            }
//            Long id = UserId.get().getId();
//            if (user1.isEmpty()) {
//                throw new UsernameNotFoundException("User not found");
//            }
//            var role = user1.get().getRoles();
//            user.setRoles(role);
//            user.setId(id);
//        }

        var token = tokenConfig.generateToken(userLogged);
        return ResponseEntity.ok(new LoginResponse(token));

    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('GERENTE')")
    public void deleteUser(@RequestParam Long id){
        log.info("Delete user from ID: " + id);
        userService.deleteUser(id);

    }
}
