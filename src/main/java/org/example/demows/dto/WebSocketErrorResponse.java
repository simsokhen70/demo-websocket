package org.example.demows.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WebSocket error response DTO for real-time error handling
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketErrorResponse {
    private String traceId;
    private String type;
    private String error;
    private String message;
    private String suggestion;
    private LocalDateTime timestamp;
    private String sessionId;
    private String username;
    
    // Static factory methods
    public static WebSocketErrorResponse authenticationError(String sessionId) {
        return WebSocketErrorResponse.builder()
                .traceId(generateTraceId())
                .type("AUTHENTICATION_ERROR")
                .error("Authentication Failed")
                .message("Invalid or missing authentication token")
                .suggestion("Please login again and reconnect")
                .timestamp(LocalDateTime.now())
                .sessionId(sessionId)
                .build();
    }
    
    public static WebSocketErrorResponse subscriptionError(String sessionId, String username, String message) {
        return WebSocketErrorResponse.builder()
                .traceId(generateTraceId())
                .type("SUBSCRIPTION_ERROR")
                .error("Subscription Failed")
                .message(message)
                .suggestion("Please try subscribing again")
                .timestamp(LocalDateTime.now())
                .sessionId(sessionId)
                .username(username)
                .build();
    }
    
    public static WebSocketErrorResponse connectionError(String sessionId, String message) {
        return WebSocketErrorResponse.builder()
                .traceId(generateTraceId())
                .type("CONNECTION_ERROR")
                .error("Connection Error")
                .message(message)
                .suggestion("Please check your connection and try again")
                .timestamp(LocalDateTime.now())
                .sessionId(sessionId)
                .build();
    }
    
    private static String generateTraceId() {
        return java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
