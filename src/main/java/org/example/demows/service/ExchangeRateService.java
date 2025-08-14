package org.example.demows.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.ExchangeRateDto;
import org.example.demows.dto.WebSocketMessage;
import org.example.demows.entity.ExchangeRate;
import org.example.demows.exception.ResourceNotFoundException;
import org.example.demows.repository.ExchangeRateRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Service class for exchange rate management with real-time updates
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    private static final String EXCHANGE_RATES_TOPIC = "exchange-rates";
    private static final String EXCHANGE_RATES_WS_TOPIC = "/topic/exchange-rates";
    private final Random random = new Random();

    public List<ExchangeRateDto> getAllExchangeRates() {
        log.info("Fetching all exchange rates");
        return exchangeRateRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public ExchangeRateDto getExchangeRate(String fromCurrency, String toCurrency) {
        log.info("Fetching exchange rate from {} to {}", fromCurrency, toCurrency);
        ExchangeRate exchangeRate = exchangeRateRepository.findExchangeRate(fromCurrency, toCurrency)
                .orElseThrow(() -> new ResourceNotFoundException("ExchangeRate", 
                        String.format("fromCurrency=%s, toCurrency=%s", fromCurrency, toCurrency), 
                        String.format("%s-%s", fromCurrency, toCurrency)));

        return mapToDto(exchangeRate);
    }

    public ExchangeRateDto updateExchangeRate(String fromCurrency, String toCurrency, BigDecimal newRate) {
        log.info("Updating exchange rate from {} to {} with new rate: {}", fromCurrency, toCurrency, newRate);
        ExchangeRate exchangeRate = exchangeRateRepository.findExchangeRate(fromCurrency, toCurrency)
                .orElseThrow(() -> new ResourceNotFoundException("ExchangeRate", 
                        String.format("fromCurrency=%s, toCurrency=%s", fromCurrency, toCurrency), 
                        String.format("%s-%s", fromCurrency, toCurrency)));

        exchangeRate.setRate(newRate);
        exchangeRate.setLastUpdated(LocalDateTime.now());
        
        ExchangeRate updatedRate = exchangeRateRepository.save(exchangeRate);
        ExchangeRateDto dto = mapToDto(updatedRate);

        // Send to Kafka for real-time updates
        publishExchangeRateUpdate(dto);

        return dto;
    }

    /**
     * Scheduled task to simulate real-time exchange rate updates
     * In production, this would call external exchange rate APIs
     */
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void simulateExchangeRateUpdates() {
        log.info("Simulating exchange rate updates");
        
        List<ExchangeRate> allRates = exchangeRateRepository.findAll();
        
        for (ExchangeRate rate : allRates) {
            // Simulate small random changes (±2%)
            BigDecimal currentRate = rate.getRate();
            BigDecimal changePercent = BigDecimal.valueOf(random.nextDouble() * 0.04 - 0.02); // -2% to +2%
            BigDecimal newRate = currentRate.multiply(BigDecimal.ONE.add(changePercent))
                    .setScale(6, RoundingMode.HALF_UP);
            
            rate.setRate(newRate);
            rate.setLastUpdated(LocalDateTime.now());
            
            ExchangeRate updatedRate = exchangeRateRepository.save(rate);
            ExchangeRateDto dto = mapToDto(updatedRate);
            
            // Send to Kafka for real-time updates
            publishExchangeRateUpdate(dto);
        }
    }

    private void publishExchangeRateUpdate(ExchangeRateDto exchangeRateDto) {
        try {
            WebSocketMessage<ExchangeRateDto> message = WebSocketMessage.<ExchangeRateDto>builder()
                    .type("EXCHANGE_RATE_UPDATE")
                    .data(exchangeRateDto)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            String messageJson = objectMapper.writeValueAsString(message);
            
            // Send to Kafka
            kafkaTemplate.send(EXCHANGE_RATES_TOPIC, messageJson);
            
            // Send to WebSocket subscribers
            messagingTemplate.convertAndSend(EXCHANGE_RATES_WS_TOPIC, message);
            
            log.debug("Published exchange rate update: {}", messageJson);
        } catch (JsonProcessingException e) {
            log.error("Error serializing exchange rate update", e);
        }
    }

    private ExchangeRateDto mapToDto(ExchangeRate exchangeRate) {
        return ExchangeRateDto.builder()
                .id(exchangeRate.getId())
                .fromCurrency(exchangeRate.getFromCurrency())
                .toCurrency(exchangeRate.getToCurrency())
                .rate(exchangeRate.getRate())
                .lastUpdated(exchangeRate.getLastUpdated())
                .build();
    }
}
