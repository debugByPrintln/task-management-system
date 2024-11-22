package com.melnikov.taskmanagementsystem.controller;

import com.melnikov.taskmanagementsystem.dto.auth.AuthRequestDTO;
import com.melnikov.taskmanagementsystem.dto.auth.AuthResponseDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateUserDTO;
import com.melnikov.taskmanagementsystem.exception.user.UserNotFoundException;
import com.melnikov.taskmanagementsystem.jwt.JwtTokenProvider;
import com.melnikov.taskmanagementsystem.model.User;
import com.melnikov.taskmanagementsystem.service.CustomUserDetailsService;
import com.melnikov.taskmanagementsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations related to authentication")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final CustomUserDetailsService userDetailsService;

    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                          CustomUserDetailsService userDetailsService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @PostMapping("/signin")
    @Operation(summary = "Sign in", description = "Sign in with email and password. FOR EVERYBODY.")
    public ResponseEntity<AuthResponseDTO> signIn(@Valid @RequestBody AuthRequestDTO request) {
        try {
            log.info("Signing in user with details {}", request);
            return authenticateUser(request);
        }
        catch (AuthenticationException e) {
            log.warn("User not found with details {}", request);
            throw new UserNotFoundException("User not found: " + request.getEmail());
        }
    }

    @PostMapping("/signup")
    @Operation(summary = "Sign up", description = "Register a new user with email, password, and role. FOR EVERYBODY.")
    public ResponseEntity<AuthResponseDTO> signUp(@Valid @RequestBody CreateUserDTO createUserDTO) {
        userService.createUser(createUserDTO);

        AuthRequestDTO requestDTO = new AuthRequestDTO();
        requestDTO.setEmail(createUserDTO.getEmail());
        requestDTO.setPassword(createUserDTO.getPassword());

        try {
            log.info("Signing up user with details {}", createUserDTO);
            return authenticateUser(requestDTO);
        }
        catch (AuthenticationException e) {
            log.warn("Failed to sign up user with details {}", createUserDTO);
            throw new UserNotFoundException("Failed to authenticate user after signing up: " + createUserDTO.getEmail());
        }
    }

    private ResponseEntity<AuthResponseDTO> authenticateUser(AuthRequestDTO request) throws AuthenticationException{
        String email = request.getEmail();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        User user = (User) userDetailsService.loadUserByUsername(email);
        String token = jwtTokenProvider.createToken(email, user.getRole().getName().name());

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);

        return ResponseEntity.ok(response);
    }
}