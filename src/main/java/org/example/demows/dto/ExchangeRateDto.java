package org.example.demows.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for exchange rate information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {

    private Long id;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal rate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;
}
