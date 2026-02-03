package br.com.hackathon.sus.prenatal_alertas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import br.com.hackathon.sus.prenatal_alertas.domain.gateways.NotificationOrchestratorGateway;
import br.com.hackathon.sus.prenatal_alertas.domain.repositories.AgendaRepository;
import br.com.hackathon.sus.prenatal_alertas.domain.repositories.DocumentoRepository;
import br.com.hackathon.sus.prenatal_alertas.domain.repositories.ProntuarioRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class PrenatalAlertasApplicationTests {

    @MockitoBean
    private ProntuarioRepository prontuarioRepository;

    @MockitoBean
    private AgendaRepository agendaRepository;

    @MockitoBean
    private DocumentoRepository documentoRepository;

    @MockitoBean
    private NotificationOrchestratorGateway notificationGateway;

    @Test
    void contextLoads() {
    }
}
