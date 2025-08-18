package org.example.demows.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for Swagger documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        final boolean swaggerEnabled = Boolean.parseBoolean(System.getenv().getOrDefault("SWAGGER_ENABLED", "true"));
        
        String serverUrl = System.getenv().getOrDefault("OPENAPI_SERVER_URL", "");

        OpenAPI openAPI = new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                
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
        if (!serverUrl.isBlank()) {
            openAPI.addServersItem(new Server().url(serverUrl).description("Configured server"));
        }
        if (!swaggerEnabled) {
            // When disabled, keep OpenAPI bean minimal (no servers), UI paths can be blocked by security config
        }
        return openAPI;
    }
}
