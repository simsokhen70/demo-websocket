package org.example.demows.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.ExchangeRateDto;
import org.example.demows.service.ExchangeRateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for exchange rate operations
 */
@RestController
@RequestMapping("/api/exchange-rates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Exchange Rates", description = "Exchange rate management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    @Operation(summary = "Get all exchange rates", description = "Retrieves all available exchange rates")
    public ResponseEntity<List<ExchangeRateDto>> getAllExchangeRates() {
        log.info("Request to get all exchange rates");
        List<ExchangeRateDto> exchangeRates = exchangeRateService.getAllExchangeRates();
        return ResponseEntity.ok(exchangeRates);
    }

    @GetMapping("/{fromCurrency}/{toCurrency}")
    @Operation(summary = "Get specific exchange rate", description = "Retrieves exchange rate for specific currency pair")
    public ResponseEntity<ExchangeRateDto> getExchangeRate(
            @Parameter(description = "Source currency code (e.g., USD)", example = "USD")
            @PathVariable String fromCurrency,
            @Parameter(description = "Target currency code (e.g., EUR)", example = "EUR")
            @PathVariable String toCurrency) {
        log.info("Request to get exchange rate from {} to {}", fromCurrency, toCurrency);
        ExchangeRateDto exchangeRate = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);
        return ResponseEntity.ok(exchangeRate);
    }

    @PutMapping("/{fromCurrency}/{toCurrency}")
    @Operation(summary = "Update exchange rate", description = "Updates exchange rate for specific currency pair")
    public ResponseEntity<ExchangeRateDto> updateExchangeRate(
            @Parameter(description = "Source currency code", example = "USD")
            @PathVariable String fromCurrency,
            @Parameter(description = "Target currency code", example = "EUR")
            @PathVariable String toCurrency,
            @Parameter(description = "New exchange rate value", example = "0.85")
            @RequestParam BigDecimal rate) {
        log.info("Request to update exchange rate from {} to {} with rate: {}", fromCurrency, toCurrency, rate);
        ExchangeRateDto updatedRate = exchangeRateService.updateExchangeRate(fromCurrency, toCurrency, rate);
        return ResponseEntity.ok(updatedRate);
    }
}
