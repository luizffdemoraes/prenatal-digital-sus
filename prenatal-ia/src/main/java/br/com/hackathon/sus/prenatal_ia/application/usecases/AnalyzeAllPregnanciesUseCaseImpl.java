package br.com.hackathon.sus.prenatal_ia.application.usecases;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

@Service
public class AnalyzeAllPregnanciesUseCaseImpl implements AnalyzeAllPregnanciesUseCase {

    private static final int NUCHAL_WEEKS_MIN = 12;
    private static final int NUCHAL_WEEKS_MAX = 14;
    private static final int MORPHOLOGICAL_WEEKS_MIN = 20;
    private static final int GLUCOSE_CURVE_WEEKS_MIN = 28;

    private final ProntuarioRepository prontuarioRepository;
    private final AgendaRepository agendaRepository;
    private final DocumentoRepository documentoRepository;
    private final NotificationOrchestratorGateway notificationGateway;

    private static final Logger log = LoggerFactory.getLogger(AnalyzeAllPregnanciesUseCaseImpl.class);

    public AnalyzeAllPregnanciesUseCaseImpl(ProntuarioRepository prontuarioRepository, AgendaRepository agendaRepository,
            DocumentoRepository documentoRepository, NotificationOrchestratorGateway notificationGateway) {
        this.prontuarioRepository = prontuarioRepository;
        this.agendaRepository = agendaRepository;
        this.documentoRepository = documentoRepository;
        this.notificationGateway = notificationGateway;
    }

    @Override
    public void execute() {
        List<PregnantPatient> patients = prontuarioRepository.findAllActivePregnancies();
        log.info("Análise de gestações: {} paciente(s) ativo(s) no prontuário.", patients.size());
        for (PregnantPatient patient : patients) {
            processPatient(patient);
        }
    }

    private void processPatient(PregnantPatient patient) {
        String cpf = patient.getCpf() != null ? patient.getCpf().replaceAll("\\D", "") : null;
        if (cpf == null || cpf.length() != 11) {
            log.debug("Paciente {} ignorado: CPF inválido ou ausente.", patient.getId());
            return;
        }

        List<ExamRecord> exams = documentoRepository.findExamsByCpf(cpf);
        List<VaccineRecord> vaccines = documentoRepository.findVaccinesByCpf(cpf);
        List<AppointmentSummary> appointments = agendaRepository.findAppointmentsByCpf(cpf);

        List<PrenatalAlert> alerts = applyRules(patient, exams, vaccines, appointments);

        if (alerts.isEmpty()) return;

        String patientEmail = patient.getEmail();
        String doctorName = patient.getDoctorName();
        String doctorEmail = patient.getDoctorEmail();

        PrenatalAnalysisResult result = new PrenatalAnalysisResult(
                patient.getId(),
                patient.getName(),
                patientEmail,
                patient.getGestationalWeeks(),
                alerts,
                doctorName,
                doctorEmail);

        notificationGateway.sendNotifications(result);
    }

    private List<PrenatalAlert> applyRules(
            PregnantPatient patient,
            List<ExamRecord> exams,
            List<VaccineRecord> vaccines,
            List<AppointmentSummary> appointments) {

        List<PrenatalAlert> alerts = new ArrayList<>();
        Integer gestationalWeeks = patient.getGestationalWeeks();
        if (gestationalWeeks == null) return alerts;

        checkMorphologicalUltrasound(gestationalWeeks, exams, alerts);
        checkNuchalTranslucency(gestationalWeeks, exams, alerts);
        checkGlucoseCurve(gestationalWeeks, exams, alerts);
        checkPendingVaccine(vaccines, alerts);
        checkNoAppointmentScheduled(appointments, alerts);
        checkCriticalExamWithRiskFactor(patient, gestationalWeeks, exams, alerts);

        return alerts;
    }

