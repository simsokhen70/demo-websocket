package org.example.demows.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI configuration for Swagger documentation
 */
@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                final String securitySchemeName = "bearerAuth";

                return new OpenAPI()
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .addServersItem(new Server()
                                                .url("https://0ee3709bb06f.ngrok-free.app")
                                                .description("Dev server"))
                                .addServersItem(new Server()
                                                .url("http://localhost:8080")
                                                .description("Local server"))
                                .components(new Components()
                                                .addSecuritySchemes(securitySchemeName,
                                                                new SecurityScheme()
                                                                                .name(securitySchemeName)
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")))
                                .info(new Info()
                                                .title("Demo WebSocket API")
                                                .description("A comprehensive Spring Boot WebSocket application with real-time features including user management, exchange rates, chat message, notification and promotions.")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Demo Team")
                                                                .url("https://github.com/simsokhen70/demo-websocket/tree/demo")));
        }
}
