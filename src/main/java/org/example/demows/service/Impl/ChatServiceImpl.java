package org.example.demows.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.ChatMessageDto;
import org.example.demows.dto.ChatRequestDto;
import org.example.demows.dto.WebSocketMessage;
import org.example.demows.entity.ChatMessage;
import org.example.demows.exception.ResourceNotFoundException;
import org.example.demows.repository.ChatMessageRepository;
import org.example.demows.service.ChatService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    
    private final ChatMessageRepository chatMessageRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String CHAT_TOPIC = "chat-messages";
    private final Random random = new Random();

    @Override
    public List<ChatMessageDto> getUserChats(String username) {
        log.info("Fetching chat conversations for user: {}", username);
        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessagesForUser(username);
        return lastMessages.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<ChatMessageDto> getConversation(String username, String otherUsername) {
        log.info("Fetching conversation between {} and {}", username, otherUsername);
        List<ChatMessage> messages = chatMessageRepository.findConversationBetweenUsers(username, otherUsername);
        return messages.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public ChatMessageDto sendMessage(String senderUsername, ChatRequestDto request) {
        log.info("Sending message from {} to {}: {}", senderUsername, request.getReceiverUsername(), request.getMessage());
        
        ChatMessage chatMessage = ChatMessage.builder()
                .senderUsername(senderUsername)
                .receiverUsername(request.getReceiverUsername())
                .message(request.getMessage())
                .messageType(ChatMessage.MessageType.TEXT)
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .isDeleted(false)
                .build();
        
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        ChatMessageDto dto = mapToDto(savedMessage);
        
        // Publish real-time update
        publishChatMessage(dto);
        
        return dto;
    }

    @Override
    public ChatMessageDto markMessageAsRead(Long messageId, String username) {
        log.info("Marking message {} as read by user: {}", messageId, username);
        
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMessage", "id", messageId));
        
        // Verify the user is the receiver of this message
        if (!message.getReceiverUsername().equals(username)) {
            throw new ResourceNotFoundException("ChatMessage", "id", messageId);
        }
        
        message.setRead(true);
        message.setReadAt(LocalDateTime.now());
        
        ChatMessage updatedMessage = chatMessageRepository.save(message);
        ChatMessageDto dto = mapToDto(updatedMessage);
        
        // Publish update
        publishChatMessage(dto);
        
        return dto;
    }

    @Override
    public List<ChatMessageDto> markConversationAsRead(String username, String otherUsername) {
        log.info("Marking conversation between {} and {} as read", username, otherUsername);
        
        List<ChatMessage> unreadMessages = chatMessageRepository.findUnreadMessagesByReceiverUsername(username)
                .stream()
                .filter(msg -> msg.getSenderUsername().equals(otherUsername))
                .toList();
        
        for (ChatMessage message : unreadMessages) {
            message.setRead(true);
            message.setReadAt(LocalDateTime.now());
        }
        
        List<ChatMessage> updatedMessages = chatMessageRepository.saveAll(unreadMessages);
        
        // Publish updates
        updatedMessages.forEach(msg -> publishChatMessage(mapToDto(msg)));
        
        return updatedMessages.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<String> getChatPartners(String username) {
        log.info("Fetching chat partners for user: {}", username);
        return chatMessageRepository.findChatPartnersForUser(username);
    }

    @Override
    public long getUnreadMessageCount(String username, String otherUsername) {
        return chatMessageRepository.countUnreadMessagesInConversation(otherUsername, username);
    }

    private void publishChatMessage(ChatMessageDto chatMessageDto) {
        try {
            WebSocketMessage<ChatMessageDto> message = WebSocketMessage.<ChatMessageDto>builder()
                    .type("CHAT_MESSAGE")
                    .data(chatMessageDto)
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            
            String messageJson = objectMapper.writeValueAsString(message);
            
            // Send to Kafka for logging/monitoring
            kafkaTemplate.send(CHAT_TOPIC, messageJson);
            
            // Only send to receiver's WebSocket queue (new message)
            // Sender will see the message when conversation is refreshed
            messagingTemplate.convertAndSendToUser(
                chatMessageDto.getReceiverUsername(), 
                "/queue/chat", 
                message
            );
            
            log.debug("Published chat message to receiver: {}", chatMessageDto.getReceiverUsername());
        } catch (JsonProcessingException e) {
            log.error("Error serializing chat message", e);
        }
    }

    private ChatMessageDto mapToDto(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .id(chatMessage.getId())
                .senderUsername(chatMessage.getSenderUsername())
                .receiverUsername(chatMessage.getReceiverUsername())
                .message(chatMessage.getMessage())
                .messageType(chatMessage.getMessageType().name())
                .timestamp(chatMessage.getTimestamp().toString())
                .isRead(chatMessage.isRead())
                .readAt(chatMessage.getReadAt() != null ? chatMessage.getReadAt().toString() : null)
                .isDeleted(chatMessage.isDeleted())
                .build();
    }

    @Scheduled(fixedRate = 60000) // Every minute
    public void simulateNewMessages() {
        log.info("Simulating new chat messages for demo");
        
        // For demo purposes, we could create some automated messages
        // This is just a placeholder for future enhancements
        log.debug("Chat message simulation completed");
    }
}
