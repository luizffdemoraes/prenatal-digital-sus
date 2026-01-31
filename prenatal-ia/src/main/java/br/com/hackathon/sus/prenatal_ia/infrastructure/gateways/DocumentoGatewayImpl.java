package br.com.hackathon.sus.prenatal_ia.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_ia.domain.entities.ExamRecord;
import br.com.hackathon.sus.prenatal_ia.domain.entities.VaccineRecord;
import br.com.hackathon.sus.prenatal_ia.domain.gateways.DocumentoGateway;
import br.com.hackathon.sus.prenatal_ia.infrastructure.gateways.client.DocumentApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
public class DocumentoGatewayImpl implements DocumentoGateway {

    private final WebClient webClient;

    public DocumentoGatewayImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${app.documento.base-url:http://localhost:8081}")
    private String baseUrl;

    @Override
    public List<ExamRecord> findExamsByCpf(String cpf) {
        String digits = cpf != null ? cpf.replaceAll("\\D", "") : "";
        if (digits.length() != 11) return Collections.emptyList();

        try {
            List<DocumentApiResponse> response = webClient.get()
                    .uri(baseUrl.replaceAll("/$", "") + "/api/prenatal-records/" + digits + "/documents")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<DocumentApiResponse>>() {})
                    .block();
            if (response == null) return Collections.emptyList();
            return response.stream()
                    .filter(d -> "EXAM".equals(d.tipoDocumento()) || "ULTRASOUND".equals(d.tipoDocumento()))
                    .map(this::toExamRecord)
                    .toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<VaccineRecord> findVaccinesByCpf(String cpf) {
        return Collections.emptyList();
    }

    private ExamRecord toExamRecord(DocumentApiResponse r) {
        return new ExamRecord(
                r.tipoDocumento(),
                r.dataCriacao() != null ? r.dataCriacao().toLocalDate() : null);
    }
}
