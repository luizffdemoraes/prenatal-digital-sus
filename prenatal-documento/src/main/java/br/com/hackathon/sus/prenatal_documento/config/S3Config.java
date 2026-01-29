package br.com.hackathon.sus.prenatal_documento.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Configuration
public class S3Config {

    private static final Logger logger = LoggerFactory.getLogger(S3Config.class);

    @Value("${aws.s3.endpoint-url:http://localhost:4566}")
    private String endpointUrl;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Value("${aws.s3.access-key:test}")
    private String accessKey;

    @Value("${aws.s3.secret-key:test}")
    private String secretKey;

    @Value("${aws.s3.bucket-name:prenatal-documents}")
    private String bucketName;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .endpointOverride(java.net.URI.create(endpointUrl))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .forcePathStyle(true) // Necessário para LocalStack
                .build();
    }

    /**
     * Cria o bucket no S3/LocalStack após o contexto estar pronto, evitando ciclo
     * (não chamar @Bean da mesma classe dentro de @PostConstruct).
     */
    @Bean
    public ApplicationRunner s3BucketInitializer(S3Client s3Client) {
        return args -> {
            try {
                HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                        .bucket(bucketName)
                        .build();
                s3Client.headBucket(headBucketRequest);
                logger.info("Bucket '{}' já existe", bucketName);
            } catch (S3Exception e) {
                if (e.statusCode() == 404) {
                    CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                            .bucket(bucketName)
                            .build();
                    s3Client.createBucket(createBucketRequest);
                    logger.info("Bucket '{}' criado com sucesso", bucketName);
                } else {
                    logger.error("Erro ao verificar/criar bucket: {}", e.getMessage());
                }
            } catch (Exception e) {
                logger.warn("Não foi possível criar/verificar o bucket. Certifique-se de que o LocalStack está rodando: {}", e.getMessage());
            }
        };
    }
}
