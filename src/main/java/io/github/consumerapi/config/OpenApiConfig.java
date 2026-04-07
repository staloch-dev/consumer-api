package io.github.consumerapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Consumer API")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de clientes. " +
                                "A interface permite realizar o CRUD completo de clientes, " +
                                "incluindo buscas por ID, CPF e filtragem por nome, " +
                                "garantindo a integridade de dados únicos como e-mail e CPF.")
                        .contact(new Contact()
                                .name("Jaison Staloch Junior")
                                .email("staloch.dev@gmail.com")
                                .url("https://github.com/staloch-dev")));
    }

}
