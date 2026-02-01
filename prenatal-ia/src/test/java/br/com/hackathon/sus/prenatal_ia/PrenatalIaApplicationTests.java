package br.com.hackathon.sus.prenatal_ia;

import br.com.hackathon.sus.prenatal_ia.domain.gateways.NotificationOrchestratorGateway;
import br.com.hackathon.sus.prenatal_ia.domain.repositories.AgendaRepository;
import br.com.hackathon.sus.prenatal_ia.domain.repositories.DocumentoRepository;
import br.com.hackathon.sus.prenatal_ia.domain.repositories.ProntuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class PrenatalIaApplicationTests {

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
