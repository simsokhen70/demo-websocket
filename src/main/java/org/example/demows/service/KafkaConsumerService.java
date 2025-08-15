package org.example.demows.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.ExchangeRateDto;
import org.example.demows.dto.NotificationDto;
import org.example.demows.dto.PromotionDto;
import org.example.demows.dto.WebSocketMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer service for handling real-time updates
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "exchange-rates", groupId = "demo-ws-group")
    public void consumeExchangeRateUpdates(String message) {
        try {
            WebSocketMessage<ExchangeRateDto> webSocketMessage = objectMapper.readValue(message,
                    objectMapper.getTypeFactory().constructParametricType(WebSocketMessage.class, ExchangeRateDto.class));

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
                    .readValue(message, new TypeReference<WebSocketMessage<NotificationDto>>() {});

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
}
