package org.example.demows.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.ApiResponse;
import org.example.demows.dto.WebSocketErrorResponse;
import org.example.demows.dto.WebSocketMessage;
import org.example.demows.dto.WebSocketMessageType;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        try {
            String traceId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            log.warn("STOMP error [TraceId: {}]: {}", traceId, ex.getMessage());

            WebSocketErrorResponse error = WebSocketErrorResponse.builder()
                    .traceId(traceId)
                    .type("STOMP_ERROR")
                    .error("STOMP Protocol Error")
                    .message(ex.getMessage())
                    .suggestion("Please reconnect and check your payload or headers")
                    .timestamp(LocalDateTime.now())
                    .build();

            WebSocketMessage<WebSocketErrorResponse> payload = WebSocketMessage.<WebSocketErrorResponse>builder()
                    .type(WebSocketMessageType.ERROR.name())
                    .data(error)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            String body = objectMapper.writeValueAsString(payload);

            StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
            accessor.setMessage("STOMP error");
            accessor.setLeaveMutable(true);

            return MessageBuilder.createMessage(body.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
        } catch (Exception e) {
            // Fallback to default behavior
            return super.handleClientMessageProcessingError(clientMessage, ex);
        }
    }
}


