package com.hackathon.sus.prenatal_prontuario.infrastructure.config.dependency;

import com.hackathon.sus.prenatal_prontuario.application.usecases.*;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordHistoryGateway;
import com.hackathon.sus.prenatal_prontuario.infrastructure.gateways.MedicalRecordGatewayImpl;
import com.hackathon.sus.prenatal_prontuario.infrastructure.gateways.MedicalRecordHistoryGatewayImpl;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.repository.MedicalRecordHistoryRepository;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.repository.MedicalRecordRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyInjectionConfig {

    @Bean
    public MedicalRecordGateway medicalRecordGateway(MedicalRecordRepository repository) {
        return new MedicalRecordGatewayImpl(repository);
    }

    @Bean
    public MedicalRecordHistoryGateway medicalRecordHistoryGateway(MedicalRecordHistoryRepository repository) {
        return new MedicalRecordHistoryGatewayImpl(repository);
    }

    @Bean
    public CreateMedicalRecordUseCase createMedicalRecordUseCase(MedicalRecordGateway medicalRecordGateway,
                                                                 MedicalRecordHistoryGateway historyGateway) {
        return new CreateMedicalRecordUseCaseImp(medicalRecordGateway, historyGateway);
    }

    @Bean
    public FindMedicalRecordByIdUseCase findMedicalRecordByIdUseCase(MedicalRecordGateway medicalRecordGateway) {
        return new FindMedicalRecordByIdUseCaseImp(medicalRecordGateway);
    }

    @Bean
    public FindMedicalRecordByCpfUseCase findMedicalRecordByCpfUseCase(MedicalRecordGateway medicalRecordGateway) {
        return new FindMedicalRecordByCpfUseCaseImp(medicalRecordGateway);
    }

    @Bean
    public UpdateMedicalRecordUseCase updateMedicalRecordUseCase(MedicalRecordGateway medicalRecordGateway,
                                                                 MedicalRecordHistoryGateway historyGateway) {
        return new UpdateMedicalRecordUseCaseImp(medicalRecordGateway, historyGateway);
    }

    @Bean
    public UpdateRiskFactorsUseCase updateRiskFactorsUseCase(MedicalRecordGateway medicalRecordGateway,
                                                             MedicalRecordHistoryGateway historyGateway) {
        return new UpdateRiskFactorsUseCaseImp(medicalRecordGateway, historyGateway);
    }

    @Bean
    public FindMedicalRecordHistoryUseCase findMedicalRecordHistoryUseCase(MedicalRecordHistoryGateway historyGateway,
                                                                           MedicalRecordGateway medicalRecordGateway) {
        return new FindMedicalRecordHistoryUseCaseImp(historyGateway, medicalRecordGateway);
    }
}
