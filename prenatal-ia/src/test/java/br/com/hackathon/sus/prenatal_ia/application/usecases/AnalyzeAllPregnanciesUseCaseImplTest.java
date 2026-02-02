package br.com.hackathon.sus.prenatal_ia.application.usecases;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.hackathon.sus.prenatal_ia.domain.entities.AppointmentSummary;
import br.com.hackathon.sus.prenatal_ia.domain.entities.ExamRecord;
import br.com.hackathon.sus.prenatal_ia.domain.entities.PregnantPatient;
import br.com.hackathon.sus.prenatal_ia.domain.entities.PrenatalAlert;
import br.com.hackathon.sus.prenatal_ia.domain.entities.PrenatalAnalysisResult;
import br.com.hackathon.sus.prenatal_ia.domain.entities.VaccineRecord;
import br.com.hackathon.sus.prenatal_ia.domain.enums.AlertSeverity;
import br.com.hackathon.sus.prenatal_ia.domain.enums.AlertType;
import br.com.hackathon.sus.prenatal_ia.domain.enums.NotificationTarget;
import br.com.hackathon.sus.prenatal_ia.domain.gateways.NotificationOrchestratorGateway;
import br.com.hackathon.sus.prenatal_ia.domain.repositories.AgendaRepository;
import br.com.hackathon.sus.prenatal_ia.domain.repositories.DocumentoRepository;
import br.com.hackathon.sus.prenatal_ia.domain.repositories.ProntuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AnalyzeAllPregnanciesUseCaseImpl")
class AnalyzeAllPregnanciesUseCaseImplTest {

    @Mock
    private ProntuarioRepository prontuarioRepository;

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private NotificationOrchestratorGateway notificationGateway;

    @InjectMocks
    private AnalyzeAllPregnanciesUseCaseImpl useCase;

    private PregnantPatient patientComCpfValido;

    @BeforeEach
    void setUp() {
        patientComCpfValido = new PregnantPatient(
                "1", "Maria", "12345678900", 25, "maria@email.com",
                false, List.of());
    }

    @Test
    @DisplayName("não processa paciente com CPF inválido")
    void naoProcessaPacienteComCpfInvalido() {
        when(prontuarioRepository.findAllActivePregnancies())
                .thenReturn(List.of(
                        new PregnantPatient("1", "Maria", null, 25, null, false, List.of()),
                        new PregnantPatient("2", "Joana", "123", 20, null, false, List.of())));

        useCase.execute();

        verify(documentoRepository, never()).findExamsByCpf(any());
        verify(notificationGateway, never()).sendNotifications(any());
    }

    @Test
    @DisplayName("não envia notificações quando não há alertas")
    void naoEnviaQuandoSemAlertas() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 5, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of());
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DTPA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        verify(notificationGateway, never()).sendNotifications(any());
    }

    @Test
    @DisplayName("gera alerta de ultrassom morfológico pendente para gestação >= 20 semanas")
    void geraAlertaUltrassomMorfologico() {
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patientComCpfValido));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of());
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DTPA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAnalysisResult result = captor.getValue();

        assertEquals("1", result.getPatientId());
        assertEquals("Maria", result.getPatientName());
        assertEquals("maria@email.com", result.getPatientEmail());
        assertEquals(25, result.getGestationalWeeks());

        PrenatalAlert alertaMorfologico = result.getAlerts().stream()
                .filter(a -> a.getMessage().contains("morfológico"))
                .findFirst().orElse(null);
        assertNotNull(alertaMorfologico);
        assertEquals(AlertType.MISSING_EXAM, alertaMorfologico.getType());
        assertEquals(AlertSeverity.HIGH, alertaMorfologico.getSeverity());
        assertEquals(NotificationTarget.PATIENT, alertaMorfologico.getTarget());
    }

    @Test
    @DisplayName("não gera alerta de ultrassom morfológico quando exame existe")
    void naoGeraAlertaUltrassomQuandoExameExiste() {
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patientComCpfValido));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("MORPHOLOGICAL_ULTRASOUND", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DTPA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        verify(notificationGateway, never()).sendNotifications(any());
    }

    @Test
    @DisplayName("gera alerta de vacina antitetânica pendente")
    void geraAlertaVacinaPendente() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 15, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("NUCHAL_TRANSLUCENCY", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(Collections.emptyList());
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaVacina = captor.getValue().getAlerts().stream()
                .filter(a -> a.getMessage().contains("antitetânica"))
                .findFirst().orElse(null);
        assertNotNull(alertaVacina);
        assertEquals(AlertType.PENDING_VACCINE, alertaVacina.getType());
    }

    @Test
    @DisplayName("gera alerta de consulta não agendada")
    void geraAlertaConsultaNaoAgendada() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 10, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of());
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DTPA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(Collections.emptyList());

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaConsulta = captor.getValue().getAlerts().stream()
                .filter(a -> a.getMessage().contains("consulta"))
                .findFirst().orElse(null);
        assertNotNull(alertaConsulta);
        assertEquals(AlertType.NO_APPOINTMENT_SCHEDULED, alertaConsulta.getType());
        assertEquals(NotificationTarget.PROFESSIONAL, alertaConsulta.getTarget());
    }

    @Test
    @DisplayName("gera alerta de curva glicêmica para gestação >= 28 semanas")
    void geraAlertaCurvaGlicemica() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 30, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("MORPHOLOGICAL_ULTRASOUND", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DTPA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaCurva = captor.getValue().getAlerts().stream()
                .filter(a -> a.getMessage().contains("glicêmica"))
                .findFirst().orElse(null);
        assertNotNull(alertaCurva);
        assertEquals(AlertType.MISSING_EXAM, alertaCurva.getType());
    }

    @Test
    @DisplayName("aceita vacina DT como antitetânica")
    void aceitaVacinaDTComoAntitetanica() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 10, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("NUCHAL_TRANSLUCENCY", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DT", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        verify(notificationGateway, never()).sendNotifications(any());
    }

    @Test
    @DisplayName("normaliza CPF com formatação")
    void normalizaCpfComFormatacao() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "123.456.789-00", 25, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of());
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DTPA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        verify(documentoRepository).findExamsByCpf("12345678900");
        verify(documentoRepository).findVaccinesByCpf("12345678900");
        verify(agendaRepository).findAppointmentsByCpf("12345678900");
    }
}
