package org.example.demows.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demows.entity.ChatMessage;
import org.example.demows.entity.ExchangeRate;
import org.example.demows.entity.Notification;
import org.example.demows.entity.Promotion;
import org.example.demows.entity.User;
import org.example.demows.repository.ChatMessageRepository;
import org.example.demows.repository.ExchangeRateRepository;
import org.example.demows.repository.NotificationRepository;
import org.example.demows.repository.PromotionRepository;
import org.example.demows.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Data initializer for populating H2 database with sample data
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final PromotionRepository promotionRepository;
    private final NotificationRepository notificationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");

        // Create sample users
        createSampleUsers();

        // Create sample exchange rates
        createSampleExchangeRates();

        // Create sample promotions
        createSamplePromotions();

        // Create sample notification
        initializeNotifications();

        // Create sample chat messages
        createSampleChatMessages();

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

            User adminUser = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("password"))
                    .firstName("Admin")
                    .lastName("User")
                    .phone("+1122334455")
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            User user1 = User.builder()
                    .username("user1")
                    .email("user1@example.com")
                    .password(passwordEncoder.encode("password"))
                    .firstName("User")
                    .lastName("One")
                    .phone("+1555666777")
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            User user2 = User.builder()
                    .username("user2")
                    .email("user2@example.com")
                    .password(passwordEncoder.encode("password"))
                    .firstName("User")
                    .lastName("Two")
                    .phone("+1888999000")
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.saveAll(Arrays.asList(demoUser, testUser, adminUser, user1, user2));
            log.info("Created sample users: demo/password, test/password, admin/password, user1/password, user2/password");
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

    private void createSamplePromotions() {
        if (promotionRepository.count() == 0) {
            // Get demo user
            User demoUser = userRepository.findByUsername("demo").orElse(null);
            if (demoUser != null) {
                Promotion welcomeBonus = Promotion.builder()
                        .user(demoUser)
                        .title("Welcome Bonus")
                        .description("Get 10% off your first purchase")
                        .discountPercentage(new BigDecimal("10.00"))
                        .minPurchaseAmount(new BigDecimal("50.00"))
                        .validFrom(LocalDateTime.now())
                        .validUntil(LocalDateTime.now().plusMonths(1))
                        .isActive(true)
                        .isUsed(false)
                        .build();

                Promotion referralBonus = Promotion.builder()
                        .user(demoUser)
                        .title("Refer a Friend")
                        .description("Earn $20 when your friend joins")
                        .discountAmount(new BigDecimal("20.00"))
                        .validFrom(LocalDateTime.now())
                        .validUntil(LocalDateTime.now().plusMonths(3))
                        .isActive(true)
                        .isUsed(false)
                        .build();

                promotionRepository.saveAll(Arrays.asList(welcomeBonus, referralBonus));
                log.info("Created sample promotions for demo user");
            }
        }
    }

    public void initializeNotifications() {
        log.info("Initializing sample notifications...");

        try {
            User demoUser = userRepository.findByUsername("demo")
                    .orElseThrow(() -> new RuntimeException("Demo user not found"));

            // Create sample notifications
            List<Notification> notifications = Arrays.asList(
                    Notification.builder()
                            .username(demoUser.getUsername())
                            .user(demoUser)
                            .title("Welcome to Demo-WS!")
                            .message("Thank you for joining our platform. Check out our latest promotions!")
                            .type("SYSTEM")
                            .priority("NORMAL")
                            .isRead(false)
                            .isActive(true)
                            .createdAt(LocalDateTime.now().minusHours(2))
                            .build(),

                    Notification.builder()
                            .username(demoUser.getUsername())
                            .user(demoUser)
                            .title("New Promotion Available")
                            .message("You have a new 20% discount on all purchases!")
                            .type("PROMOTION")
                            .priority("HIGH")
                            .isRead(false)
                            .isActive(true)
                            .createdAt(LocalDateTime.now().minusMinutes(30))
                            .build()
            );

            notificationRepository.saveAll(notifications);
            log.info("Created {} sample notifications", notifications.size());

        } catch (Exception e) {
            log.error("Error initializing notifications", e);
        }
    }

    public void createSampleChatMessages() {
        log.info("Initializing sample chat messages...");

        try {
            User demoUser = userRepository.findByUsername("demo")
                    .orElseThrow(() -> new RuntimeException("Demo user not found"));
            User testUser = userRepository.findByUsername("test")
                    .orElseThrow(() -> new RuntimeException("Test user not found"));
            User adminUser = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("Admin user not found"));
            User user1 = userRepository.findByUsername("user1")
                    .orElseThrow(() -> new RuntimeException("User1 not found"));
            User user2 = userRepository.findByUsername("user2")
                    .orElseThrow(() -> new RuntimeException("User2 not found"));

            // Create sample chat messages between multiple users
            List<ChatMessage> chatMessages = Arrays.asList(
                // Demo <-> Test conversation
                ChatMessage.builder()
                        .senderUsername("demo")
                        .receiverUsername("test")
                        .message("Hey! How are you doing?")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(30))
                        .isRead(true)
                        .readAt(LocalDateTime.now().minusMinutes(25))
                        .isDeleted(false)
                        .build(),

                ChatMessage.builder()
                        .senderUsername("test")
                        .receiverUsername("demo")
                        .message("Hi! I'm doing great, thanks for asking. How about you?")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(25))
                        .isRead(true)
                        .readAt(LocalDateTime.now().minusMinutes(20))
                        .isDeleted(false)
                        .build(),

                ChatMessage.builder()
                        .senderUsername("demo")
                        .receiverUsername("test")
                        .message("I'm good too! Have you checked out the new exchange rates?")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(20))
                        .isRead(true)
                        .readAt(LocalDateTime.now().minusMinutes(15))
                        .isDeleted(false)
                        .build(),

                ChatMessage.builder()
                        .senderUsername("test")
                        .receiverUsername("demo")
                        .message("Yes! The USD to EUR rate is looking good today.")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(15))
                        .isRead(false)
                        .isDeleted(false)
                        .build(),

                ChatMessage.builder()
                        .senderUsername("demo")
                        .receiverUsername("test")
                        .message("Perfect! Let's catch up later!")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(5))
                        .isRead(false)
                        .isDeleted(false)
                        .build(),

                // Demo <-> Admin conversation
                ChatMessage.builder()
                        .senderUsername("demo")
                        .receiverUsername("admin")
                        .message("Hi Admin! Can you help me with something?")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(45))
                        .isRead(true)
                        .readAt(LocalDateTime.now().minusMinutes(40))
                        .isDeleted(false)
                        .build(),

                ChatMessage.builder()
                        .senderUsername("admin")
                        .receiverUsername("demo")
                        .message("Of course! What do you need help with?")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(40))
                        .isRead(true)
                        .readAt(LocalDateTime.now().minusMinutes(35))
                        .isDeleted(false)
                        .build(),

                ChatMessage.builder()
                        .senderUsername("demo")
                        .receiverUsername("admin")
                        .message("I'm having trouble with the chat feature.")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(35))
                        .isRead(false)
                        .isDeleted(false)
                        .build(),

                // Test <-> User1 conversation
                ChatMessage.builder()
                        .senderUsername("test")
                        .receiverUsername("user1")
                        .message("Hello User1! How's it going?")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(50))
                        .isRead(true)
                        .readAt(LocalDateTime.now().minusMinutes(45))
                        .isDeleted(false)
                        .build(),

                ChatMessage.builder()
                        .senderUsername("user1")
                        .receiverUsername("test")
                        .message("Hi Test! Everything is going well, thanks!")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(45))
                        .isRead(false)
                        .isDeleted(false)
                        .build(),

                // Admin <-> User2 conversation
                ChatMessage.builder()
                        .senderUsername("admin")
                        .receiverUsername("user2")
                        .message("Welcome to the platform, User2!")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(60))
                        .isRead(true)
                        .readAt(LocalDateTime.now().minusMinutes(55))
                        .isDeleted(false)
                        .build(),

                ChatMessage.builder()
                        .senderUsername("user2")
                        .receiverUsername("admin")
                        .message("Thank you! I'm excited to explore the features.")
                        .messageType(ChatMessage.MessageType.TEXT)
                        .timestamp(LocalDateTime.now().minusMinutes(55))
                        .isRead(false)
                        .isDeleted(false)
                        .build()
            );

            chatMessageRepository.saveAll(chatMessages);
            log.info("Created {} sample chat messages", chatMessages.size());

        } catch (Exception e) {
            log.error("Error initializing chat messages", e);
        }
    }
}
