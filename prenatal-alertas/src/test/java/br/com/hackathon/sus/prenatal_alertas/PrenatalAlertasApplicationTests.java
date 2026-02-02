package br.com.hackathon.sus.prenatal_alertas;

import br.com.hackathon.sus.prenatal_alertas.domain.gateways.NotificationOrchestratorGateway;
import br.com.hackathon.sus.prenatal_alertas.domain.repositories.AgendaRepository;
import br.com.hackathon.sus.prenatal_alertas.domain.repositories.DocumentoRepository;
import br.com.hackathon.sus.prenatal_alertas.domain.repositories.ProntuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class PrenatalAlertasApplicationTests {

    @MockBean
    private ProntuarioRepository prontuarioRepository;

    @MockBean
    private AgendaRepository agendaRepository;

    @MockBean
    private DocumentoRepository documentoRepository;

    @MockBean
    private NotificationOrchestratorGateway notificationGateway;

    @Test
    void contextLoads() {
    }
}
