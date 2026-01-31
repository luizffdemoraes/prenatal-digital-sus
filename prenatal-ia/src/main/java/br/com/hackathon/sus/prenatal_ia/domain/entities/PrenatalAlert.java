package br.com.hackathon.sus.prenatal_ia.domain.entities;

import br.com.hackathon.sus.prenatal_ia.domain.enums.AlertSeverity;
import br.com.hackathon.sus.prenatal_ia.domain.enums.AlertType;
import br.com.hackathon.sus.prenatal_ia.domain.enums.NotificationTarget;

public class PrenatalAlert {
    private AlertType type;
    private AlertSeverity severity;
    private String message;
    private NotificationTarget target;

    public PrenatalAlert() {
    }

    public PrenatalAlert(AlertType type, AlertSeverity severity, String message, NotificationTarget target) {
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.target = target;
    }

    public AlertType getType() { return type; }
    public void setType(AlertType type) { this.type = type; }
    public AlertSeverity getSeverity() { return severity; }
    public void setSeverity(AlertSeverity severity) { this.severity = severity; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public NotificationTarget getTarget() { return target; }
    public void setTarget(NotificationTarget target) { this.target = target; }
}
