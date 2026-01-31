package br.com.hackathon.sus.prenatal_ia.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_ia.domain.entities.AppointmentSummary;
import br.com.hackathon.sus.prenatal_ia.domain.gateways.AgendaGateway;
import br.com.hackathon.sus.prenatal_ia.infrastructure.gateways.client.AppointmentApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
public class AgendaGatewayImpl implements AgendaGateway {

    private final WebClient webClient;

    public AgendaGatewayImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${app.agenda.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public List<AppointmentSummary> findAppointmentsByCpf(String cpf) {
        String digits = cpf != null ? cpf.replaceAll("\\D", "") : cpf;
        if (digits == null || digits.length() != 11) return Collections.emptyList();

        try {
            String url = baseUrl.replaceAll("/$", "") + "/api/gestantes/consultas?cpf=" + digits;
            List<AppointmentApiResponse> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AppointmentApiResponse>>() {})
                    .block();
            return response != null ? response.stream().map(this::toSummary).toList() : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private AppointmentSummary toSummary(AppointmentApiResponse r) {
        return new AppointmentSummary(r.id(), r.data(), r.horario(), r.status());
    }

}
