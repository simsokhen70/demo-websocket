package org.example.demows.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.ApiResponse;
import org.example.demows.dto.ChatMessageDto;
import org.example.demows.dto.ChatRequestDto;
import org.example.demows.entity.User;
import org.example.demows.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Real-time chat management APIs")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations")
    @Operation(summary = "Get user's chat conversations", description = "Retrieves all chat conversations for the current user")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getUserChats(@AuthenticationPrincipal User user) {
        log.info("Fetching chat conversations for user: {}", user.getUsername());
        List<ChatMessageDto> chats = chatService.getUserChats(user.getUsername());
        
        return ResponseEntity.ok(ApiResponse.<List<ChatMessageDto>>builder()
                .success(true)
                .message("Chat conversations retrieved successfully")
                .data(chats)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/conversation/{otherUsername}")
    @Operation(summary = "Get conversation with specific user", description = "Retrieves conversation between current user and specified user")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getConversation(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Username of the other person in conversation", example = "john")
            @PathVariable String otherUsername) {
        
        log.info("Fetching conversation between {} and {}", user.getUsername(), otherUsername);
        List<ChatMessageDto> conversation = chatService.getConversation(user.getUsername(), otherUsername);
        
        return ResponseEntity.ok(ApiResponse.<List<ChatMessageDto>>builder()
                .success(true)
                .message("Conversation retrieved successfully")
                .data(conversation)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/send")
    @Operation(summary = "Send a message", description = "Sends a message to another user")
    public ResponseEntity<ApiResponse<ChatMessageDto>> sendMessage(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChatRequestDto request) {
        
        log.info("Sending message from {} to {}: {}", user.getUsername(), request.getReceiverUsername(), request.getMessage());
        ChatMessageDto sentMessage = chatService.sendMessage(user.getUsername(), request);
        
        return ResponseEntity.ok(ApiResponse.<ChatMessageDto>builder()
                .success(true)
                .message("Message sent successfully")
                .data(sentMessage)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/message/{messageId}/read")
    @Operation(summary = "Mark message as read", description = "Marks a specific message as read")
    public ResponseEntity<ApiResponse<ChatMessageDto>> markMessageAsRead(
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID of the message to mark as read", example = "1")
            @PathVariable Long messageId) {
        
        log.info("Marking message {} as read by user: {}", messageId, user.getUsername());
        ChatMessageDto updatedMessage = chatService.markMessageAsRead(messageId, user.getUsername());
        
        return ResponseEntity.ok(ApiResponse.<ChatMessageDto>builder()
                .success(true)
                .message("Message marked as read")
                .data(updatedMessage)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping("/conversation/{otherUsername}/read")
    @Operation(summary = "Mark conversation as read", description = "Marks all messages in a conversation as read")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> markConversationAsRead(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Username of the other person in conversation", example = "john")
            @PathVariable String otherUsername) {
        
        log.info("Marking conversation between {} and {} as read", user.getUsername(), otherUsername);
        List<ChatMessageDto> updatedMessages = chatService.markConversationAsRead(user.getUsername(), otherUsername);
        
        return ResponseEntity.ok(ApiResponse.<List<ChatMessageDto>>builder()
                .success(true)
                .message("Conversation marked as read")
                .data(updatedMessages)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/partners")
    @Operation(summary = "Get chat partners", description = "Retrieves list of users the current user has chatted with")
    public ResponseEntity<ApiResponse<List<String>>> getChatPartners(@AuthenticationPrincipal User user) {
        log.info("Fetching chat partners for user: {}", user.getUsername());
        List<String> partners = chatService.getChatPartners(user.getUsername());
        
        return ResponseEntity.ok(ApiResponse.<List<String>>builder()
                .success(true)
                .message("Chat partners retrieved successfully")
                .data(partners)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/unread/{otherUsername}")
    @Operation(summary = "Get unread message count", description = "Retrieves count of unread messages from a specific user")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Username to count unread messages from", example = "john")
            @PathVariable String otherUsername) {
        
        log.info("Fetching unread message count from {} for user: {}", otherUsername, user.getUsername());
        long count = chatService.getUnreadMessageCount(user.getUsername(), otherUsername);
        
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .success(true)
                .message("Unread message count retrieved successfully")
                .data(count)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
