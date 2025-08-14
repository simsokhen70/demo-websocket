package org.example.demows;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application class for Demo WebSocket Service
 * 
 * @author Demo Team
 * @version 1.0
 */
@SpringBootApplication
@EnableScheduling
public class DemoWsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoWsApplication.class, args);
    }
}
