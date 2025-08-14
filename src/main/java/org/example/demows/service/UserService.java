package org.example.demows.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.UserProfileDto;
import org.example.demows.dto.UserRegistrationRequest;
import org.example.demows.entity.User;
import org.example.demows.exception.ResourceAlreadyExistsException;
import org.example.demows.exception.ResourceNotFoundException;
import org.example.demows.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for user management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findActiveUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public UserProfileDto registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("User", "username", request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("User", "email", request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return mapToUserProfileDto(savedUser);
    }

    public UserProfileDto getUserProfile(String username) {
        log.info("Getting user profile for username: {}", username);
        User user = userRepository.findActiveUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return mapToUserProfileDto(user);
    }

    public UserProfileDto updateUserProfile(String username, UserProfileDto updateRequest) {
        log.info("Updating user profile for username: {}", username);
        User user = userRepository.findActiveUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getPhone() != null) {
            user.setPhone(updateRequest.getPhone());
        }

        User updatedUser = userRepository.save(user);
        log.info("User profile updated successfully for ID: {}", updatedUser.getId());

        return mapToUserProfileDto(updatedUser);
    }

    private UserProfileDto mapToUserProfileDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
