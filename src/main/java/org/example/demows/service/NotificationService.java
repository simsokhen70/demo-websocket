package org.example.demows.service;


import org.example.demows.dto.CreateNotificationRequest;
import org.example.demows.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getUserNotifications(String username);
    NotificationDto createNotification(String username, CreateNotificationRequest request);
    NotificationDto markNotificationAsRead(Long notificationId, String username);
}
