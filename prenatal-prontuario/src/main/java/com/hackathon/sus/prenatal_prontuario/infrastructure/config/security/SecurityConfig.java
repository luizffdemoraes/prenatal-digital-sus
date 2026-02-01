package com.hackathon.sus.prenatal_prontuario.infrastructure.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Segurança: JWT obrigatório.
 * Roles: ROLE_GESTANTE, ROLE_ENFERMEIRO, ROLE_MEDICO; ROLE_NURSE, ROLE_DOCTOR, ROLE_PATIENT (prenatal-auth).
 * - POST, PUT, PATCH: ENFERMEIRO, MEDICO, NURSE, DOCTOR.
 * - GET /cpf/{cpf}, GET /cpf/{cpf}/historico: autenticados; gestante/paciente só o próprio (claim cpf).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.jwt.issuer:http://localhost:8079}")
    private String jwtIssuer;

    @Value("${security.jwt.jwks-uri:http://localhost:8079/oauth2/jwks}")
    private String jwksUri;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // Criar prontuário: ENFERMEIRO, MEDICO ou NURSE, DOCTOR (compatível com prenatal-auth)
                        .requestMatchers(HttpMethod.POST, "/api/v1/prontuarios").hasAnyAuthority("ROLE_ENFERMEIRO", "ROLE_MEDICO", "ROLE_NURSE", "ROLE_DOCTOR")
                        // Atualizar dados clínicos: ENFERMEIRO, MEDICO ou NURSE, DOCTOR
                        .requestMatchers(HttpMethod.PUT, "/api/v1/prontuarios/cpf/*").hasAnyAuthority("ROLE_ENFERMEIRO", "ROLE_MEDICO", "ROLE_NURSE", "ROLE_DOCTOR")
                        // Atualizar fatores de risco: ENFERMEIRO, MEDICO ou NURSE, DOCTOR
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/prontuarios/cpf/*/fatores-risco").hasAnyAuthority("ROLE_ENFERMEIRO", "ROLE_MEDICO", "ROLE_NURSE", "ROLE_DOCTOR")
                        // Buscar por CPF e histórico: autenticados (gestante só o próprio, via claim cpf no controller)
                        .requestMatchers(HttpMethod.GET, "/api/v1/prontuarios/cpf/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/prontuarios/cpf/*/historico").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(new TolerantBearerTokenResolver())
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<String> authorities = jwt.getClaim("authorities");
            if (authorities == null) return java.util.List.of();
            return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        });
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(jwksUri)
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(jwtIssuer));
        return decoder;
    }
}
