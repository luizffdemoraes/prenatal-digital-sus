package br.com.hackathon.sus.prenatal_ia.domain.gateways;

import br.com.hackathon.sus.prenatal_ia.domain.entities.PrenatalAnalysisResult;

public interface NotificationOrchestratorGateway {
    void sendNotifications(PrenatalAnalysisResult result);
}
