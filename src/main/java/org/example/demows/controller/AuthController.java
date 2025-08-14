package org.example.demows.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.AuthResponse;
import org.example.demows.dto.UserLoginRequest;
import org.example.demows.dto.UserRegistrationRequest;
import org.example.demows.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns authentication token")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Registration request received for username: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        log.info("Login request received for username: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
