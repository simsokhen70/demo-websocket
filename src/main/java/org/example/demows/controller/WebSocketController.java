package org.example.demows.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.*;
import org.example.demows.entity.User;
import org.example.demows.service.ExchangeRateService;
import org.example.demows.service.NotificationService;
import org.example.demows.service.PromotionService;
import org.example.demows.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * WebSocket controller for handling real-time messaging
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final ExchangeRateService exchangeRateService;
    private final PromotionService promotionService;
    private final NotificationService notificationService;
    private final ChatService chatService;

    @MessageMapping("/exchange-rates/subscribe")
    @SendTo("/topic/exchange-rates")
    public WebSocketMessage<List<ExchangeRateDto>> subscribeToExchangeRates(SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "anonymous";
        log.info("User {} subscribed to exchange rates", username);
        
        List<ExchangeRateDto> exchangeRates = exchangeRateService.getAllExchangeRates();
        
        return WebSocketMessage.<List<ExchangeRateDto>>builder()
                .type("EXCHANGE_RATES_INITIAL")
                .data(exchangeRates)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @MessageMapping("/promotions/subscribe")
    @SendToUser("/queue/promotions")
    public WebSocketMessage<?> subscribeToPromotions(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Promotion subscription request received [SessionId: {}]", sessionId);
        
        try {
            String username = "demo";
            if (headerAccessor.getUser() != null) {
                username = headerAccessor.getUser().getName();
                log.info("User {} subscribed to promotions [SessionId: {}]", username, sessionId);
            } else {
                log.warn("Anonymous user attempted to subscribe to promotions [SessionId: {}]", sessionId);
                // Send error response for anonymous users
                WebSocketErrorResponse errorResponse = WebSocketErrorResponse.authenticationError(sessionId);
                return WebSocketMessage.builder()
                        .type("ERROR")
                        .data(errorResponse)
                        .timestamp(LocalDateTime.now().toString())
                        .build();
            }
            
            List<PromotionDto> promotions = promotionService.getUserPromotions(username);
            log.info("Found {} promotions for user {} [SessionId: {}]", promotions.size(), username, sessionId);
            
            WebSocketMessage<List<PromotionDto>> response = WebSocketMessage.<List<PromotionDto>>builder()
                    .type("PROMOTIONS_INITIAL")
                    .data(promotions)
                    .timestamp(LocalDateTime.now().toString())
                    .build();
            
            log.info("Sending promotions response for user {} [SessionId: {}]", username, sessionId);
            return response;
            
        } catch (Exception e) {
            log.error("Error processing promotion subscription [SessionId: {}]: {}", sessionId, e.getMessage(), e);
            WebSocketErrorResponse errorResponse = WebSocketErrorResponse.subscriptionError(
                sessionId, 
                headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "anonymous",
                "Failed to load promotions: " + e.getMessage()
            );
            return WebSocketMessage.builder()
                    .type("ERROR")
                    .data(errorResponse)
                    .timestamp(LocalDateTime.now().toString())
                    .build();
        }
    }

    @MessageMapping("/exchange-rates/request")
    @SendTo("/topic/exchange-rates")
    public WebSocketMessage<ExchangeRateDto> requestExchangeRate(String currencyPair) {
        log.info("Exchange rate request received for: {}", currencyPair);
        
        try {
            String[] currencies = currencyPair.split("-");
            if (currencies.length == 2) {
                ExchangeRateDto exchangeRate = exchangeRateService.getExchangeRate(currencies[0], currencies[1]);
                
                return WebSocketMessage.<ExchangeRateDto>builder()
                        .type("EXCHANGE_RATE_REQUEST")
                        .data(exchangeRate)
                        .timestamp(LocalDateTime.now().toString())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error processing exchange rate request for: {}", currencyPair, e);
        }
        
        return WebSocketMessage.<ExchangeRateDto>builder()
                .type("EXCHANGE_RATE_ERROR")
                .data(null)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @MessageMapping("/notifications/subscribe")
    @SendToUser("/queue/notifications")
    public WebSocketMessage<?> subscribeToNotifications(
            SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Promotion subscription request received [SessionId: {}]", sessionId);

        String username = "anonymous";
        try {
            if (headerAccessor.getUser() != null) {
                username = headerAccessor.getUser().getName();
                log.info("User {} subscribed to notification [SessionId: {}]", username, sessionId);
            } else {
                log.warn("Anonymous user attempted to subscribe to notification [SessionId: {}]", sessionId);
                // Send error response for anonymous users
                WebSocketErrorResponse errorResponse = WebSocketErrorResponse.authenticationError(sessionId);
                return WebSocketMessage.builder()
                        .type("ERROR")
                        .data(errorResponse)
                        .timestamp(LocalDateTime.now().toString())
                        .build();
            }

            List<NotificationDto> notifications = notificationService
                    .getUserNotifications(username);

            return WebSocketMessage.builder()
                    .type("NOTIFICATIONS_INITIAL")
                    .data(notifications)
                    .timestamp(LocalDateTime.now().toString())
                    .build();
        } catch (Exception e) {
            log.error("Error subscribing to notifications for user {}", username, e);

            return WebSocketMessage.builder()
                    .type("ERROR")
                    .data(WebSocketErrorResponse.builder()
                            .traceId(UUID.randomUUID().toString())
                            .type("NOTIFICATION_SUBSCRIPTION_ERROR")
                            .error("Subscription failed")
                            .message("Failed to subscribe to notifications")
                            .suggestion("Please try again or contact support")
                            .timestamp(LocalDateTime.now())
                            .sessionId("N/A")
                            .username(username)
                            .build())
                    .timestamp(LocalDateTime.now().toString())
                    .build();
        }
    }

    @MessageMapping("/chat/subscribe")
    @SendToUser("/queue/chat")
    public WebSocketMessage<?> subscribeToChat(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Chat subscription request received [SessionId: {}]", sessionId);

        String username = "anonymous";
        try {
            if (headerAccessor.getUser() != null) {
                username = headerAccessor.getUser().getName();
                log.info("User {} subscribed to chat [SessionId: {}]", username, sessionId);
            } else {
                log.warn("Anonymous user attempted to subscribe to chat [SessionId: {}]", sessionId);
                WebSocketErrorResponse errorResponse = WebSocketErrorResponse.authenticationError(sessionId);
                return WebSocketMessage.builder()
                        .type("ERROR")
                        .data(errorResponse)
                        .timestamp(LocalDateTime.now().toString())
                        .build();
            }

            List<ChatMessageDto> conversations = chatService.getUserChats(username);

            return WebSocketMessage.builder()
                    .type("CHAT_CONVERSATIONS_INITIAL")
                    .data(conversations)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

        } catch (Exception e) {
            log.error("Error subscribing to chat for user {} [SessionId: {}]: {}", username, sessionId, e.getMessage(), e);
            WebSocketErrorResponse errorResponse = WebSocketErrorResponse.subscriptionError(
                sessionId, 
                username,
                "Failed to load chat conversations: " + e.getMessage()
            );
            return WebSocketMessage.builder()
                    .type("ERROR")
                    .data(errorResponse)
                    .timestamp(LocalDateTime.now().toString())
                    .build();
        }
    }

    @MessageMapping("/chat/send")
    @SendToUser("/queue/chat")
    public WebSocketMessage<?> sendMessage(ChatRequestDto request, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Chat message request received [SessionId: {}]", sessionId);

        String username = "anonymous";
        try {
            if (headerAccessor.getUser() != null) {
                username = headerAccessor.getUser().getName();
                log.info("User {} sending message to {} [SessionId: {}]", username, request.getReceiverUsername(), sessionId);
            } else {
                log.warn("Anonymous user attempted to send message [SessionId: {}]", sessionId);
                WebSocketErrorResponse errorResponse = WebSocketErrorResponse.authenticationError(sessionId);
                return WebSocketMessage.builder()
                        .type("ERROR")
                        .data(errorResponse)
                        .timestamp(LocalDateTime.now().toString())
                        .build();
            }

            ChatMessageDto sentMessage = chatService.sendMessage(username, request);

            return WebSocketMessage.builder()
                    .type("CHAT_MESSAGE_SENT")
                    .data(sentMessage)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

        } catch (Exception e) {
            log.error("Error sending chat message for user {} [SessionId: {}]: {}", username, sessionId, e.getMessage(), e);
            WebSocketErrorResponse errorResponse = WebSocketErrorResponse.subscriptionError(
                sessionId, 
                username,
                "Failed to send message: " + e.getMessage()
            );
            return WebSocketMessage.builder()
                    .type("ERROR")
                    .data(errorResponse)
                    .timestamp(LocalDateTime.now().toString())
                    .build();
        }
    }

    @MessageMapping("/chat/conversation/{otherUsername}")
    @SendToUser("/queue/chat")
    public WebSocketMessage<?> getConversation(String otherUsername, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Chat conversation request received for {} [SessionId: {}]", otherUsername, sessionId);

        String username = "anonymous";
        try {
            if (headerAccessor.getUser() != null) {
                username = headerAccessor.getUser().getName();
                log.info("User {} requesting conversation with {} [SessionId: {}]", username, otherUsername, sessionId);
            } else {
                log.warn("Anonymous user attempted to get conversation [SessionId: {}]", sessionId);
                WebSocketErrorResponse errorResponse = WebSocketErrorResponse.authenticationError(sessionId);
                return WebSocketMessage.builder()
                        .type("ERROR")
                        .data(errorResponse)
                        .timestamp(LocalDateTime.now().toString())
                        .build();
            }

            List<ChatMessageDto> conversation = chatService.getConversation(username, otherUsername);

            return WebSocketMessage.builder()
                    .type("CHAT_CONVERSATION_LOADED")
                    .data(conversation)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

        } catch (Exception e) {
            log.error("Error loading conversation for user {} with {} [SessionId: {}]: {}", username, otherUsername, sessionId, e.getMessage(), e);
            WebSocketErrorResponse errorResponse = WebSocketErrorResponse.subscriptionError(
                sessionId, 
                username,
                "Failed to load conversation: " + e.getMessage()
            );
            return WebSocketMessage.builder()
                    .type("ERROR")
                    .data(errorResponse)
                    .timestamp(LocalDateTime.now().toString())
                    .build();
        }
    }
    /*public WebSocketMessage<?> subscribeToNotifications(
            @AuthenticationPrincipal User user) {
        log.info("User {} subscribed to notifications", user.getUsername());

        try {
            List<NotificationDto> notifications = notificationService
                    .getUserNotifications(user.getUsername());

            return WebSocketMessage.builder()
                    .type("NOTIFICATIONS_INITIAL")
                    .data(notifications)
                    .timestamp(LocalDateTime.now().toString())
                    .build();
        } catch (Exception e) {
            log.error("Error subscribing to notifications for user {}", user.getUsername(), e);

            return WebSocketMessage.builder()
                    .type("ERROR")
                    .data(WebSocketErrorResponse.builder()
                            .traceId(UUID.randomUUID().toString())
                            .type("NOTIFICATION_SUBSCRIPTION_ERROR")
                            .error("Subscription failed")
                            .message("Failed to subscribe to notifications")
                            .suggestion("Please try again or contact support")
                            .timestamp(LocalDateTime.now())
                            .sessionId("N/A")
                            .username(user.getUsername())
                            .build())
                    .timestamp(LocalDateTime.now().toString())
                    .build();
        }
    }*/
}
