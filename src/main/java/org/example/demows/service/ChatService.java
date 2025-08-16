package org.example.demows.service;

import org.example.demows.dto.ChatMessageDto;
import org.example.demows.dto.ChatRequestDto;

import java.util.List;

public interface ChatService {
    List<ChatMessageDto> getUserChats(String username);
        List<ChatMessageDto> getConversation(String username, String otherUsername);
    ChatMessageDto sendMessage(String senderUsername, ChatRequestDto request);
    ChatMessageDto markMessageAsRead(Long messageId, String username);
    List<ChatMessageDto> markConversationAsRead(String username, String otherUsername);
    List<String> getChatPartners(String username);
    long getUnreadMessageCount(String username, String otherUsername);
}
