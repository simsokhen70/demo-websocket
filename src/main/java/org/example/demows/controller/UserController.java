package org.example.demows.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.UserProfileDto;
import org.example.demows.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user profile operations
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User profile management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieves the current user's profile information")
    public ResponseEntity<UserProfileDto> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        log.info("Profile request received for user: {}", username);
        UserProfileDto profile = userService.getUserProfile(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Updates the current user's profile information")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileDto updateRequest) {
        String username = authentication.getName();
        log.info("Profile update request received for user: {}", username);
        UserProfileDto updatedProfile = userService.updateUserProfile(username, updateRequest);
        return ResponseEntity.ok(updatedProfile);
    }
}
