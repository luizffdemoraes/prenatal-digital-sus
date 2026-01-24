package br.com.hackathon.sus.prenatal_agenda.infrastructure.config.security;

import java.util.Collection;
import java.util.stream.Collectors;

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

/**
 * Configuração de segurança para o módulo de Agenda
 * Integra com o serviço de autenticação via OAuth2 Resource Server
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.jwt.issuer:http://localhost:8079}")
    private String jwtIssuer;

    @Value("${security.jwt.jwks-uri:http://localhost:8079/oauth2/jwks}")
    private String jwksUri;

    // ============================================================
    //  1. Configuração principal de segurança
    // ============================================================
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Health check - público (sem auth), para checar se a aplicação está de pé
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        
                        // Agenda do médico - Enfermeiras e Médicos: criar, alterar, excluir; todos: consultar
                        .requestMatchers(HttpMethod.POST, "/api/agendas/medico").hasAnyAuthority("ROLE_DOCTOR", "ROLE_NURSE")
                        .requestMatchers(HttpMethod.GET, "/api/agendas/medico/*").hasAnyAuthority("ROLE_DOCTOR", "ROLE_NURSE", "ROLE_PATIENT")
                        .requestMatchers(HttpMethod.PUT, "/api/agendas/medico/*").hasAnyAuthority("ROLE_DOCTOR", "ROLE_NURSE")
                        .requestMatchers(HttpMethod.DELETE, "/api/agendas/medico/*").hasAnyAuthority("ROLE_DOCTOR", "ROLE_NURSE")
                        
                        // Agendamento de consultas - gestantes e enfermeiros podem agendar
                        .requestMatchers(HttpMethod.POST, "/api/consultas/agendar").hasAnyAuthority("ROLE_PATIENT", "ROLE_NURSE")
                        
                        // Consultar horários disponíveis - Enfermeiras, Médicos, Gestantes
                        .requestMatchers(HttpMethod.GET, "/api/disponibilidade").hasAnyAuthority("ROLE_DOCTOR", "ROLE_NURSE", "ROLE_PATIENT")
                        
                        // Listar consultas por CPF (própria agenda da gestante) - Gestantes, Enfermeiras, Médicos
                        .requestMatchers(HttpMethod.GET, "/api/gestantes/consultas").hasAnyAuthority("ROLE_PATIENT", "ROLE_NURSE", "ROLE_DOCTOR")
                        // Listar consultas por gestanteId - Enfermeiras, Médicos (apoio)
                        .requestMatchers(HttpMethod.GET, "/api/gestantes/*/consultas").hasAnyAuthority("ROLE_PATIENT", "ROLE_NURSE", "ROLE_DOCTOR")
                        
                        // Cancelar consulta - gestante própria, enfermeiro ou médico
                        .requestMatchers(HttpMethod.DELETE, "/api/consultas/*/cancelar").hasAnyAuthority("ROLE_PATIENT", "ROLE_NURSE", "ROLE_DOCTOR")
                        
                        // Todas as outras requisições precisam estar autenticadas
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    // ============================================================
    //  2. Converter de autoridades a partir do JWT
    // ============================================================
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<String> authorities = jwt.getClaim("authorities");
            if (authorities == null) {
                return java.util.List.of();
            }
            return authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return converter;
    }

    // ============================================================
    //  3. JwtDecoder via JWKS
    // ============================================================
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
