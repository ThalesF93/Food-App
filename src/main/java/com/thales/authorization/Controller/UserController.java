package com.thales.authorization.Controller;

import com.thales.authorization.config.TokenConfig;
import com.thales.authorization.dto.RegisterDTO;
import com.thales.authorization.entities.Roles;
import com.thales.authorization.entities.User;
import com.thales.authorization.login.LoginRequest;
import com.thales.authorization.login.LoginResponse;
import com.thales.authorization.service.RoleService;
import com.thales.authorization.service.UserService;
import com.thales.repository.RolesRepository;
import com.thales.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final TokenConfig tokenConfig;
    private final RolesRepository rolesRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterDTO dto){
            userService.registerUser(dto);

    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){

        UsernamePasswordAuthenticationToken userLog = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication authentication = authenticationManager.authenticate(userLog);
        User userLogged = (User) authentication.getPrincipal();

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(request.password());

        var user1 = userRepository.findByUsername(user.getUsername());

        if (!user1.isPresent()){
           throw new UsernameNotFoundException("User not found");
        }

        var role = user1.get().getRoles();
        user.setRoles(role);

        var token = tokenConfig.generateToken(userLogged);
        System.out.println(token);
        return ResponseEntity.ok(new LoginResponse(token));

    }
}
