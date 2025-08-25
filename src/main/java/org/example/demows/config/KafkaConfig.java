package org.example.demows.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

/**
 * Kafka configuration for producer setup. Relies on Spring Boot's KafkaProperties,
 * which are sourced from application.yml (and env vars), avoiding hardcoded secrets.
 */
@Configuration
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public NewTopic exchangeRatesTopic() {
        return TopicBuilder.name("exchange-rates")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic promotionsTopic() {
        return TopicBuilder.name("promotions")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationsTopic() {
        return TopicBuilder.name("notifications")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic chatTopic() {
        return TopicBuilder.name("chat-messages").build();
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> producerConfigs = kafkaProperties.buildProducerProperties();
        // Recommended production options; keep them in properties per profile, but these are safe defaults
        // Use values from properties if present; KafkaProperties already merges them.
        return new DefaultKafkaProducerFactory<>(producerConfigs);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

/*    @Bean
    DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        var recoverer = new DeadLetterPublishingRecoverer(
                template,
                (r, e) -> new TopicPartition(r.topic() + ".DLT", r.partition())
        );
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
    }*/

}
