package org.example.demows.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String username;
    private String title;
    private String message;
    private String type;
    private String priority;
    private boolean isRead;
    private String createdAt;
    private String readAt;
}
