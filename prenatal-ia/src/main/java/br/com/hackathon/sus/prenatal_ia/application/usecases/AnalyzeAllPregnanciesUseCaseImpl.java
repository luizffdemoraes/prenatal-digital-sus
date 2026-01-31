package br.com.hackathon.sus.prenatal_ia.application.usecases;

import br.com.hackathon.sus.prenatal_ia.domain.entities.*;
import br.com.hackathon.sus.prenatal_ia.domain.enums.AlertSeverity;
import br.com.hackathon.sus.prenatal_ia.domain.enums.AlertType;
import br.com.hackathon.sus.prenatal_ia.domain.enums.NotificationTarget;
import br.com.hackathon.sus.prenatal_ia.domain.gateways.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyzeAllPregnanciesUseCaseImpl implements AnalyzeAllPregnanciesUseCase {

    private static final int NUCHAL_WEEKS_MIN = 12;
    private static final int NUCHAL_WEEKS_MAX = 14;
    private static final int MORPHOLOGICAL_WEEKS_MIN = 20;

    private final ProntuarioGateway prontuarioGateway;
    private final AgendaGateway agendaGateway;
    private final DocumentoGateway documentoGateway;
    private final AuthGateway authGateway;
    private final NotificationOrchestratorGateway notificationGateway;

    public AnalyzeAllPregnanciesUseCaseImpl(ProntuarioGateway prontuarioGateway, AgendaGateway agendaGateway,
            DocumentoGateway documentoGateway, AuthGateway authGateway,
            NotificationOrchestratorGateway notificationGateway) {
        this.prontuarioGateway = prontuarioGateway;
        this.agendaGateway = agendaGateway;
        this.documentoGateway = documentoGateway;
        this.authGateway = authGateway;
        this.notificationGateway = notificationGateway;
    }

    @Override
    public void execute() {
        List<PregnantPatient> patients = prontuarioGateway.findAllActivePregnancies();
        for (PregnantPatient patient : patients) {
            processPatient(patient);
        }
    }

    private void processPatient(PregnantPatient patient) {
        String cpf = patient.getCpf() != null ? patient.getCpf().replaceAll("\\D", "") : null;
        if (cpf == null || cpf.length() != 11) return;

        List<ExamRecord> exams = documentoGateway.findExamsByCpf(cpf);
        List<VaccineRecord> vaccines = documentoGateway.findVaccinesByCpf(cpf);
        List<AppointmentSummary> appointments = agendaGateway.findAppointmentsByCpf(cpf);

        List<PrenatalAlert> alerts = applyRules(
                patient.getGestationalWeeks(),
                exams,
                vaccines,
                appointments
        );

        String patientEmail = authGateway.findEmailByCpf(cpf).orElse(patient.getEmail());

        PrenatalAnalysisResult result = new PrenatalAnalysisResult(
                patient.getId(),
                patient.getName(),
                patientEmail,
                patient.getGestationalWeeks(),
                alerts);

        notificationGateway.sendToN8n(result);
    }

    private List<PrenatalAlert> applyRules(
            Integer gestationalWeeks,
            List<ExamRecord> exams,
            List<VaccineRecord> vaccines,
            List<AppointmentSummary> appointments) {

        List<PrenatalAlert> alerts = new ArrayList<>();

        if (gestationalWeeks == null) return alerts;

        checkMorphologicalUltrasound(gestationalWeeks, exams, alerts);
        checkNuchalTranslucency(gestationalWeeks, exams, alerts);
        checkPendingVaccine(gestationalWeeks, vaccines, alerts);
        checkNoAppointmentScheduled(appointments, alerts);

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

    private void checkPendingVaccine(Integer gestationalWeeks, List<VaccineRecord> vaccines, List<PrenatalAlert> alerts) {
        if (gestationalWeeks < 20) return;

        boolean hasDtpa = vaccines.stream()
                .anyMatch(v -> "DTPA".equalsIgnoreCase(v.getType()) || "DTAP".equalsIgnoreCase(v.getType()));

        if (!hasDtpa) {
            alerts.add(new PrenatalAlert(AlertType.PENDING_VACCINE, AlertSeverity.MEDIUM,
                    "Vacina dTpa pendente (recomendada entre 27-36 semanas)", NotificationTarget.PATIENT));
        }
    }

    private void checkNoAppointmentScheduled(List<AppointmentSummary> appointments, List<PrenatalAlert> alerts) {
        boolean hasFutureAppointment = appointments.stream()
                .anyMatch(a -> "AGENDADA".equalsIgnoreCase(a.getStatus()));

        if (!hasFutureAppointment) {
            alerts.add(new PrenatalAlert(AlertType.NO_APPOINTMENT_SCHEDULED, AlertSeverity.MEDIUM,
                    "Não há próxima consulta de pré-natal agendada", NotificationTarget.PATIENT));
        }
    }
}
