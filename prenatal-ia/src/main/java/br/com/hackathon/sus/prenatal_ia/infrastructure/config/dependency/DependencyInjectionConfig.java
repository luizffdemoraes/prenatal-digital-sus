package br.com.hackathon.sus.prenatal_ia.infrastructure.config.dependency;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DependencyInjectionConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
