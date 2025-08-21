package org.example.demows.service;

import java.util.List;

import org.example.demows.dto.ChatMessageDto;
import org.example.demows.dto.ExchangeRateDto;
import org.example.demows.dto.NotificationDto;
import org.example.demows.dto.PromotionDto;
import org.example.demows.dto.WebSocketMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka consumer service for handling real-time updates
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    // @KafkaListener(topics = "exchange-rates", groupId = "demo-ws-group")
    // public void consumeExchangeRateUpdates(String message) {
    // try {
    // WebSocketMessage<ExchangeRateDto> webSocketMessage =
    // objectMapper.readValue(message,
    // objectMapper.getTypeFactory().constructParametricType(WebSocketMessage.class,
    // ExchangeRateDto.class));

    // log.debug("Received exchange rate update from Kafka: {}", message);

    // // Broadcast to all WebSocket subscribers
    // messagingTemplate.convertAndSend("/topic/exchange-rates", webSocketMessage);

    // } catch (JsonProcessingException e) {
    // log.error("Error processing exchange rate message from Kafka", e);
    // }
    // }

    @KafkaListener(topics = "exchange-rates", groupId = "demo-ws-group")
    public void consumeExchangeRateUpdates(String message) {
        try {
            WebSocketMessage<List<ExchangeRateDto>> webSocketMessage = objectMapper.readValue(
                    message,
                    objectMapper.getTypeFactory().constructParametricType(
                            WebSocketMessage.class,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, ExchangeRateDto.class)));

            log.debug("Received exchange rate update from Kafka: {}", message);

            // Broadcast to all WebSocket subscribers
            messagingTemplate.convertAndSend("/topic/exchange-rates", webSocketMessage);

        } catch (JsonProcessingException e) {
            log.error("Error processing exchange rate message from Kafka", e);
        }
    }

    @KafkaListener(topics = "promotions", groupId = "demo-ws-group")
    public void consumePromotionUpdates(String message) {
        try {
            WebSocketMessage<PromotionDto> webSocketMessage = objectMapper.readValue(message,
                    objectMapper.getTypeFactory().constructParametricType(WebSocketMessage.class, PromotionDto.class));

            log.debug("Received promotion update from Kafka: {}", message);

            // Note: For promotions, we typically send to specific users
            // This is handled in the PromotionService directly
            // Here we just log the message for monitoring purposes

        } catch (JsonProcessingException e) {
            log.error("Error processing promotion message from Kafka", e);
        }
    }

    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void handleNotificationMessage(String message) {
        try {
            log.debug("Received notification message: {}", message);

            WebSocketMessage<NotificationDto> notificationMessage = objectMapper
                    .readValue(message, new TypeReference<WebSocketMessage<NotificationDto>>() {
                    });

            // Extract username from notification data
            NotificationDto notification = notificationMessage.getData();
            String username = notification.getUsername(); // You'll need to add this to DTO

            // Send to specific user's WebSocket queue
            messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notificationMessage);

            log.debug("Forwarded notification to user {}: {}", username, message);
        } catch (Exception e) {
            log.error("Error processing notification message", e);
        }
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void handleChatMessage(String message) {
        try {
            log.debug("Received chat message from Kafka: {}", message);

            // Note: ChatServiceImpl already sends messages directly to WebSocket
            // This consumer is mainly for monitoring and future features
            // We don't need to forward the message again to avoid duplicates

            WebSocketMessage<ChatMessageDto> chatMessage = objectMapper
                    .readValue(message, new TypeReference<WebSocketMessage<ChatMessageDto>>() {
                    });

            ChatMessageDto chatData = chatMessage.getData();

            log.debug("Chat message processed from Kafka: {} -> {}",
                    chatData.getSenderUsername(), chatData.getReceiverUsername());

            // No need to send to WebSocket again - ChatServiceImpl handles this
            // This prevents duplicate messages

        } catch (Exception e) {
            log.error("Error processing chat message from Kafka", e);
        }
    }

    @KafkaListener(topics = "postgres.dbserver1.public.promotions", groupId = "ws-group")
    public void consumePromotion(String message) {
        log.info("Received from Debezium: {}", message);
        // messagingTemplate.convertAndSend("/topic/promotions", message);
    }
}
