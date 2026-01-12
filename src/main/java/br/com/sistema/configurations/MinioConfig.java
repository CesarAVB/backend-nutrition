package br.com.sistema.configurations;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.region}")
    private String region;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @PostConstruct
    public void init() {
        log.info("MinIO configurado - Endpoint: {}", endpoint);
        log.info("MinIO - Bucket: {}", bucketName);
        createBucketIfNotExists();
    }

    private void createBucketIfNotExists() {
        try {
            S3Client client = s3Client();
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            
            client.headBucket(headBucketRequest);
            log.info("Bucket '{}' já existe", bucketName);
            
        } catch (Exception e) {
            try {
                S3Client client = s3Client();
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build();
                
                client.createBucket(createBucketRequest);
                log.info("Bucket '{}' criado com sucesso", bucketName);
                
            } catch (Exception ex) {
                log.error("Erro ao criar bucket '{}': {}", bucketName, ex.getMessage());
            }
        }
    }
}