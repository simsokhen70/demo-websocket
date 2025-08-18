package org.example.demows.dto;

/**
 * Centralized list of WebSocket/STOMP message types.
 * Use enum values to avoid typos and ensure clients have a stable contract.
 */
public enum WebSocketMessageType {
    // Exchange rates
    EXCHANGE_RATES_INITIAL,
    EXCHANGE_RATE_UPDATE,

    // Promotions
    PROMOTIONS_INITIAL,
    PROMOTION_UPDATE,

    // Notifications
    NOTIFICATIONS_INITIAL,
    NOTIFICATION_UPDATE,

    // Chat
    CHAT_CONVERSATIONS_INITIAL,
    CHAT_MESSAGE,
    CHAT_MESSAGE_SENT,

    // Errors
    ERROR
}


