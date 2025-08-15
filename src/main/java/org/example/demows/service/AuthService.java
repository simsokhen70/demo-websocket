package org.example.demows.service;


import org.example.demows.dto.AuthResponse;
import org.example.demows.dto.UserLoginRequest;
import org.example.demows.dto.UserRegistrationRequest;

public interface AuthService {
    AuthResponse login(UserLoginRequest loginRequest);
    AuthResponse register(UserRegistrationRequest registrationRequest);

}