    private void checkMorphologicalUltrasound(Integer gestationalWeeks, List<ExamRecord> exams, List<PrenatalAlert> alerts) {
        if (gestationalWeeks < MORPHOLOGICAL_WEEKS_MIN) return;

        boolean hasMorphological = exams.stream()
                .anyMatch(e -> "MORPHOLOGICAL_ULTRASOUND".equalsIgnoreCase(e.getType())
                        || "ULTRASOUND".equalsIgnoreCase(e.getType()));

        if (!hasMorphological) {
            alerts.add(new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.HIGH,
                    "Ultrassom morfológico não encontrado", NotificationTarget.PATIENT));
        }
    }

    private void checkNuchalTranslucency(Integer gestationalWeeks, List<ExamRecord> exams, List<PrenatalAlert> alerts) {
        if (gestationalWeeks < NUCHAL_WEEKS_MIN || gestationalWeeks > NUCHAL_WEEKS_MAX) return;

        boolean hasNuchal = exams.stream()
                .anyMatch(e -> "NUCHAL_TRANSLUCENCY".equalsIgnoreCase(e.getType())
                        || "ULTRASOUND".equalsIgnoreCase(e.getType()));

        if (!hasNuchal) {
            alerts.add(new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.HIGH,
                    "Translucência nucal não encontrada", NotificationTarget.PATIENT));
        }
    }

    private void checkPendingVaccine(List<VaccineRecord> vaccines, List<PrenatalAlert> alerts) {
        boolean hasTetanusVaccine = vaccines.stream()
                .anyMatch(v -> "DTPA".equalsIgnoreCase(v.getType()) || "DTAP".equalsIgnoreCase(v.getType())
                        || "DT".equalsIgnoreCase(v.getType()) || "DUPLA_ADULTO".equalsIgnoreCase(v.getType()));

        if (!hasTetanusVaccine) {
            alerts.add(new PrenatalAlert(AlertType.PENDING_VACCINE, AlertSeverity.MEDIUM,
                    "Vacina antitetânica (dT ou dTpa) pendente", NotificationTarget.PATIENT));
        }
    }

    private void checkNoAppointmentScheduled(List<AppointmentSummary> appointments, List<PrenatalAlert> alerts) {
        boolean hasFutureAppointment = appointments.stream()
                .anyMatch(a -> "AGENDADA".equalsIgnoreCase(a.getStatus()));

        if (!hasFutureAppointment) {
            alerts.add(new PrenatalAlert(AlertType.NO_APPOINTMENT_SCHEDULED, AlertSeverity.MEDIUM,
                    "Não há próxima consulta de pré-natal agendada", NotificationTarget.PROFESSIONAL));
        }
    }

    private void checkGlucoseCurve(Integer gestationalWeeks, List<ExamRecord> exams, List<PrenatalAlert> alerts) {
        if (gestationalWeeks < GLUCOSE_CURVE_WEEKS_MIN) return;

        boolean hasGlucoseCurve = exams.stream()
                .anyMatch(e -> "CURVA_GLICEMICA".equalsIgnoreCase(e.getType())
                        || "GLUCOSE_CURVE".equalsIgnoreCase(e.getType())
                        || "GLICEMIA".equalsIgnoreCase(e.getType()));

        if (!hasGlucoseCurve) {
            alerts.add(new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.HIGH,
                    "Curva glicêmica não encontrada", NotificationTarget.PATIENT));
        }
    }

    private void checkCriticalExamWithRiskFactor(PregnantPatient patient, Integer gestationalWeeks,
                                                 List<ExamRecord> exams, List<PrenatalAlert> alerts) {
        if (!patient.hasRiskFactor()) return;

        boolean hasCriticalMissing = false;

        if (gestationalWeeks >= MORPHOLOGICAL_WEEKS_MIN) {
            boolean hasMorphological = exams.stream()
                    .anyMatch(e -> "MORPHOLOGICAL_ULTRASOUND".equalsIgnoreCase(e.getType())
                            || "ULTRASOUND".equalsIgnoreCase(e.getType()));
            if (!hasMorphological) hasCriticalMissing = true;
        }
        if (!hasCriticalMissing && gestationalWeeks >= NUCHAL_WEEKS_MIN && gestationalWeeks <= NUCHAL_WEEKS_MAX) {
            boolean hasNuchal = exams.stream()
                    .anyMatch(e -> "NUCHAL_TRANSLUCENCY".equalsIgnoreCase(e.getType())
                            || "ULTRASOUND".equalsIgnoreCase(e.getType()));
            if (!hasNuchal) hasCriticalMissing = true;
        }
        if (!hasCriticalMissing && gestationalWeeks >= GLUCOSE_CURVE_WEEKS_MIN) {
            boolean hasGlucoseCurve = exams.stream()
                    .anyMatch(e -> "CURVA_GLICEMICA".equalsIgnoreCase(e.getType())
                            || "GLUCOSE_CURVE".equalsIgnoreCase(e.getType())
                            || "GLICEMIA".equalsIgnoreCase(e.getType()));
            if (!hasGlucoseCurve) hasCriticalMissing = true;
        }

        if (hasCriticalMissing) {
            alerts.add(new PrenatalAlert(AlertType.HIGH_RISK_ATTENTION, AlertSeverity.HIGH,
                    "Gestante de risco com exame crítico pendente - atenção necessária", NotificationTarget.PROFESSIONAL));
        }
    }
}
