package com.library.fine.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI libraryFineOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Library Fine API")
                .description("API para gestionar multas: consulta, listado y pago.")
                .version("v1"));
    }
}