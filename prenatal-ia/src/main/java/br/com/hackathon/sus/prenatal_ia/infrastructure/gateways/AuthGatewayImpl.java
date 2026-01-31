package br.com.hackathon.sus.prenatal_ia.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_ia.domain.gateways.AuthGateway;
import br.com.hackathon.sus.prenatal_ia.infrastructure.gateways.client.UserApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Component
public class AuthGatewayImpl implements AuthGateway {

    private final WebClient webClient;

    @Value("${app.auth.base-url:http://localhost:8079}")
    private String baseUrl;

    public AuthGatewayImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Optional<String> findEmailByCpf(String cpf) {
        String digits = cpf != null ? cpf.replaceAll("\\D", "") : "";
        if (digits.length() != 11) return Optional.empty();

        try {
            String url = baseUrl.replaceAll("/$", "") + "/v1/usuarios/cpf/" + digits;
            UserApiResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(UserApiResponse.class)
                    .block();
            return response != null && response.email() != null && !response.email().isBlank()
                    ? Optional.of(response.email())
                    : Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
