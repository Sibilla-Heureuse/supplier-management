package com.stockmanager.supplier.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI supplierManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Supplier Management API")
                        .description("RESTful API for managing suppliers in the Stock Management system.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Stock Manager Team")
                                .email("dev@stockmanager.com"))
                        .license(new License()
                                .name("MIT License")))
                .components(new Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")));
    }
}
