package br.com.sistema.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sistema.dtos.AvaliacaoFisicaDTO;
import br.com.sistema.exceptions.BusinessException;
import br.com.sistema.exceptions.ResourceNotFoundException;
import br.com.sistema.models.AvaliacaoFisica;
import br.com.sistema.models.Consulta;
import br.com.sistema.repositories.AvaliacaoFisicaRepository;
import br.com.sistema.repositories.ConsultaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvaliacaoFisicaService {
    
    private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    private final ConsultaRepository consultaRepository;
    
    //  ################## MÉTODOS PRINCIPAIS ##################
    
    // ## Criar nova avaliação física para uma consulta ##
    @Transactional
    public AvaliacaoFisicaDTO salvarAvaliacao(Long consultaId, AvaliacaoFisicaDTO dto) {
        Consulta consulta = consultaRepository.findById(consultaId).orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
        
        if (avaliacaoFisicaRepository.existsByConsultaId(consultaId)) {
            throw new BusinessException("Já existe uma avaliação física para esta consulta");
        }
        
        AvaliacaoFisica avaliacao = new AvaliacaoFisica();
        avaliacao.setConsulta(consulta);
        mapearDTOParaEntidade(dto, avaliacao);
        AvaliacaoFisica saved = avaliacaoFisicaRepository.save(avaliacao);
        return converterParaDTO(saved);
    }
    
    // 
    @Transactional
    public AvaliacaoFisicaDTO atualizarAvaliacao(Long consultaId, AvaliacaoFisicaDTO dto) {
    	System.err.println("Atualizando avaliação física para consulta ID: " + consultaId);
        AvaliacaoFisica avaliacao = avaliacaoFisicaRepository.findByConsultaId(consultaId).orElseThrow(() -> new ResourceNotFoundException("Avaliação física não encontrada"));
        mapearDTOParaEntidade(dto, avaliacao);
        AvaliacaoFisica updated = avaliacaoFisicaRepository.save(avaliacao);
        return converterParaDTO(updated);
    }
    
    // ## Buscar avaliação física por consulta ##
    @Transactional(readOnly = true)
    public AvaliacaoFisicaDTO buscarPorConsulta(Long consultaId) {
        AvaliacaoFisica avaliacao = avaliacaoFisicaRepository.findByConsultaId(consultaId).orElseThrow(() -> new ResourceNotFoundException("Avaliação física não encontrada"));
        return converterParaDTO(avaliacao);
    }
    
    // ## Deletar avaliação física por consulta ##
    @Transactional
    public void deletarAvaliacao(Long consultaId) {
        if (!avaliacaoFisicaRepository.existsByConsultaId(consultaId)) {
            throw new ResourceNotFoundException("Avaliação física não encontrada");
        }
        avaliacaoFisicaRepository.deleteByConsultaId(consultaId);
    }
    
    //  ################## MÉTODOS AUXILIARES ##################
    
    // Mapear DTO para Entidade
    private void mapearDTOParaEntidade(AvaliacaoFisicaDTO dto, AvaliacaoFisica entidade) {
    	System.out.println("Mapeando DTO para Entidade: " + dto);
        entidade.setPerimetroOmbro(dto.getPerimetroOmbro());
        entidade.setPerimetroTorax(dto.getPerimetroTorax());
        entidade.setPerimetroCintura(dto.getPerimetroCintura());
        entidade.setPerimetroAbdominal(dto.getPerimetroAbdominal());
        entidade.setPerimetroQuadril(dto.getPerimetroQuadril());
        entidade.setPerimetroBracoDireitoRelax(dto.getPerimetroBracoDireitoRelax());
        entidade.setPerimetroBracoDireitoContr(dto.getPerimetroBracoDireitoContr());
        entidade.setPerimetroBracoEsquerdoRelax(dto.getPerimetroBracoEsquerdoRelax());
        entidade.setPerimetroBracoEsquerdoContr(dto.getPerimetroBracoEsquerdoContr());
        entidade.setPerimetroAntebracoDireito(dto.getPerimetroAntebracoDireito());
        entidade.setPerimetroAntebracoEsquerdo(dto.getPerimetroAntebracoEsquerdo());
        entidade.setPerimetroCoxa(dto.getPerimetroCoxa());
        entidade.setPerimetroCoxaDireita(dto.getPerimetroCoxaDireita());
        entidade.setPerimetroCoxaEsquerda(dto.getPerimetroCoxaEsquerda());
        entidade.setPerimetroPanturrilhaDireita(dto.getPerimetroPanturrilhaDireita());
        entidade.setPerimetroPanturrilhaEsquerda(dto.getPerimetroPanturrilhaEsquerda());
        entidade.setDobraTriceps(dto.getDobraTriceps());
        entidade.setDobraPeito(dto.getDobraPeito());
        entidade.setDobraAxilarMedia(dto.getDobraAxilarMedia());
        entidade.setDobraSubescapular(dto.getDobraSubescapular());
        entidade.setDobraAbdominal(dto.getDobraAbdominal());
        entidade.setDobraSupraIliaca(dto.getDobraSupraIliaca());
        entidade.setDobraCoxa(dto.getDobraCoxa());
        entidade.setPesoAtual(dto.getPesoAtual());
        entidade.setMassaMagra(dto.getMassaMagra());
        entidade.setMassaGorda(dto.getMassaGorda());
        entidade.setPercentualGordura(dto.getPercentualGordura());
        entidade.setImc(dto.getImc());
    }
    
    // Converter Entidade para DTO
    private AvaliacaoFisicaDTO converterParaDTO(AvaliacaoFisica avaliacao) {
        AvaliacaoFisicaDTO dto = new AvaliacaoFisicaDTO();
        dto.setId(avaliacao.getId());
        dto.setConsultaId(avaliacao.getConsulta().getId());
        dto.setPerimetroOmbro(avaliacao.getPerimetroOmbro());
        dto.setPerimetroTorax(avaliacao.getPerimetroTorax());
        dto.setPerimetroCintura(avaliacao.getPerimetroCintura());
        dto.setPerimetroAbdominal(avaliacao.getPerimetroAbdominal());
        dto.setPerimetroQuadril(avaliacao.getPerimetroQuadril());
        dto.setPerimetroBracoDireitoRelax(avaliacao.getPerimetroBracoDireitoRelax());
        dto.setPerimetroBracoDireitoContr(avaliacao.getPerimetroBracoDireitoContr());
        dto.setPerimetroBracoEsquerdoRelax(avaliacao.getPerimetroBracoEsquerdoRelax());
        dto.setPerimetroBracoEsquerdoContr(avaliacao.getPerimetroBracoEsquerdoContr());
        dto.setPerimetroAntebracoDireito(avaliacao.getPerimetroAntebracoDireito());
        dto.setPerimetroAntebracoEsquerdo(avaliacao.getPerimetroAntebracoEsquerdo());
        dto.setPerimetroCoxa(avaliacao.getPerimetroCoxa());
        dto.setPerimetroCoxaDireita(avaliacao.getPerimetroCoxaDireita());
        dto.setPerimetroCoxaEsquerda(avaliacao.getPerimetroCoxaEsquerda());
        dto.setPerimetroPanturrilhaDireita(avaliacao.getPerimetroPanturrilhaDireita());
        dto.setPerimetroPanturrilhaEsquerda(avaliacao.getPerimetroPanturrilhaEsquerda());
        dto.setDobraTriceps(avaliacao.getDobraTriceps());
        dto.setDobraPeito(avaliacao.getDobraPeito());
        dto.setDobraAxilarMedia(avaliacao.getDobraAxilarMedia());
        dto.setDobraSubescapular(avaliacao.getDobraSubescapular());
        dto.setDobraAbdominal(avaliacao.getDobraAbdominal());
        dto.setDobraSupraIliaca(avaliacao.getDobraSupraIliaca());
        dto.setDobraCoxa(avaliacao.getDobraCoxa());
        dto.setPesoAtual(avaliacao.getPesoAtual());
        dto.setMassaMagra(avaliacao.getMassaMagra());
        dto.setMassaGorda(avaliacao.getMassaGorda());
        dto.setPercentualGordura(avaliacao.getPercentualGordura());
        dto.setImc(avaliacao.getImc());
        return dto;
    }
}