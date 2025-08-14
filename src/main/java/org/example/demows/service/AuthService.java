package org.example.demows.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.AuthResponse;
import org.example.demows.dto.UserLoginRequest;
import org.example.demows.dto.UserProfileDto;
import org.example.demows.dto.UserRegistrationRequest;
import org.example.demows.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service class for authentication operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public AuthResponse login(UserLoginRequest loginRequest) {
        log.info("User login attempt for username: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserProfileDto userProfile = userService.getUserProfile(loginRequest.getUsername());

        log.info("User login successful for username: {}", loginRequest.getUsername());

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .user(userProfile)
                .build();
    }

    public AuthResponse register(UserRegistrationRequest registrationRequest) {
        log.info("User registration attempt for username: {}", registrationRequest.getUsername());

        UserProfileDto userProfile = userService.registerUser(registrationRequest);

        // Generate token for newly registered user
        String jwt = tokenProvider.generateTokenFromUsername(registrationRequest.getUsername());

        log.info("User registration successful for username: {}", registrationRequest.getUsername());

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .user(userProfile)
                .build();
    }
}
