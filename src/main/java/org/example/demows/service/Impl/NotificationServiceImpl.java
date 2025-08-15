package org.example.demows.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.demows.dto.CreateNotificationRequest;
import org.example.demows.dto.NotificationDto;
import org.example.demows.dto.WebSocketMessage;
import org.example.demows.entity.Notification;
import org.example.demows.entity.User;
import org.example.demows.exception.ResourceNotFoundException;
import org.example.demows.repository.NotificationRepository;
import org.example.demows.service.NotificationService;
import org.example.demows.service.UserService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final UserService userService;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;


    // get user notification
    @Override
    public List<NotificationDto> getUserNotifications(String username){
        log.info("Fetching notifications for user: {}", username);
        User user =  (User) userService.loadUserByUsername(username);
        List<Notification> notifications = notificationRepository.findAllNotificationsForUser(user.getId());

        return notifications.stream()
                .map(this::mapToDto)
                .toList();
    }

    // Create new notification
    @Override
    public NotificationDto createNotification(String username, CreateNotificationRequest request){
        User user = (User) userService.loadUserByUsername(username);
        Notification notification = Notification.builder()
                .username(username)
                .user(user)
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .priority(request.getPriority())
                .isActive(true)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        Notification savedNotification = notificationRepository.save(notification);
        NotificationDto dto = mapToDto(savedNotification);

        // Publish real-time update
        publishNotificationUpdate(dto, username);

        return dto;
    }

    // Mark notification as read
    @Override
    public NotificationDto markNotificationAsRead(Long notificationId, String username){
        log.info("Marking notification {} as read for user: {}", notificationId, username);
        User user =  (User) userService.loadUserByUsername(username);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        // Verify notification belongs to user
        if(!notification.getUser().getId().equals(user.getId())){
            throw new ResourceNotFoundException("Notification not found");
        }
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);
        NotificationDto dto = mapToDto(savedNotification);
        // Publish update
        publishNotificationUpdate(dto, username);

        return dto;
    }

    // Publish to Kafka and WebSocket
    public void publishNotificationUpdate(NotificationDto notificationDto, String username){
        try{
            WebSocketMessage<NotificationDto> message = WebSocketMessage.<NotificationDto>builder()
                    .type("NOTIFICATION_UPDATE")
                    .data(notificationDto)
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            String messageJson = objectMapper.writeValueAsString(message);
            log.debug("Notification update message: {}", messageJson);

            // Send to Kafka topics
            kafkaTemplate.send("notifications", messageJson);

            // Send to user's WebSocket queue
            // Used to send WebSocket messages to clients — typically via STOMP protocol.
            // Sends real-time updates to all clients subscribed to /topic/notifications.
            messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);

        } catch (Exception e){
            log.error("Error publishing notification update", e);
        }
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .username(notification.getUsername())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .priority(notification.getPriority())
                .isRead(notification.getIsRead())
                .createdAt(String.valueOf(notification.getCreatedAt()))
                .readAt(String.valueOf(notification.getReadAt()))
                .build();
    }

    // Helper method to create system notifications

/*    public void createSystemNotification(String username, String title, String message) {
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .title(title)
                .message(message)
                .type("SYSTEM")
                .priority("NORMAL")
                .build();

        createNotification(username, request);
    }*/
}
