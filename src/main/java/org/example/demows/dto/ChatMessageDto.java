package org.example.demows.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private String senderUsername;
    private String receiverUsername;
    private String message;
    private String messageType;
    private String timestamp;
    private boolean isRead;
    private String readAt;
    private boolean isDeleted;
}
