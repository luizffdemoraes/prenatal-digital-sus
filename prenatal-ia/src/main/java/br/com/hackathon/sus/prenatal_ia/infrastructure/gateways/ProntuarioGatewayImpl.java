package br.com.hackathon.sus.prenatal_ia.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_ia.domain.entities.PregnantPatient;
import br.com.hackathon.sus.prenatal_ia.domain.gateways.ProntuarioGateway;
import br.com.hackathon.sus.prenatal_ia.infrastructure.gateways.client.ProntuarioApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
public class ProntuarioGatewayImpl implements ProntuarioGateway {

    private final WebClient webClient;

    public ProntuarioGatewayImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${app.prontuario.base-url:http://localhost:8082}")
    private String baseUrl;

    @Override
    public List<PregnantPatient> findAllActivePregnancies() {
        try {
            String url = baseUrl.replaceAll("/$", "") + "/api/v1/prontuarios";
            List<ProntuarioApiResponse> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<ProntuarioApiResponse>>() {})
                    .block();
            return response != null ? response.stream().map(this::toPregnantPatient).toList() : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private PregnantPatient toPregnantPatient(ProntuarioApiResponse r) {
        return new PregnantPatient(
                r.id(),
                r.nomeCompleto(),
                r.cpf(),
                r.idadeGestacionalSemanas(),
                null);
    }
}
