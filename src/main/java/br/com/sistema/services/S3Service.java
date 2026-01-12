package br.com.sistema.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * Faz upload de arquivo para o MinIO/S3
     * 
     * @param file MultipartFile a ser enviado
     * @param folder Pasta dentro do bucket (ex: "fotos-consultas")
     * @return URL completa do arquivo no S3
     */
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
            return getFileUrl(key);

        } catch (S3Exception e) {
            log.error("Erro ao fazer upload no S3: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao fazer upload da imagem: " + e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            log.error("Erro ao ler arquivo: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao processar arquivo");
        }
    }

    /**
     * Faz upload de byte array (útil para imagens processadas)
     */
    public String uploadBytes(byte[] bytes, String fileName, String contentType, String folder) {
        String key = folder + "/" + generateFileName(fileName);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentLength((long) bytes.length)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                    new ByteArrayInputStream(bytes), bytes.length));

            log.info("Bytes enviados com sucesso: {}", key);
            return getFileUrl(key);

        } catch (S3Exception e) {
            log.error("Erro ao fazer upload no S3: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao fazer upload da imagem");
        }
    }

    /**
     * Deleta arquivo do S3
     */
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

    /**
     * Gera URL completa do arquivo
     */
    private String getFileUrl(String key) {
        return String.format("%s/%s/%s", endpoint, bucketName, key);
    }

    /**
     * Extrai a key (caminho) da URL completa
     */
    private String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new BusinessException("URL do arquivo inválida");
        }
        
        // Remove o endpoint e bucket da URL
        String baseUrl = String.format("%s/%s/", endpoint, bucketName);
        return fileUrl.replace(baseUrl, "");
    }

    /**
     * Gera nome único para o arquivo
     */
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * Valida o arquivo antes do upload
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Arquivo não pode ser vazio");
        }

        // Validar tamanho (exemplo: máximo 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new BusinessException("Arquivo muito grande. Tamanho máximo: 5MB");
        }

        // Validar tipo de arquivo (apenas imagens)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("Apenas arquivos de imagem são permitidos");
        }
    }
}