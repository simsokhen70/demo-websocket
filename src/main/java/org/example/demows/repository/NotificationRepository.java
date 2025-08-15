package org.example.demows.repository;

import org.example.demows.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Find unread notifications for user
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId " +
            "AND n.isRead = false AND n.isActive = true " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotificationsForUser(@Param("userId") Long userId);

    // Find all notifications for user
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId " +
            "AND n.isActive = true ORDER BY n.createdAt DESC")
    List<Notification> findAllNotificationsForUser(@Param("userId") Long userId);

    // Count unread notifications
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId " +
            "AND n.isRead = false AND n.isActive = true")
    long countUnreadNotificationsForUser(@Param("userId") Long userId);
}
