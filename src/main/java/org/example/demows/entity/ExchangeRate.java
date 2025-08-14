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
 * ExchangeRate entity for storing currency exchange rates
 */
@Entity
@Table(name = "exchange_rates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "From currency is required")
    @Column(name = "from_currency", nullable = false, length = 3)
    private String fromCurrency;

    @NotBlank(message = "To currency is required")
    @Column(name = "to_currency", nullable = false, length = 3)
    private String toCurrency;

    @NotNull(message = "Exchange rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Exchange rate must be positive")
    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal rate;

    @Builder.Default
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
