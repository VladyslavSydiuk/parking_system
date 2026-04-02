package com.parking.system.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI parkingSystemOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Parking System API")
                        .description("REST API for parking lot administration, vehicle check-in/check-out, and fee calculation.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Smart Parking System")
                                .email("support@parking-system.local"))
                        .license(new License()
                                .name("Internal Assignment Use")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project README")
                        .url("https://github.com/VladyslavSydiuk/parking_system"));
    }
}
