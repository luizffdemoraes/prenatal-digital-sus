package com.hackathon.sus.prenatal_documento.infrastructure.gateways;

import com.hackathon.sus.prenatal_documento.domain.ports.outbound.StoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

@Component
public class S3StorageGateway implements StoragePort {

    private static final Logger logger = LoggerFactory.getLogger(S3StorageGateway.class);

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageGateway(S3Client s3Client, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String upload(String key, InputStream inputStream, String contentType, long contentLength) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(inputStream, contentLength);

            s3Client.putObject(putObjectRequest, requestBody);
            logger.info("Arquivo enviado com sucesso para S3: {}", key);
            return key;
        } catch (Exception e) {
            logger.error("Erro ao fazer upload do arquivo para S3: {}", key, e);
            throw new RuntimeException("Erro ao fazer upload do arquivo", e);
        }
    }

    @Override
    public byte[] download(String storagePath) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storagePath)
                    .build();

            return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
        } catch (NoSuchKeyException e) {
            logger.error("Arquivo não encontrado no S3: {}", storagePath);
            throw new RuntimeException("Arquivo não encontrado", e);
        } catch (Exception e) {
            logger.error("Erro ao fazer download do arquivo do S3: {}", storagePath, e);
            throw new RuntimeException("Erro ao fazer download do arquivo", e);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storagePath)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("Arquivo deletado com sucesso do S3: {}", storagePath);
        } catch (Exception e) {
            logger.error("Erro ao deletar arquivo do S3: {}", storagePath, e);
            throw new RuntimeException("Erro ao deletar arquivo", e);
        }
    }

    @Override
    public boolean exists(String storagePath) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storagePath)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            logger.error("Erro ao verificar existência do arquivo no S3: {}", storagePath, e);
            return false;
        }
    }
}
