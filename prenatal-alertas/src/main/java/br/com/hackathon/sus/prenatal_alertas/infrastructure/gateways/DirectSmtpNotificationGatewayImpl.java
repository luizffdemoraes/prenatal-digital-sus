package br.com.hackathon.sus.prenatal_alertas.infrastructure.gateways;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import br.com.hackathon.sus.prenatal_alertas.domain.entities.PrenatalAlert;
import br.com.hackathon.sus.prenatal_alertas.domain.entities.PrenatalAnalysisResult;
import br.com.hackathon.sus.prenatal_alertas.domain.enums.NotificationTarget;
import br.com.hackathon.sus.prenatal_alertas.domain.gateways.NotificationOrchestratorGateway;

/**
 * Envio de e-mail direto por SMTP: gestante recebe pendências de forma clara;
 * médico recebe alertas clínicos quando medico_email está cadastrado no prontuário.
 */
@Component
public class DirectSmtpNotificationGatewayImpl implements NotificationOrchestratorGateway {

    private static final Logger log = LoggerFactory.getLogger(DirectSmtpNotificationGatewayImpl.class);

    private final JavaMailSender mailSender;

    @Value("${app.smtp.email:}")
    private String fromEmail;

    public DirectSmtpNotificationGatewayImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendNotifications(PrenatalAnalysisResult result) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("Envio direto SMTP: SMTP_EMAIL não configurado. Defina a variável de ambiente e reinicie.");
            return;
        }

        List<PrenatalAlert> alerts = result.getAlerts() != null ? result.getAlerts() : List.of();
        List<PrenatalAlert> patientAlerts = alerts.stream().filter(a -> a.getTarget() == NotificationTarget.PATIENT).toList();
        List<PrenatalAlert> professionalAlerts = alerts.stream().filter(a -> a.getTarget() == NotificationTarget.PROFESSIONAL).toList();

        Integer weeks = result.getGestationalWeeks() != null ? result.getGestationalWeeks() : 0;

        // E-mail para a gestante: pendências de forma clara
        String toPatient = result.getPatientEmail();
        if (toPatient != null && !toPatient.isBlank() && !patientAlerts.isEmpty()) {
            String subjectPatient = String.format("Pré-natal: pendências no seu acompanhamento (%dª semana) - %s", weeks, result.getPatientName());
            String bodyPatient = buildPatientEmailBody(result.getPatientName(), weeks, patientAlerts);
            send(fromEmail, toPatient.trim(), subjectPatient, bodyPatient, "gestante", result.getPatientId());
        }

        // E-mail para o médico: alertas clínicos
        String toDoctor = result.getDoctorEmail();
        if (toDoctor != null && !toDoctor.isBlank() && !professionalAlerts.isEmpty()) {
            String subjectDoctor = String.format("Alerta pré-natal - %s (%dª semana) - ID %s", result.getPatientName(), weeks, result.getPatientId());
            String bodyDoctor = buildDoctorEmailBody(result.getPatientName(), weeks, result.getPatientId(), professionalAlerts);
            send(fromEmail, toDoctor.trim(), subjectDoctor, bodyDoctor, "médico", result.getPatientId());
        }
    }

    private String buildPatientEmailBody(String patientName, int weeks, List<PrenatalAlert> patientAlerts) {
        StringBuilder sb = new StringBuilder();
        sb.append("Olá ").append(patientName).append(",\n\n");
        sb.append("Identificamos as seguintes pendências no seu acompanhamento pré-natal (").append(weeks).append("ª semana):\n\n");
        for (PrenatalAlert a : patientAlerts) {
            sb.append("• ").append(a.getMessage()).append("\n");
        }
        sb.append("\nRecomendação: procure sua unidade de saúde para agendar os procedimentos.\n\n");
        sb.append("— Prenatal Digital SUS");
        return sb.toString();
    }

    private String buildDoctorEmailBody(String patientName, int weeks, String patientId, List<PrenatalAlert> professionalAlerts) {
        StringBuilder sb = new StringBuilder();
        sb.append("Alerta clínico – gestante ").append(patientName).append(" (").append(weeks).append("ª semana), ID: ").append(patientId).append("\n\n");
        sb.append("Pendências que requerem atenção:\n\n");
        for (PrenatalAlert a : professionalAlerts) {
            sb.append("• ").append(a.getMessage()).append(" [").append(a.getSeverity() != null ? a.getSeverity().name() : "—").append("]\n");
        }
        sb.append("\nAção recomendada: verificar prontuário e contatar a gestante.\n\n");
        sb.append("— Prenatal Digital SUS");
        return sb.toString();
    }

    private void send(String from, String to, String subject, String body, String destinatario, String patientId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            String masked = to.length() > 4 && to.contains("@") ? to.substring(0, 2) + "***@" + to.substring(to.indexOf('@') + 1) : "***";
            log.info("E-mail enviado diretamente (SMTP) para {} ({}, patientId={}).", masked, destinatario, patientId);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail diretamente (SMTP) para {} ({}): {}", to, destinatario, e.getMessage());
        }
    }
}
