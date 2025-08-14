package org.example.demows.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Promotion entity for storing user-specific promotions
 */
@Entity
@Table(name = "promotions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @DecimalMin(value = "0.0", message = "Discount percentage must be non-negative")
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "Minimum purchase amount must be non-negative")
    @Column(name = "min_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount;

    @DecimalMin(value = "0.0", message = "Maximum discount amount must be non-negative")
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @NotNull(message = "Valid from date is required")
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @NotNull(message = "Valid until date is required")
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_used")
    private Boolean isUsed = false;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
