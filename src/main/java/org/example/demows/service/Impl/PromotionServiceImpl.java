package org.example.demows.service.Impl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.demows.dto.PromotionDto;
import org.example.demows.dto.WebSocketMessage;
import org.example.demows.entity.Promotion;
import org.example.demows.entity.User;
import org.example.demows.exception.ResourceNotFoundException;
import org.example.demows.repository.PromotionRepository;
import org.example.demows.service.PromotionService;
import org.example.demows.service.UserService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final UserService userService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    private static final String PROMOTIONS_TOPIC = "promotions";
    private final Random random = new Random();
    @Override
    public List<PromotionDto> getUserPromotions(String username) {
        log.info("Fetching promotions for user: {}", username);

        if ("anonymous".equals(username)) {
            log.warn("Anonymous user requested promotions - returning empty list");
            return List.of();
        }

        try {
            User user = (User) userService.loadUserByUsername(username);
            return promotionRepository.findActivePromotionsForUser(user.getId())
                    .stream()
                    .map(this::mapToDto)
                    .toList();
        } catch (Exception e) {
            log.error("Error fetching promotions for user {}: {}", username, e.getMessage());
            return List.of();
        }
    }

    public List<PromotionDto> getAllActivePromotions() {
        log.info("Fetching all active promotions");
        return promotionRepository.findAllActivePromotions()
                .stream()
                .map(this::mapToDto)
                .toList();
    }
    @Override
    public PromotionDto createPromotion(String username, PromotionDto promotionDto) {
        log.info("Creating promotion for user: {}", username);
        User user = (User) userService.loadUserByUsername(username);

        Promotion promotion = Promotion.builder()
                .user(user)
                .title(promotionDto.getTitle())
                .description(promotionDto.getDescription())
                .discountPercentage(promotionDto.getDiscountPercentage())
                .discountAmount(promotionDto.getDiscountAmount())
                .minPurchaseAmount(promotionDto.getMinPurchaseAmount())
                .maxDiscountAmount(promotionDto.getMaxDiscountAmount())
                .validFrom(promotionDto.getValidFrom())
                .validUntil(promotionDto.getValidUntil())
                .isActive(true)
                .isUsed(false)
                .build();

        Promotion savedPromotion = promotionRepository.save(promotion);
        PromotionDto dto = mapToDto(savedPromotion);

        // Send to Kafka for real-time updates
        publishPromotionUpdate(dto, username);

        return dto;
    }
    @Override
    public PromotionDto updatePromotion(Long promotionId, String username, PromotionDto promotionDto) {
        log.info("Updating promotion {} for user: {}", promotionId, username);
        User user = (User) userService.loadUserByUsername(username);

        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", promotionId));

        // Ensure the promotion belongs to the user
        if (!promotion.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Promotion", "id", promotionId);
        }

        if (promotionDto.getTitle() != null) {
            promotion.setTitle(promotionDto.getTitle());
        }
        if (promotionDto.getDescription() != null) {
            promotion.setDescription(promotionDto.getDescription());
        }
        if (promotionDto.getDiscountPercentage() != null) {
            promotion.setDiscountPercentage(promotionDto.getDiscountPercentage());
        }
        if (promotionDto.getDiscountAmount() != null) {
            promotion.setDiscountAmount(promotionDto.getDiscountAmount());
        }
        if (promotionDto.getMinPurchaseAmount() != null) {
            promotion.setMinPurchaseAmount(promotionDto.getMinPurchaseAmount());
        }
        if (promotionDto.getMaxDiscountAmount() != null) {
            promotion.setMaxDiscountAmount(promotionDto.getMaxDiscountAmount());
        }
        if (promotionDto.getValidFrom() != null) {
            promotion.setValidFrom(promotionDto.getValidFrom());
        }
        if (promotionDto.getValidUntil() != null) {
            promotion.setValidUntil(promotionDto.getValidUntil());
        }

        Promotion updatedPromotion = promotionRepository.save(promotion);
        PromotionDto dto = mapToDto(updatedPromotion);

        // Send to Kafka for real-time updates
        publishPromotionUpdate(dto, username);

        return dto;
    }
    @Override
    public void deletePromotion(Long promotionId, String username) {
        log.info("Deleting promotion {} for user: {}", promotionId, username);
        User user = (User) userService.loadUserByUsername(username);

        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", promotionId));

        // Ensure the promotion belongs to the user
        if (!promotion.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Promotion", "id", promotionId);
        }

        promotion.setIsActive(false);
        promotionRepository.save(promotion);

        // Send to Kafka for real-time updates
        publishPromotionUpdate(mapToDto(promotion), username);
    }

    /**
     * Scheduled task to simulate new promotions being created
     * In production, this would be triggered by business logic
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void simulateNewPromotions() {
        log.info("Simulating new promotions");

        // Get all users and randomly create promotions for some of them
        // This is a simplified simulation - in real scenario, promotions would be created based on business rules

        // For demo purposes, we'll just log that promotions could be created
        log.info("New promotions simulation completed");
    }

    private void publishPromotionUpdate(PromotionDto promotionDto, String username) {
        try {
            WebSocketMessage<PromotionDto> message = WebSocketMessage.<PromotionDto>builder()
                    .type("PROMOTION_UPDATE")
                    .data(promotionDto)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            String messageJson = objectMapper.writeValueAsString(message);

            // Send to Kafka
            kafkaTemplate.send(PROMOTIONS_TOPIC, messageJson);

            // Send to specific user's WebSocket queue
            messagingTemplate.convertAndSendToUser(username, "/queue/promotions", message);

            log.debug("Published promotion update for user {}: {}", username, messageJson);
        } catch (JsonProcessingException e) {
            log.error("Error serializing promotion update", e);
        }
    }

    private PromotionDto mapToDto(Promotion promotion) {
        return PromotionDto.builder()
                .id(promotion.getId())
                .title(promotion.getTitle())
                .description(promotion.getDescription())
                .discountPercentage(promotion.getDiscountPercentage())
                .discountAmount(promotion.getDiscountAmount())
                .minPurchaseAmount(promotion.getMinPurchaseAmount())
                .maxDiscountAmount(promotion.getMaxDiscountAmount())
                .validFrom(promotion.getValidFrom())
                .validUntil(promotion.getValidUntil())
                .isActive(promotion.getIsActive())
                .isUsed(promotion.getIsUsed())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .build();
    }
}
