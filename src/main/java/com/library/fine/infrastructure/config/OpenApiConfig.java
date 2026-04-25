package com.library.fine.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI libraryFineOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Library Fine API")
                .description("API para gestionar multas: consulta, listado y pago.")
                .version("v1"))
                .servers(List.of(new Server().url("/").description("Current host")));
    }
}