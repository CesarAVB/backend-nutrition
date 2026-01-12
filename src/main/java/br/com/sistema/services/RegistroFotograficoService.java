package br.com.sistema.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.sistema.dtos.RegistroFotograficoDTO;
import br.com.sistema.exceptions.BusinessException;
import br.com.sistema.exceptions.ResourceNotFoundException;
import br.com.sistema.models.Consulta;
import br.com.sistema.models.RegistroFotografico;
import br.com.sistema.repositories.ConsultaRepository;
import br.com.sistema.repositories.RegistroFotograficoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistroFotograficoService {

    private final RegistroFotograficoRepository registroFotograficoRepository;
    private final ConsultaRepository consultaRepository;
    private final S3Service s3Service;

    private static final String FOLDER_FOTOS = "fotos-consultas";

    @Transactional
    public RegistroFotograficoDTO salvarRegistro(Long consultaId, 
                                                   MultipartFile fotoAnterior,
                                                   MultipartFile fotoPosterior,
                                                   MultipartFile fotoLateralEsquerda,
                                                   MultipartFile fotoLateralDireita) {
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));

        if (registroFotograficoRepository.existsByConsultaId(consultaId)) {
            throw new BusinessException("Já existe um registro fotográfico para esta consulta");
        }

        RegistroFotografico registro = new RegistroFotografico();
        registro.setConsulta(consulta);

        // Upload das fotos para o MinIO
        if (fotoAnterior != null && !fotoAnterior.isEmpty()) {
            String urlAnterior = s3Service.uploadFile(fotoAnterior, FOLDER_FOTOS);
            registro.setFotoAnterior(urlAnterior);
        }

        if (fotoPosterior != null && !fotoPosterior.isEmpty()) {
            String urlPosterior = s3Service.uploadFile(fotoPosterior, FOLDER_FOTOS);
            registro.setFotoPosterior(urlPosterior);
        }

        if (fotoLateralEsquerda != null && !fotoLateralEsquerda.isEmpty()) {
            String urlLateralEsquerda = s3Service.uploadFile(fotoLateralEsquerda, FOLDER_FOTOS);
            registro.setFotoLateralEsquerda(urlLateralEsquerda);
        }

        if (fotoLateralDireita != null && !fotoLateralDireita.isEmpty()) {
            String urlLateralDireita = s3Service.uploadFile(fotoLateralDireita, FOLDER_FOTOS);
            registro.setFotoLateralDireita(urlLateralDireita);
        }

        RegistroFotografico saved = registroFotograficoRepository.save(registro);
        return converterParaDTO(saved);
    }

    @Transactional
    public RegistroFotograficoDTO atualizarRegistro(Long consultaId,
                                                      MultipartFile fotoAnterior,
                                                      MultipartFile fotoPosterior,
                                                      MultipartFile fotoLateralEsquerda,
                                                      MultipartFile fotoLateralDireita) {
        RegistroFotografico registro = registroFotograficoRepository.findByConsultaId(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro fotográfico não encontrado"));

        // Atualizar foto anterior
        if (fotoAnterior != null && !fotoAnterior.isEmpty()) {
            if (registro.getFotoAnterior() != null) {
                s3Service.deleteFile(registro.getFotoAnterior());
            }
            String urlAnterior = s3Service.uploadFile(fotoAnterior, FOLDER_FOTOS);
            registro.setFotoAnterior(urlAnterior);
        }

        // Atualizar foto posterior
        if (fotoPosterior != null && !fotoPosterior.isEmpty()) {
            if (registro.getFotoPosterior() != null) {
                s3Service.deleteFile(registro.getFotoPosterior());
            }
            String urlPosterior = s3Service.uploadFile(fotoPosterior, FOLDER_FOTOS);
            registro.setFotoPosterior(urlPosterior);
        }

        // Atualizar foto lateral esquerda
        if (fotoLateralEsquerda != null && !fotoLateralEsquerda.isEmpty()) {
            if (registro.getFotoLateralEsquerda() != null) {
                s3Service.deleteFile(registro.getFotoLateralEsquerda());
            }
            String urlLateralEsquerda = s3Service.uploadFile(fotoLateralEsquerda, FOLDER_FOTOS);
            registro.setFotoLateralEsquerda(urlLateralEsquerda);
        }

        // Atualizar foto lateral direita
        if (fotoLateralDireita != null && !fotoLateralDireita.isEmpty()) {
            if (registro.getFotoLateralDireita() != null) {
                s3Service.deleteFile(registro.getFotoLateralDireita());
            }
            String urlLateralDireita = s3Service.uploadFile(fotoLateralDireita, FOLDER_FOTOS);
            registro.setFotoLateralDireita(urlLateralDireita);
        }

        RegistroFotografico updated = registroFotograficoRepository.save(registro);
        return converterParaDTO(updated);
    }

    @Transactional(readOnly = true)
    public RegistroFotograficoDTO buscarPorConsulta(Long consultaId) {
        RegistroFotografico registro = registroFotograficoRepository.findByConsultaId(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro fotográfico não encontrado"));
        return converterParaDTO(registro);
    }

    @Transactional
    public void deletarRegistro(Long consultaId) {
        RegistroFotografico registro = registroFotograficoRepository.findByConsultaId(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro fotográfico não encontrado"));

        // Deletar fotos do S3
        if (registro.getFotoAnterior() != null) {
            s3Service.deleteFile(registro.getFotoAnterior());
        }
        if (registro.getFotoPosterior() != null) {
            s3Service.deleteFile(registro.getFotoPosterior());
        }
        if (registro.getFotoLateralEsquerda() != null) {
            s3Service.deleteFile(registro.getFotoLateralEsquerda());
        }
        if (registro.getFotoLateralDireita() != null) {
            s3Service.deleteFile(registro.getFotoLateralDireita());
        }

        registroFotograficoRepository.deleteByConsultaId(consultaId);
    }

    private RegistroFotograficoDTO converterParaDTO(RegistroFotografico registro) {
        RegistroFotograficoDTO dto = new RegistroFotograficoDTO();
        dto.setId(registro.getId());
        dto.setConsultaId(registro.getConsulta().getId());
        dto.setFotoAnterior(registro.getFotoAnterior());
        dto.setFotoPosterior(registro.getFotoPosterior());
        dto.setFotoLateralEsquerda(registro.getFotoLateralEsquerda());
        dto.setFotoLateralDireita(registro.getFotoLateralDireita());
        return dto;
    }
}