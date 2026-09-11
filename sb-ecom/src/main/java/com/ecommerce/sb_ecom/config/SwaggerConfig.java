package com.ecommerce.sb_ecom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {


    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearerScheme = new SecurityScheme().type(SecurityScheme.Type.APIKEY).scheme("bearer").bearerFormat("JWT").description("JWT bearer token");

        SecurityRequirement beareRequirement = new SecurityRequirement().addList("Bearer authentication");

        return new OpenAPI()
                .info(new Info()
                        .title("Spring boot ecom API")
                        .description("This is a spring boot project for eCommerce")
                        .version("1.0")
                )
                .components(new Components()
                        .addSecuritySchemes("Bearer authentication", bearerScheme))
                .addSecurityItem(beareRequirement);
    }
}
