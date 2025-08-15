package org.example.demows.repository;

import org.example.demows.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Promotion entity
 */
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByUserIdAndIsActiveTrue(Long userId);

   // @Query("SELECT p FROM Promotion p WHERE p.user.id = :userId AND p.isActive = true AND p.validFrom <= :now AND p.validUntil >= :now")
    @Query("SELECT p FROM Promotion p WHERE p.user.id = :userId AND p.isActive = true")

    List<Promotion> findActivePromotionsForUser(@Param("userId") Long userId);

    @Query("SELECT p FROM Promotion p WHERE p.user.id = :userId AND p.isActive = true AND p.isUsed = false AND p.validFrom <= :now AND p.validUntil >= :now")
    List<Promotion> findUnusedActivePromotionsForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    //@Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.validFrom <= :now AND p.validUntil >= :now")
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true")
    List<Promotion> findAllActivePromotions();
}
