package br.com.hackathon.sus.prenatal_auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Configuração de teste que fornece um KeyPair RSA gerado programaticamente
 * para evitar a necessidade de arquivos PEM nos testes.
 */
@Configuration
@Profile("test")
public class TestSecurityConfig {

    @Bean
    @Primary
    public KeyPair rsaKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
