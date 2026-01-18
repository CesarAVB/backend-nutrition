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
    
    // ## Atualizar avaliação física ##
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
    	
    	// ATUALIZAR APENAS SE O CAMPO NÃO FOR NULL
    	if (dto.getAltura() != null) entidade.setAltura(dto.getAltura());
        if (dto.getPerimetroOmbro() != null) entidade.setPerimetroOmbro(dto.getPerimetroOmbro());
        if (dto.getPerimetroTorax() != null) entidade.setPerimetroTorax(dto.getPerimetroTorax());
        if (dto.getPerimetroCintura() != null) entidade.setPerimetroCintura(dto.getPerimetroCintura());
        if (dto.getPerimetroAbdominal() != null) entidade.setPerimetroAbdominal(dto.getPerimetroAbdominal());
        if (dto.getPerimetroQuadril() != null) entidade.setPerimetroQuadril(dto.getPerimetroQuadril());
        if (dto.getPerimetroBracoDireitoRelax() != null) entidade.setPerimetroBracoDireitoRelax(dto.getPerimetroBracoDireitoRelax());
        if (dto.getPerimetroBracoDireitoContr() != null) entidade.setPerimetroBracoDireitoContr(dto.getPerimetroBracoDireitoContr());
        if (dto.getPerimetroBracoEsquerdoRelax() != null) entidade.setPerimetroBracoEsquerdoRelax(dto.getPerimetroBracoEsquerdoRelax());
        if (dto.getPerimetroBracoEsquerdoContr() != null) entidade.setPerimetroBracoEsquerdoContr(dto.getPerimetroBracoEsquerdoContr());
        if (dto.getPerimetroAntebracoDireito() != null) entidade.setPerimetroAntebracoDireito(dto.getPerimetroAntebracoDireito());
        if (dto.getPerimetroAntebracoEsquerdo() != null) entidade.setPerimetroAntebracoEsquerdo(dto.getPerimetroAntebracoEsquerdo());
        if (dto.getPerimetroCoxa() != null) entidade.setPerimetroCoxa(dto.getPerimetroCoxa());
        if (dto.getPerimetroCoxaDireita() != null) entidade.setPerimetroCoxaDireita(dto.getPerimetroCoxaDireita());
        if (dto.getPerimetroCoxaEsquerda() != null) entidade.setPerimetroCoxaEsquerda(dto.getPerimetroCoxaEsquerda());
        if (dto.getPerimetroPanturrilhaDireita() != null) entidade.setPerimetroPanturrilhaDireita(dto.getPerimetroPanturrilhaDireita());
        if (dto.getPerimetroPanturrilhaEsquerda() != null) entidade.setPerimetroPanturrilhaEsquerda(dto.getPerimetroPanturrilhaEsquerda());
        
        if (dto.getDobraTriceps() != null) entidade.setDobraTriceps(dto.getDobraTriceps());
        if (dto.getDobraPeito() != null) entidade.setDobraPeito(dto.getDobraPeito());
        if (dto.getDobraAxilarMedia() != null) entidade.setDobraAxilarMedia(dto.getDobraAxilarMedia());
        if (dto.getDobraSubescapular() != null) entidade.setDobraSubescapular(dto.getDobraSubescapular());
        if (dto.getDobraAbdominal() != null) entidade.setDobraAbdominal(dto.getDobraAbdominal());
        if (dto.getDobraSupraIliaca() != null) entidade.setDobraSupraIliaca(dto.getDobraSupraIliaca());
        if (dto.getDobraCoxa() != null) entidade.setDobraCoxa(dto.getDobraCoxa());
        
        if (dto.getPesoAtual() != null) entidade.setPesoAtual(dto.getPesoAtual());
        if (dto.getMassaMagra() != null) entidade.setMassaMagra(dto.getMassaMagra());
        if (dto.getMassaGorda() != null) entidade.setMassaGorda(dto.getMassaGorda());
        if (dto.getPercentualGordura() != null) entidade.setPercentualGordura(dto.getPercentualGordura());
        if (dto.getImc() != null) entidade.setImc(dto.getImc());
    }
    
    // Converter Entidade para DTO
    private AvaliacaoFisicaDTO converterParaDTO(AvaliacaoFisica avaliacao) {
        AvaliacaoFisicaDTO dto = new AvaliacaoFisicaDTO();
        dto.setId(avaliacao.getId());
        dto.setConsultaId(avaliacao.getConsulta().getId());
        dto.setAltura(avaliacao.getAltura());
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