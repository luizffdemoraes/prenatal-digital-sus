package br.com.hackathon.sus.prenatal_alertas.domain.gateways;

import br.com.hackathon.sus.prenatal_alertas.domain.entities.PrenatalAnalysisResult;

public interface NotificationOrchestratorGateway {
    void sendNotifications(PrenatalAnalysisResult result);
}
