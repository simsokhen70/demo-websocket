package org.example.demows.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.PromotionDto;
import org.example.demows.service.PromotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for promotion operations
 */
@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Promotions", description = "Promotion management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    @Operation(summary = "Get user promotions", description = "Retrieves all active promotions for the current user")
    public ResponseEntity<List<PromotionDto>> getUserPromotions(Authentication authentication) {
        String username = authentication.getName();
        log.info("Request to get promotions for user: {}", username);
        List<PromotionDto> promotions = promotionService.getUserPromotions(username);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all active promotions", description = "Retrieves all active promotions (admin function)")
    public ResponseEntity<List<PromotionDto>> getAllActivePromotions() {
        log.info("Request to get all active promotions");
        List<PromotionDto> promotions = promotionService.getAllActivePromotions();
        return ResponseEntity.ok(promotions);
    }

    @PostMapping
    @Operation(summary = "Create promotion", description = "Creates a new promotion for the current user")
    public ResponseEntity<PromotionDto> createPromotion(
            Authentication authentication,
            @Valid @RequestBody PromotionDto promotionDto) {
        String username = authentication.getName();
        log.info("Request to create promotion for user: {}", username);
        PromotionDto createdPromotion = promotionService.createPromotion(username, promotionDto);
        return ResponseEntity.ok(createdPromotion);
    }

    @PostMapping("/{username}")
    @Operation(summary = "Create promotion", description = "Creates a new promotion for other user by username")
    public ResponseEntity<PromotionDto> createPromotionByAdmin(
            @PathVariable String username,
            @Valid @RequestBody PromotionDto promotionDto) {
        log.info("Request to create promotion for user: {}", username);
        PromotionDto createdPromotion = promotionService.createPromotion(username, promotionDto);
        return ResponseEntity.ok(createdPromotion);
    }

    @PutMapping("/{promotionId}")
    @Operation(summary = "Update promotion", description = "Updates an existing promotion")
    public ResponseEntity<PromotionDto> updatePromotion(
            Authentication authentication,
            @Parameter(description = "Promotion ID", example = "1")
            @PathVariable Long promotionId,
            @Valid @RequestBody PromotionDto promotionDto) {
        String username = authentication.getName();
        log.info("Request to update promotion {} for user: {}", promotionId, username);
        PromotionDto updatedPromotion = promotionService.updatePromotion(promotionId, username, promotionDto);
        return ResponseEntity.ok(updatedPromotion);
    }

    @DeleteMapping("/{promotionId}")
    @Operation(summary = "Delete promotion", description = "Deactivates a promotion")
    public ResponseEntity<Void> deletePromotion(
            Authentication authentication,
            @Parameter(description = "Promotion ID", example = "1")
            @PathVariable Long promotionId) {
        String username = authentication.getName();
        log.info("Request to delete promotion {} for user: {}", promotionId, username);
        promotionService.deletePromotion(promotionId, username);
        return ResponseEntity.noContent().build();
    }
}
