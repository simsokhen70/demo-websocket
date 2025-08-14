package org.example.demows.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.dto.ExchangeRateDto;
import org.example.demows.dto.PromotionDto;
import org.example.demows.dto.WebSocketMessage;
import org.example.demows.service.ExchangeRateService;
import org.example.demows.service.PromotionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WebSocket controller for handling real-time messaging
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final ExchangeRateService exchangeRateService;
    private final PromotionService promotionService;

    @MessageMapping("/exchange-rates/subscribe")
    @SendTo("/topic/exchange-rates")
    public WebSocketMessage<List<ExchangeRateDto>> subscribeToExchangeRates(SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "anonymous";
        log.info("User {} subscribed to exchange rates", username);
        
        List<ExchangeRateDto> exchangeRates = exchangeRateService.getAllExchangeRates();
        
        return WebSocketMessage.<List<ExchangeRateDto>>builder()
                .type("EXCHANGE_RATES_INITIAL")
                .data(exchangeRates)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @MessageMapping("/promotions/subscribe")
    @SendToUser("/queue/promotions")
    public WebSocketMessage<List<PromotionDto>> subscribeToPromotions(SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "anonymous";
        log.info("User {} subscribed to promotions", username);
        
        List<PromotionDto> promotions = promotionService.getUserPromotions(username);
        
        return WebSocketMessage.<List<PromotionDto>>builder()
                .type("PROMOTIONS_INITIAL")
                .data(promotions)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @MessageMapping("/exchange-rates/request")
    @SendTo("/topic/exchange-rates")
    public WebSocketMessage<ExchangeRateDto> requestExchangeRate(String currencyPair) {
        log.info("Exchange rate request received for: {}", currencyPair);
        
        try {
            String[] currencies = currencyPair.split("-");
            if (currencies.length == 2) {
                ExchangeRateDto exchangeRate = exchangeRateService.getExchangeRate(currencies[0], currencies[1]);
                
                return WebSocketMessage.<ExchangeRateDto>builder()
                        .type("EXCHANGE_RATE_REQUEST")
                        .data(exchangeRate)
                        .timestamp(LocalDateTime.now().toString())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error processing exchange rate request for: {}", currencyPair, e);
        }
        
        return WebSocketMessage.<ExchangeRateDto>builder()
                .type("EXCHANGE_RATE_ERROR")
                .data(null)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
