package br.com.hackathon.sus.prenatal_ia.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_ia.application.dtos.responses.N8nWebhookRequest;
import br.com.hackathon.sus.prenatal_ia.application.dtos.responses.PrenatalAlertDTO;
import br.com.hackathon.sus.prenatal_ia.domain.entities.PrenatalAlert;
import br.com.hackathon.sus.prenatal_ia.domain.entities.PrenatalAnalysisResult;
import br.com.hackathon.sus.prenatal_ia.domain.gateways.NotificationOrchestratorGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
public class N8nWebhookGatewayImpl implements NotificationOrchestratorGateway {

    private final WebClient webClient;

    public N8nWebhookGatewayImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${app.n8n.webhook-url:http://localhost:5678/webhook/prenatal-alert}")
    private String webhookUrl;

    @Override
    public void sendToN8n(PrenatalAnalysisResult result) {
        N8nWebhookRequest payload = toN8nRequest(result);
        webClient.post()
                .uri(webhookUrl)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private N8nWebhookRequest toN8nRequest(PrenatalAnalysisResult result) {
        List<PrenatalAlert> alertsList = result.getAlerts() != null ? result.getAlerts() : Collections.emptyList();
        List<PrenatalAlertDTO> alerts = alertsList.stream()
                .map(a -> new PrenatalAlertDTO(
                        a.getType().name(),
                        a.getSeverity().name(),
                        a.getMessage(),
                        a.getTarget().name()))
                .toList();

        return new N8nWebhookRequest(
                result.getPatientId(),
                result.getPatientName(),
                result.getPatientEmail(),
                result.getGestationalWeeks(),
                alerts);
    }
}
