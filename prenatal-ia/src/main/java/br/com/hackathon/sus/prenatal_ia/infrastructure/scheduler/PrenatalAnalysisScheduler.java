package br.com.hackathon.sus.prenatal_ia.infrastructure.scheduler;

import br.com.hackathon.sus.prenatal_ia.application.usecases.AnalyzeAllPregnanciesUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PrenatalAnalysisScheduler {

    private final AnalyzeAllPregnanciesUseCase analyzeAllPregnanciesUseCase;

    public PrenatalAnalysisScheduler(AnalyzeAllPregnanciesUseCase analyzeAllPregnanciesUseCase) {
        this.analyzeAllPregnanciesUseCase = analyzeAllPregnanciesUseCase;
    }

    @Scheduled(cron = "0 0 */12 * * *")
    public void runAnalysis() {
        analyzeAllPregnanciesUseCase.execute();
    }
}
