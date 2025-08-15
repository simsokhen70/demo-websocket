package org.example.demows.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.ApiResponse;
import org.example.demows.dto.CreateNotificationRequest;
import org.example.demows.dto.NotificationDto;
import org.example.demows.entity.User;
import org.example.demows.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification Real time update management APIs")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUserNotifications(@AuthenticationPrincipal User user){
        List<NotificationDto> notifications = notificationService.getUserNotifications(user.getUsername());
        return ResponseEntity.ok(ApiResponse.<List<NotificationDto>>builder()
                .success(true)
                .message("Notifications retrieved successfully")
                .data(notifications)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationDto>> createNotification(@AuthenticationPrincipal User user,@Valid @RequestBody CreateNotificationRequest request){
        NotificationDto notification = notificationService
                .createNotification(user.getUsername(), request);

        return ResponseEntity.ok(ApiResponse.<NotificationDto>builder()
                .success(true)
                .message("Notification created successfully")
                .data(notification)
                .timestamp(LocalDateTime.now())
                .build());
    }

    // Mark as read
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markNotificationAsRead(@PathVariable Long id ,@AuthenticationPrincipal User user){
        NotificationDto notification = notificationService
                .markNotificationAsRead(id, user.getUsername());

        return ResponseEntity.ok(ApiResponse.<NotificationDto>builder()
                .success(true)
                .message("Notification marked as read")
                .data(notification)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
