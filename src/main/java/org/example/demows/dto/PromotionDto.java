package org.example.demows.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for promotion information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDto {

    private Long id;
    private String title;
    private String description;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal minPurchaseAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean isActive;
    private Boolean isUsed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
