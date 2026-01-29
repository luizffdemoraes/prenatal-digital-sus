package br.com.hackathon.sus.prenatal_documento.infrastructure.config.dependency;

import br.com.hackathon.sus.prenatal_documento.application.usecases.*;
import br.com.hackathon.sus.prenatal_documento.domain.gateways.StorageGateway;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.MedicalDocumentRepository;
import br.com.hackathon.sus.prenatal_documento.infrastructure.gateways.S3StorageGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class DependencyInjectionConfig {

    @Bean
    public StorageGateway storageGateway(S3Client s3Client,
                                         @Value("${aws.s3.bucket-name:prenatal-documents}") String bucketName) {
        return new S3StorageGateway(s3Client, bucketName);
    }

    @Bean
    public UploadDocumentUseCase uploadDocumentUseCase(MedicalDocumentRepository repository,
                                                       StorageGateway storageGateway) {
        return new UploadDocumentUseCaseImpl(repository, storageGateway);
    }

    @Bean
    public DownloadDocumentUseCase downloadDocumentUseCase(MedicalDocumentRepository repository,
                                                           StorageGateway storageGateway) {
        return new DownloadDocumentUseCaseImpl(repository, storageGateway);
    }

    @Bean
    public DeleteDocumentUseCase deleteDocumentUseCase(MedicalDocumentRepository repository,
                                                       StorageGateway storageGateway) {
        return new DeleteDocumentUseCaseImpl(repository, storageGateway);
    }

    @Bean
    public ListDocumentsUseCase listDocumentsUseCase(MedicalDocumentRepository repository) {
        return new ListDocumentsUseCaseImpl(repository);
    }

    @Bean
    public InactivateDocumentUseCase inactivateDocumentUseCase(MedicalDocumentRepository repository) {
        return new InactivateDocumentUseCaseImpl(repository);
    }

    @Bean
    public RequestDeleteDocumentUseCase requestDeleteDocumentUseCase(InactivateDocumentUseCase inactivateDocumentUseCase) {
        return new RequestDeleteDocumentUseCaseImpl(inactivateDocumentUseCase);
    }
}

