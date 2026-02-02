package br.com.hackathon.sus.prenatal_alertas.infrastructure.scheduler;

import br.com.hackathon.sus.prenatal_alertas.application.usecases.AnalyzeAllPregnanciesUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PrenatalAnalysisScheduler")
class PrenatalAnalysisSchedulerTest {

    @Mock
    private AnalyzeAllPregnanciesUseCase analyzeAllPregnanciesUseCase;

    @InjectMocks
    private PrenatalAnalysisScheduler scheduler;

    @Test
    @DisplayName("runAnalysis chama o use case")
    void runAnalysisChamaUseCase() {
        scheduler.runAnalysis();
        verify(analyzeAllPregnanciesUseCase, times(1)).execute();
    }
}
