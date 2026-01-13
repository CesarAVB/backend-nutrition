package br.com.sistema.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.sistema.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    // ============================================
    // Upload de arquivos
    // ============================================
    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);
        String fileName = generateFileName(file.getOriginalFilename());
        String key = folder + "/" + fileName;

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

            log.info("Arquivo enviado com sucesso: {}", key);
            return key; // retornamos a key para gerar presigned URL depois

        } catch (S3Exception e) {
            log.error("Erro ao fazer upload no S3: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao fazer upload da imagem: " + e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            log.error("Erro ao ler arquivo: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao processar arquivo");
        }
    }

    public String uploadBytes(byte[] bytes, String fileName, String contentType, String folder) {
        String key = folder + "/" + generateFileName(fileName);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentLength((long) bytes.length)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(new ByteArrayInputStream(bytes), bytes.length));

            log.info("Bytes enviados com sucesso: {}", key);
            return key; // retornamos a key

        } catch (S3Exception e) {
            log.error("Erro ao fazer upload no S3: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao fazer upload da imagem");
        }
    }

    // ============================================
    // Delete
    // ============================================
    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Arquivo deletado com sucesso: {}", key);

        } catch (S3Exception e) {
            log.error("Erro ao deletar arquivo no S3: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao deletar arquivo");
        }
    }

    // ============================================
    // Presigned URL
    // ============================================
    public String generatePresignedUrl(String key, Duration duration) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(duration)
                    .getObjectRequest(getObjectRequest)
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (S3Exception e) {
            log.error("Erro ao gerar presigned URL: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao gerar link da imagem");
        }
    }

    // ============================================
    // Auxiliares
    // ============================================
    private String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new BusinessException("URL do arquivo inválida");
        }

        // Remove o endpoint e bucket da URL
        String baseUrl = String.format("%s/%s/", endpoint, bucketName);
        return fileUrl.replace(baseUrl, "");
    }

    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Arquivo não pode ser vazio");
        }

        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new BusinessException("Arquivo muito grande. Tamanho máximo: 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("Apenas arquivos de imagem são permitidos");
        }
    }

	public String getBucketName() {
		return bucketName;
	}

	public String getEndpoint() {
		return endpoint;
	}
}
