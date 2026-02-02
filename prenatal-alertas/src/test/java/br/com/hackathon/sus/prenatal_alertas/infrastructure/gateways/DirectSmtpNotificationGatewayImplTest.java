package br.com.hackathon.sus.prenatal_alertas.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_alertas.domain.entities.PrenatalAlert;
import br.com.hackathon.sus.prenatal_alertas.domain.entities.PrenatalAnalysisResult;
import br.com.hackathon.sus.prenatal_alertas.domain.enums.AlertSeverity;
import br.com.hackathon.sus.prenatal_alertas.domain.enums.AlertType;
import br.com.hackathon.sus.prenatal_alertas.domain.enums.NotificationTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DirectSmtpNotificationGatewayImpl")
class DirectSmtpNotificationGatewayImplTest {

    @Mock
    private JavaMailSender mailSender;

    private DirectSmtpNotificationGatewayImpl gateway;

    private static final String FROM_EMAIL = "noreply@prenatal.sus.br";

    @BeforeEach
    void setUp() {
        gateway = new DirectSmtpNotificationGatewayImpl(mailSender);
        ReflectionTestUtils.setField(gateway, "fromEmail", FROM_EMAIL);
    }

    @Test
    @DisplayName("não envia quando fromEmail não está configurado (null)")
    void naoEnviaQuandoFromEmailNull() {
        ReflectionTestUtils.setField(gateway, "fromEmail", (String) null);
        PrenatalAnalysisResult result = new PrenatalAnalysisResult("1", "Maria", "maria@email.com", 20,
                List.of(new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.HIGH, "Ultrassom pendente", NotificationTarget.PATIENT)));

        gateway.sendNotifications(result);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("não envia quando fromEmail está em branco")
    void naoEnviaQuandoFromEmailEmBranco() {
        ReflectionTestUtils.setField(gateway, "fromEmail", "   ");
        PrenatalAnalysisResult result = new PrenatalAnalysisResult("1", "Maria", "maria@email.com", 20,
                List.of(new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.HIGH, "Ultrassom pendente", NotificationTarget.PATIENT)));

        gateway.sendNotifications(result);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("envia e-mail para gestante quando há alertas PATIENT")
    void enviaEmailParaGestanteQuandoHaAlertasPatient() {
        PrenatalAlert alerta = new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.HIGH, "Ultrassom morfológico pendente", NotificationTarget.PATIENT);
        PrenatalAnalysisResult result = new PrenatalAnalysisResult("1", "Maria", "maria@email.com", 25, List.of(alerta));

