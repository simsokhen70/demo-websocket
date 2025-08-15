package org.example.demows.service;


import org.example.demows.dto.ExchangeRateDto;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRateService {
    List<ExchangeRateDto> getAllExchangeRates();
    ExchangeRateDto getExchangeRate(String fromCurrency, String toCurrency);

    ExchangeRateDto updateExchangeRate(String fromCurrency, String toCurrency, BigDecimal newRate);

}
