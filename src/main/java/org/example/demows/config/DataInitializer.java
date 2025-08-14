package org.example.demows.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.entity.ExchangeRate;
import org.example.demows.entity.User;
import org.example.demows.repository.ExchangeRateRepository;
import org.example.demows.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Data initializer for populating H2 database with sample data
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");

        // Create sample users
        createSampleUsers();

        // Create sample exchange rates
        createSampleExchangeRates();

        log.info("Sample data initialization completed!");
    }

    private void createSampleUsers() {
        if (userRepository.count() == 0) {
            User demoUser = User.builder()
                    .username("demo")
                    .email("demo@example.com")
                    .password(passwordEncoder.encode("password"))
                    .firstName("Demo")
                    .lastName("User")
                    .phone("+1234567890")
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            User testUser = User.builder()
                    .username("test")
                    .email("test@example.com")
                    .password(passwordEncoder.encode("password"))
                    .firstName("Test")
                    .lastName("User")
                    .phone("+0987654321")
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.saveAll(Arrays.asList(demoUser, testUser));
            log.info("Created sample users: demo/password, test/password");
        }
    }

    private void createSampleExchangeRates() {
        if (exchangeRateRepository.count() == 0) {
            ExchangeRate usdEur = ExchangeRate.builder()
                    .fromCurrency("USD")
                    .toCurrency("EUR")
                    .rate(new BigDecimal("0.85"))
                    .lastUpdated(LocalDateTime.now())
                    .build();

            ExchangeRate usdGbp = ExchangeRate.builder()
                    .fromCurrency("USD")
                    .toCurrency("GBP")
                    .rate(new BigDecimal("0.73"))
                    .lastUpdated(LocalDateTime.now())
                    .build();

            ExchangeRate usdJpy = ExchangeRate.builder()
                    .fromCurrency("USD")
                    .toCurrency("JPY")
                    .rate(new BigDecimal("110.50"))
                    .lastUpdated(LocalDateTime.now())
                    .build();

            ExchangeRate eurUsd = ExchangeRate.builder()
                    .fromCurrency("EUR")
                    .toCurrency("USD")
                    .rate(new BigDecimal("1.18"))
                    .lastUpdated(LocalDateTime.now())
                    .build();

            ExchangeRate eurGbp = ExchangeRate.builder()
                    .fromCurrency("EUR")
                    .toCurrency("GBP")
                    .rate(new BigDecimal("0.86"))
                    .lastUpdated(LocalDateTime.now())
                    .build();

            ExchangeRate gbpUsd = ExchangeRate.builder()
                    .fromCurrency("GBP")
                    .toCurrency("USD")
                    .rate(new BigDecimal("1.37"))
                    .lastUpdated(LocalDateTime.now())
                    .build();

            ExchangeRate gbpEur = ExchangeRate.builder()
                    .fromCurrency("GBP")
                    .toCurrency("EUR")
                    .rate(new BigDecimal("1.16"))
                    .lastUpdated(LocalDateTime.now())
                    .build();

            exchangeRateRepository.saveAll(Arrays.asList(
                    usdEur, usdGbp, usdJpy, eurUsd, eurGbp, gbpUsd, gbpEur
            ));
            log.info("Created sample exchange rates");
        }
    }
}
