package br.com.hackathon.sus.prenatal_alertas.infrastructure.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.hackathon.sus.prenatal_alertas.application.usecases.AnalyzeAllPregnanciesUseCase;

@Component
public class PrenatalAnalysisScheduler {

    private static final Logger log = LoggerFactory.getLogger(PrenatalAnalysisScheduler.class);

    private final AnalyzeAllPregnanciesUseCase analyzeAllPregnanciesUseCase;

    public PrenatalAnalysisScheduler(AnalyzeAllPregnanciesUseCase analyzeAllPregnanciesUseCase) {
        this.analyzeAllPregnanciesUseCase = analyzeAllPregnanciesUseCase;
    }

    @Scheduled(cron = "0 */05 * * * *")  // a cada 10 minutos (testes); produção: "0 0 */12 * * *" (a cada 12h)
    public void runAnalysis() {
        log.info("Job de análise de gestações iniciado.");
        analyzeAllPregnanciesUseCase.execute();
    }
}
