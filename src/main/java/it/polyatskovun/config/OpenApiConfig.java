package it.polyatskovun.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product API")
                        .version("v1")
                        .description("REST API for managing products")
                );
    }
}