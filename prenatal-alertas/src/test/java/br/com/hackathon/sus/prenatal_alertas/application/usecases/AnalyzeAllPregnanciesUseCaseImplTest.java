package br.com.hackathon.sus.prenatal_alertas.application.usecases;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import br.com.hackathon.sus.prenatal_alertas.domain.entities.AppointmentSummary;
import br.com.hackathon.sus.prenatal_alertas.domain.entities.ExamRecord;
import br.com.hackathon.sus.prenatal_alertas.domain.entities.PregnantPatient;
import br.com.hackathon.sus.prenatal_alertas.domain.entities.PrenatalAlert;
import br.com.hackathon.sus.prenatal_alertas.domain.entities.PrenatalAnalysisResult;
import br.com.hackathon.sus.prenatal_alertas.domain.entities.VaccineRecord;
import br.com.hackathon.sus.prenatal_alertas.domain.enums.AlertSeverity;
import br.com.hackathon.sus.prenatal_alertas.domain.enums.AlertType;
import br.com.hackathon.sus.prenatal_alertas.domain.enums.NotificationTarget;
import br.com.hackathon.sus.prenatal_alertas.domain.gateways.NotificationOrchestratorGateway;
import br.com.hackathon.sus.prenatal_alertas.domain.repositories.AgendaRepository;
import br.com.hackathon.sus.prenatal_alertas.domain.repositories.DocumentoRepository;
import br.com.hackathon.sus.prenatal_alertas.domain.repositories.ProntuarioRepository;

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
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
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
                new ExamRecord("MORPHOLOGICAL_ULTRASOUND", LocalDate.now()),
                new ExamRecord("HEMOGRAMA", LocalDate.now()),
                new ExamRecord("EAS", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DTPA", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        verify(notificationGateway, never()).sendNotifications(any());
    }

    @Test
    @DisplayName("gera alerta de vacina antitetânica pendente a partir da 20ª semana")
    void geraAlertaVacinaPendente() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 25, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("MORPHOLOGICAL_ULTRASOUND", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaAntitetanica = captor.getValue().getAlerts().stream()
                .filter(a -> a.getMessage().contains("antitetânica"))
                .findFirst().orElse(null);
        assertNotNull(alertaAntitetanica);
        assertEquals(AlertType.PENDING_VACCINE, alertaAntitetanica.getType());
    }

    @Test
    @DisplayName("gera alerta de consulta não agendada")
    void geraAlertaConsultaNaoAgendada() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 10, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("NUCHAL_TRANSLUCENCY", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
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
                new VaccineRecord("DTPA", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
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
    @DisplayName("aceita vacina DT como antitetânica e não gera alerta a partir da 20ª semana")
    void aceitaVacinaDTComoAntitetanica() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 25, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("MORPHOLOGICAL_ULTRASOUND", LocalDate.now()),
                new ExamRecord("HEMOGRAMA", LocalDate.now()),
                new ExamRecord("EAS", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DT", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
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

    @Test
    @DisplayName("não processa quando lista de pacientes está vazia")
    void naoProcessaQuandoListaVazia() {
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(Collections.emptyList());

        useCase.execute();

        verify(documentoRepository, never()).findExamsByCpf(any());
        verify(notificationGateway, never()).sendNotifications(any());
    }

    @Test
    @DisplayName("não gera alertas quando idade gestacional é nula")
    void naoGeraAlertasQuandoIdadeGestacionalNula() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", null, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of());
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of());
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of());

        useCase.execute();

        verify(notificationGateway, never()).sendNotifications(any());
    }

    @Test
    @DisplayName("gera alerta de translucência nucal quando 12–14 sem e exame ausente")
    void geraAlertaTranslucenciaNucal() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 13, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of());
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaNucal = captor.getValue().getAlerts().stream()
                .filter(a -> a.getMessage().contains("Translucência nucal"))
                .findFirst().orElse(null);
        assertNotNull(alertaNucal);
        assertEquals(AlertType.MISSING_EXAM, alertaNucal.getType());
        assertEquals(NotificationTarget.PATIENT, alertaNucal.getTarget());
    }

    @Test
    @DisplayName("não gera alerta de translucência nucal quando exame existe na janela 12–14 sem")
    void naoGeraAlertaTranslucenciaQuandoExameExiste() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 13, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("NUCHAL_TRANSLUCENCY", LocalDate.now()),
                new ExamRecord("HEMOGRAMA", LocalDate.now()),
                new ExamRecord("EAS", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        verify(notificationGateway, never()).sendNotifications(any());
    }

    @Test
    @DisplayName("gera alerta Hepatite B pendente quando zero doses")
    void geraAlertaHepatiteBPendenteZeroDoses() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 10, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("NUCHAL_TRANSLUCENCY", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("INFLUENZA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaHb = captor.getValue().getAlerts().stream()
                .filter(a -> a.getMessage().contains("Hepatite B") && a.getMessage().contains("pendente"))
                .findFirst().orElse(null);
        assertNotNull(alertaHb);
    }

    @Test
    @DisplayName("gera alerta Hepatite B esquema incompleto quando 1 ou 2 doses")
    void geraAlertaHepatiteBEsquemaIncompleto() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 10, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("NUCHAL_TRANSLUCENCY", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaHb = captor.getValue().getAlerts().stream()
                .filter(a -> a.getMessage().contains("Hepatite B") && a.getMessage().contains("incompleto"))
                .findFirst().orElse(null);
        assertNotNull(alertaHb);
        assertTrue(alertaHb.getMessage().contains("1/3"));
    }

    @Test
    @DisplayName("gera alerta Influenza pendente quando ausente")
    void geraAlertaInfluenzaPendente() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 10, "maria@email.com", false, List.of());
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("NUCHAL_TRANSLUCENCY", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6))));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaFlu = captor.getValue().getAlerts().stream()
                .filter(a -> a.getMessage().contains("Influenza"))
                .findFirst().orElse(null);
        assertNotNull(alertaFlu);
        assertEquals(AlertType.PENDING_VACCINE, alertaFlu.getType());
    }

    @Test
    @DisplayName("gestante de risco com exame crítico pendente gera HIGH_RISK_ATTENTION")
    void gestanteDeRiscoComExameCriticoPendente() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 25, "maria@email.com", true, List.of("HIPERTENSAO"), "Dr. João", "dr@email.com");
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of());
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DTPA", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaRisco = captor.getValue().getAlerts().stream()
                .filter(a -> a.getType() == AlertType.HIGH_RISK_ATTENTION)
                .findFirst().orElse(null);
        assertNotNull(alertaRisco);
        assertEquals(AlertSeverity.HIGH, alertaRisco.getSeverity());
        assertEquals(NotificationTarget.PROFESSIONAL, alertaRisco.getTarget());
    }

    @Test
    @DisplayName("médico recebe alerta para solicitar ecocardiograma fetal na janela 20–24 sem")
    void medicoRecebeAlertaEcocardiogramaFetal() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 22, "maria@email.com", false, List.of(), "Dr. João", "dr@email.com");
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("MORPHOLOGICAL_ULTRASOUND", LocalDate.now()),
                new ExamRecord("HEMOGRAMA", LocalDate.now()),
                new ExamRecord("EAS", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DTPA", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaEco = captor.getValue().getAlerts().stream()
                .filter(a -> a.getMessage().contains("ecocardiograma fetal"))
                .findFirst().orElse(null);
        assertNotNull(alertaEco);
        assertEquals(NotificationTarget.PROFESSIONAL, alertaEco.getTarget());
    }

    @Test
    @DisplayName("médico recebe alerta para solicitar exames de sangue quando ausentes (>= 8 sem)")
    void medicoRecebeAlertaExamesSangue() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 10, "maria@email.com", false, List.of(), "Dr. João", "dr@email.com");
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("NUCHAL_TRANSLUCENCY", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaSangue = captor.getValue().getAlerts().stream()
                .filter(a -> a.getMessage().contains("sangue") && a.getTarget() == NotificationTarget.PROFESSIONAL)
                .findFirst().orElse(null);
        assertNotNull(alertaSangue);
    }

    @Test
    @DisplayName("médico recebe alerta para solicitar exames de urina quando ausentes (>= 8 sem)")
    void medicoRecebeAlertaExamesUrina() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 10, "maria@email.com", false, List.of(), "Dr. João", "dr@email.com");
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of(
                new ExamRecord("NUCHAL_TRANSLUCENCY", LocalDate.now()),
                new ExamRecord("HEMOGRAMA", LocalDate.now())));
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("HEPATITE_B", LocalDate.now()),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(1)),
                new VaccineRecord("HEPATITE_B", LocalDate.now().plusMonths(6)),
                new VaccineRecord("INFLUENZA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAlert alertaUrina = captor.getValue().getAlerts().stream()
                .filter(a -> a.getMessage().contains("urina") && a.getTarget() == NotificationTarget.PROFESSIONAL)
                .findFirst().orElse(null);
        assertNotNull(alertaUrina);
    }

    @Test
    @DisplayName("resultado inclui doctorName e doctorEmail quando paciente tem")
    void resultadoIncluiDoctorNameEDoctorEmail() {
        PregnantPatient patient = new PregnantPatient("1", "Maria", "12345678900", 25, "maria@email.com", false, List.of(), "Dr. João", "drjoao@email.com");
        when(prontuarioRepository.findAllActivePregnancies()).thenReturn(List.of(patient));
        when(documentoRepository.findExamsByCpf("12345678900")).thenReturn(List.of());
        when(documentoRepository.findVaccinesByCpf("12345678900")).thenReturn(List.of(
                new VaccineRecord("DTPA", LocalDate.now())));
        when(agendaRepository.findAppointmentsByCpf("12345678900")).thenReturn(List.of(
                new AppointmentSummary(1L, LocalDate.now().plusDays(7), LocalTime.of(9, 0), "AGENDADA")));

        useCase.execute();

        ArgumentCaptor<PrenatalAnalysisResult> captor = ArgumentCaptor.forClass(PrenatalAnalysisResult.class);
        verify(notificationGateway).sendNotifications(captor.capture());
        PrenatalAnalysisResult result = captor.getValue();
        assertEquals("Dr. João", result.getDoctorName());
        assertEquals("drjoao@email.com", result.getDoctorEmail());
    }
}