        gateway.sendNotifications(result);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();
        assertEquals(FROM_EMAIL, msg.getFrom());
        assertEquals("maria@email.com", msg.getTo()[0]);
        assertTrue(msg.getSubject().contains("Pré-natal: pendências"));
        assertTrue(msg.getSubject().contains("25"));
        assertTrue(msg.getSubject().contains("Maria"));
        assertTrue(msg.getText().contains("Olá Maria"));
        assertTrue(msg.getText().contains("Ultrassom morfológico pendente"));
        assertTrue(msg.getText().contains("Prenatal Digital SUS"));
    }

    @Test
    @DisplayName("não envia para gestante quando email da gestante está em branco")
    void naoEnviaParaGestanteQuandoEmailEmBranco() {
        PrenatalAlert alerta = new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.HIGH, "Pendência", NotificationTarget.PATIENT);
        PrenatalAnalysisResult result = new PrenatalAnalysisResult("1", "Maria", null, 25, List.of(alerta));

        gateway.sendNotifications(result);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("não envia para gestante quando só há alertas PROFESSIONAL")
    void naoEnviaParaGestanteQuandoSoAlertasProfessional() {
        PrenatalAlert alerta = new PrenatalAlert(AlertType.NO_APPOINTMENT_SCHEDULED, AlertSeverity.MEDIUM, "Consulta não agendada", NotificationTarget.PROFESSIONAL);
        PrenatalAnalysisResult result = new PrenatalAnalysisResult("1", "Maria", "maria@email.com", 25, List.of(alerta), "Dr. João", "dr@email.com");

        gateway.sendNotifications(result);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        assertEquals("dr@email.com", captor.getValue().getTo()[0]);
    }

    @Test
    @DisplayName("envia e-mail para médico quando há alertas PROFESSIONAL e doctorEmail preenchido")
    void enviaEmailParaMedicoQuandoHaAlertasProfessional() {
        PrenatalAlert alerta = new PrenatalAlert(AlertType.NO_APPOINTMENT_SCHEDULED, AlertSeverity.MEDIUM, "Consulta não agendada", NotificationTarget.PROFESSIONAL);
        PrenatalAnalysisResult result = new PrenatalAnalysisResult("1", "Maria", "maria@email.com", 20, List.of(alerta), "Dr. João", "drjoao@hospital.br");

        gateway.sendNotifications(result);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();
        assertEquals("drjoao@hospital.br", msg.getTo()[0]);
        assertTrue(msg.getSubject().contains("Alerta pré-natal"));
        assertTrue(msg.getSubject().contains("Maria"));
        assertTrue(msg.getSubject().contains("1"));
        assertTrue(msg.getText().contains("Alerta clínico"));
        assertTrue(msg.getText().contains("Consulta não agendada"));
        assertTrue(msg.getText().contains("MEDIUM"));
    }

    @Test
    @DisplayName("envia dois e-mails quando há alertas para gestante e para médico")
    void enviaDoisEmailsQuandoHaAlertasParaGestanteEMedico() {
        PrenatalAlert alertaPatient = new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.HIGH, "Exame pendente", NotificationTarget.PATIENT);
        PrenatalAlert alertaProf = new PrenatalAlert(AlertType.NO_APPOINTMENT_SCHEDULED, AlertSeverity.MEDIUM, "Consulta não agendada", NotificationTarget.PROFESSIONAL);
        PrenatalAnalysisResult result = new PrenatalAnalysisResult("1", "Maria", "maria@email.com", 20,
                List.of(alertaPatient, alertaProf), "Dr. João", "dr@email.com");

        gateway.sendNotifications(result);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(2)).send(captor.capture());
        List<SimpleMailMessage> messages = captor.getAllValues();
        assertEquals("maria@email.com", messages.get(0).getTo()[0]);
        assertEquals("dr@email.com", messages.get(1).getTo()[0]);
    }

    @Test
    @DisplayName("trata alertas null como lista vazia")
    void trataAlertasNullComoListaVazia() {
        PrenatalAnalysisResult result = new PrenatalAnalysisResult("1", "Maria", "maria@email.com", 20, null);

        gateway.sendNotifications(result);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("alerta com severity null exibe travessão no corpo do e-mail do médico")
    void alertaComSeverityNullExibeTravessao() {
        PrenatalAlert alerta = new PrenatalAlert(AlertType.NO_APPOINTMENT_SCHEDULED, null, "Consulta não agendada", NotificationTarget.PROFESSIONAL);
        PrenatalAnalysisResult result = new PrenatalAnalysisResult("1", "Maria", null, 20, List.of(alerta), null, "dr@email.com");

        gateway.sendNotifications(result);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertTrue(captor.getValue().getText().contains("—"));
    }

    @Test
    @DisplayName("não relança exceção quando mailSender.send falha (apenas loga)")
    void naoRelancaExceçãoQuandoSendFalha() {
        doThrow(new RuntimeException("SMTP indisponível")).when(mailSender).send(any(SimpleMailMessage.class));
        PrenatalAlert alerta = new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.HIGH, "Pendência", NotificationTarget.PATIENT);
        PrenatalAnalysisResult result = new PrenatalAnalysisResult("1", "Maria", "maria@email.com", 20, List.of(alerta));

        assertDoesNotThrow(() -> gateway.sendNotifications(result));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
