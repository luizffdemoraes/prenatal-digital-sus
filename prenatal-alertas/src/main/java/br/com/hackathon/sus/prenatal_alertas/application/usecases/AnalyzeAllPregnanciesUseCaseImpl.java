package br.com.hackathon.sus.prenatal_alertas.application.usecases;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

@Service
public class AnalyzeAllPregnanciesUseCaseImpl implements AnalyzeAllPregnanciesUseCase {

    private static final int NUCHAL_WEEKS_MIN = 12;
    private static final int NUCHAL_WEEKS_MAX = 14;
    /** Ultrassom morfológico e vacina dTpa: a partir da 20ª semana (quinto mês). Gravidez ideal 40 sem; termo 37–42 sem. */
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
        checkPendingVaccines(patient, gestationalWeeks, vaccines, alerts);
        checkNoAppointmentScheduled(appointments, alerts);
        checkCriticalExamWithRiskFactor(patient, gestationalWeeks, exams, alerts);
        checkDoctorShouldRequestExams(gestationalWeeks, exams, alerts);

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

    /**
     * Verifica vacinas pendentes conforme Calendário Nacional de Vacinação da Gestante (PNI).
     * - Antitetânica (dT/dTpa): a partir da 20ª semana – proteção contra tétano e coqueluche no RN.
     * - Hepatite B: qualquer fase – iniciar/completar esquema de 3 doses.
     * - Influenza: qualquer fase – 1 dose anual (proteção materna e do RN).
     * Gera alertas diferenciados: gestante (sua responsabilidade: procurar UBS) e médico (orientar/prescrever).
     */
    private void checkPendingVaccines(PregnantPatient patient, Integer gestationalWeeks,
                                      List<VaccineRecord> vaccines, List<PrenatalAlert> alerts) {
        // Antitetânica: PNI recomenda a partir da 20ª semana
        boolean hasTetanusVaccine = vaccines.stream()
                .anyMatch(v -> "DTPA".equalsIgnoreCase(v.getType()) || "DTAP".equalsIgnoreCase(v.getType())
                        || "DT".equalsIgnoreCase(v.getType()) || "DUPLA_ADULTO".equalsIgnoreCase(v.getType()));

        if (gestationalWeeks >= MORPHOLOGICAL_WEEKS_MIN && !hasTetanusVaccine) {
            alerts.add(new PrenatalAlert(AlertType.PENDING_VACCINE, AlertSeverity.MEDIUM,
                    "Vacina antitetânica (dT ou dTpa) pendente – recomendada a partir da 20ª semana",
                    NotificationTarget.PATIENT));
            alerts.add(new PrenatalAlert(AlertType.PENDING_VACCINE, AlertSeverity.MEDIUM,
                    "Gestante com vacina antitetânica pendente – orientar e administrar na próxima consulta",
                    NotificationTarget.PROFESSIONAL));
        }

        // Hepatite B: qualquer fase – esquema completo de 3 doses (PNI)
        long hepatitisBDoses = vaccines.stream()
                .filter(v -> "HEPATITE_B".equalsIgnoreCase(v.getType()) || "HEPATITEB".equalsIgnoreCase(v.getType())
                        || "HB".equalsIgnoreCase(v.getType()))
                .count();

        if (hepatitisBDoses < 3) {
            String msgPatient = hepatitisBDoses == 0
                    ? "Vacina Hepatite B pendente – procurar UBS para iniciar esquema de 3 doses"
                    : "Vacina Hepatite B – esquema incompleto (" + hepatitisBDoses + "/3 doses)";
            alerts.add(new PrenatalAlert(AlertType.PENDING_VACCINE, AlertSeverity.MEDIUM, msgPatient,
                    NotificationTarget.PATIENT));
            alerts.add(new PrenatalAlert(AlertType.PENDING_VACCINE, AlertSeverity.MEDIUM,
                    "Gestante com vacina Hepatite B pendente ou incompleta – orientar na próxima consulta",
                    NotificationTarget.PROFESSIONAL));
        }

        // Influenza: em qualquer fase – 1 dose anual (temporada)
        boolean hasInfluenza = vaccines.stream()
                .anyMatch(v -> "INFLUENZA".equalsIgnoreCase(v.getType()) || "GRIPE".equalsIgnoreCase(v.getType())
                        || "FLU".equalsIgnoreCase(v.getType()));

        if (!hasInfluenza) {
            alerts.add(new PrenatalAlert(AlertType.PENDING_VACCINE, AlertSeverity.MEDIUM,
                    "Vacina Influenza (gripe) pendente – procure a UBS para dose anual",
                    NotificationTarget.PATIENT));
            alerts.add(new PrenatalAlert(AlertType.PENDING_VACCINE, AlertSeverity.MEDIUM,
                    "Gestante com vacina Influenza pendente – orientar e administrar na próxima consulta",
                    NotificationTarget.PROFESSIONAL));
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

    /**
     * Orienta o médico a solicitar exames importantes caso ainda não tenham sido solicitados/realizados:
     * - Ultrassom morfológico (≥ 20ª semana)
     * - Ecocardiograma fetal (20ª–24ª semana – avaliação cardíaca fetal)
     * - Exames de sangue (hemograma, tipagem, glicemia, sorologias – rotina 1ª consulta)
     * - Exames de urina (EAS, urocultura – rotina pré-natal)
     */
    private void checkDoctorShouldRequestExams(Integer gestationalWeeks, List<ExamRecord> exams,
                                               List<PrenatalAlert> alerts) {
        // Ultrassom morfológico: médico deve solicitar se ausente (≥ 20 sem)
        if (gestationalWeeks >= MORPHOLOGICAL_WEEKS_MIN) {
            boolean hasMorphological = exams.stream()
                    .anyMatch(e -> "MORPHOLOGICAL_ULTRASOUND".equalsIgnoreCase(e.getType())
                            || "ULTRASOUND".equalsIgnoreCase(e.getType()));
            if (!hasMorphological) {
                alerts.add(new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.HIGH,
                        "Solicitar ultrassom morfológico para a gestante – exame não encontrado no prontuário",
                        NotificationTarget.PROFESSIONAL));
            }
        }

        // Ecocardiograma fetal: 20ª–24ª semana (avaliação cardíaca fetal)
        if (gestationalWeeks >= MORPHOLOGICAL_WEEKS_MIN && gestationalWeeks <= 24) {
            boolean hasEchocardiogram = exams.stream()
                    .anyMatch(e -> "ECOCARDIOGRAMA".equalsIgnoreCase(e.getType())
                            || "ECO_CARDIACA".equalsIgnoreCase(e.getType())
                            || "ECOCARDIOGRAMA_FETAL".equalsIgnoreCase(e.getType())
                            || "ECHOCARDIOGRAM".equalsIgnoreCase(e.getType())
                            || "FETAL_ECHO".equalsIgnoreCase(e.getType()));
            if (!hasEchocardiogram) {
                alerts.add(new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.MEDIUM,
                        "Solicitar ecocardiograma fetal para a gestante – janela ideal 20ª–24ª semana",
                        NotificationTarget.PROFESSIONAL));
            }
        }

        // Exames de sangue: rotina 1ª consulta (hemograma, tipagem, glicemia, sorologias)
        if (gestationalWeeks >= 8) {
            boolean hasBloodExam = exams.stream()
                    .anyMatch(e -> {
                        String t = e.getType() != null ? e.getType().toUpperCase() : "";
                        return t.contains("HEMOGRAMA") || t.contains("TIPAGEM") || t.contains("GLICEMIA")
                                || t.contains("VDRL") || t.contains("HIV") || t.contains("SOROLOGIA")
                                || t.contains("HEPATITE") || t.contains("TOXOPLASMOSE") || t.contains("SIFILIS")
                                || t.contains("EXAME_SANGUE") || t.contains("BLOOD") || t.equals("HEMOGRAMA")
                                || t.equals("TIPAGEM_SANGUINEA") || t.equals("GLICEMIA");
                    });
            if (!hasBloodExam) {
                alerts.add(new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.MEDIUM,
                        "Solicitar exames de sangue de rotina (hemograma, tipagem, glicemia, sorologias) – não encontrados no prontuário",
                        NotificationTarget.PROFESSIONAL));
            }
        }

        // Exames de urina: EAS, urocultura – rotina pré-natal
        if (gestationalWeeks >= 8) {
            boolean hasUrineExam = exams.stream()
                    .anyMatch(e -> {
                        String t = e.getType() != null ? e.getType().toUpperCase() : "";
                        return t.contains("EAS") || t.contains("URINA") || t.contains("UROCULTURA")
                                || t.contains("URINALISE") || t.equals("EXAME_URINA");
                    });
            if (!hasUrineExam) {
                alerts.add(new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.MEDIUM,
                        "Solicitar exames de urina (EAS, urocultura) – rotina pré-natal não encontrada no prontuário",
                        NotificationTarget.PROFESSIONAL));
            }
        }
    }
}
