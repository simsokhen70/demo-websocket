package org.example.demows.repository;

import org.example.demows.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    // Get conversation between two users (both directions)
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "((cm.senderUsername = :user1 AND cm.receiverUsername = :user2) OR " +
           "(cm.senderUsername = :user2 AND cm.receiverUsername = :user1)) AND " +
           "cm.isDeleted = false " +
           "ORDER BY cm.timestamp ASC")
    List<ChatMessage> findConversationBetweenUsers(@Param("user1") String user1, @Param("user2") String user2);

    // Get unread messages for a user
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "cm.receiverUsername = :username AND " +
           "cm.isRead = false AND " +
           "cm.isDeleted = false " +
           "ORDER BY cm.timestamp DESC")
    List<ChatMessage> findUnreadMessagesByReceiverUsername(@Param("username") String username);

    // Get all conversations for a user (get unique chat partners)
    @Query("SELECT DISTINCT " +
           "CASE " +
           "  WHEN cm.senderUsername = :username THEN cm.receiverUsername " +
           "  ELSE cm.senderUsername " +
           "END as chatPartner " +
           "FROM ChatMessage cm " +
           "WHERE (cm.senderUsername = :username OR cm.receiverUsername = :username) " +
           "AND cm.isDeleted = false")
    List<String> findChatPartnersForUser(@Param("username") String username);

    // Get last message from each conversation
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.id IN (" +
           "SELECT MAX(cm2.id) FROM ChatMessage cm2 " +
           "WHERE (cm2.senderUsername = :username OR cm2.receiverUsername = :username) " +
           "AND cm2.isDeleted = false " +
           "GROUP BY " +
           "CASE " +
           "  WHEN cm2.senderUsername = :username THEN cm2.receiverUsername " +
           "  ELSE cm2.senderUsername " +
           "END) " +
           "ORDER BY cm.timestamp DESC")
    List<ChatMessage> findLastMessagesForUser(@Param("username") String username);

    // Count unread messages for a specific conversation
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE " +
           "cm.receiverUsername = :receiverUsername AND " +
           "cm.senderUsername = :senderUsername AND " +
           "cm.isRead = false AND " +
           "cm.isDeleted = false")
    long countUnreadMessagesInConversation(@Param("senderUsername") String senderUsername, 
                                          @Param("receiverUsername") String receiverUsername);
}
