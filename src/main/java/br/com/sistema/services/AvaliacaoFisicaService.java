package br.com.sistema.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sistema.dtos.AvaliacaoFisicaDTO;
import br.com.sistema.exceptions.BusinessException;
import br.com.sistema.exceptions.ResourceNotFoundException;
import br.com.sistema.models.AvaliacaoFisica;
import br.com.sistema.models.Consulta;
import br.com.sistema.models.Paciente;
import br.com.sistema.repositories.AvaliacaoFisicaRepository;
import br.com.sistema.repositories.ConsultaRepository;
import br.com.sistema.utils.CalculosNutricionais;
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
        calcularDadosAutomaticos(avaliacao, consulta.getPaciente());
        AvaliacaoFisica saved = avaliacaoFisicaRepository.save(avaliacao);
        return converterParaDTO(saved);
    }
    
    // ## Atualizar avaliação física ##
    @Transactional
    public AvaliacaoFisicaDTO atualizarAvaliacao(Long consultaId, AvaliacaoFisicaDTO dto) {
    	System.err.println("Atualizando avaliação física para consulta ID: " + consultaId);
        AvaliacaoFisica avaliacao = avaliacaoFisicaRepository.findByConsultaId(consultaId).orElseThrow(() -> new ResourceNotFoundException("Avaliação física não encontrada"));
        mapearDTOParaEntidade(dto, avaliacao);
        // Recalcula os dados automáticos caso o frontend tenha alterado peso ou dobras
        calcularDadosAutomaticos(avaliacao, avaliacao.getConsulta().getPaciente());
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
        // NÃO mapear campos calculados (IMC, %gordura, massa gorda/magra) a partir do DTO
        // Eles serão recalculados por `calcularDadosAutomaticos` com base em peso, dobras e paciente
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
    
    // Calcular dados automáticos: IMC, % Gordura, Massa Gorda, Massa Magra
    private void calcularDadosAutomaticos(AvaliacaoFisica avaliacao, Paciente paciente) {
       
    	// 1. Calcular IMC
        if (avaliacao.getPesoAtual() != null && avaliacao.getAltura() != null) {
            Double imc = CalculosNutricionais.calcularIMC(avaliacao.getPesoAtual(), avaliacao.getAltura());
            avaliacao.setImc(imc);
        }
        
        // 2. Calcular % Gordura (se todas as 7 dobras estiverem preenchidas)
        if (todasDobrasPreenchidas(avaliacao)) {
            Integer idade = CalculosNutricionais.calcularIdade(paciente.getDataNascimento());
            
            Double percentualGordura = CalculosNutricionais.calcularPercentualGordura(
                paciente.getSexo(),
                idade,
                avaliacao.getDobraTriceps(),
                avaliacao.getDobraPeito(),
                avaliacao.getDobraAxilarMedia(),
                avaliacao.getDobraSubescapular(),
                avaliacao.getDobraAbdominal(),
                avaliacao.getDobraSupraIliaca(),
                avaliacao.getDobraCoxa()
            );
            
            avaliacao.setPercentualGordura(percentualGordura);
            
            // 3. Calcular Massa Gorda e Massa Magra
            if (percentualGordura != null && avaliacao.getPesoAtual() != null) {
                Double massaGorda = CalculosNutricionais.calcularMassaGorda(avaliacao.getPesoAtual(), percentualGordura);
                avaliacao.setMassaGorda(massaGorda);
                
                Double massaMagra = CalculosNutricionais.calcularMassaMagra(
                    avaliacao.getPesoAtual(), 
                    massaGorda
                );
                avaliacao.setMassaMagra(massaMagra);
            }
        }
    }
    
    // Verificar se todas as 7 dobras estão preenchidas
    private boolean todasDobrasPreenchidas(AvaliacaoFisica avaliacao) {
        return avaliacao.getDobraTriceps() != null
            && avaliacao.getDobraPeito() != null
            && avaliacao.getDobraAxilarMedia() != null
            && avaliacao.getDobraSubescapular() != null
            && avaliacao.getDobraAbdominal() != null
            && avaliacao.getDobraSupraIliaca() != null
            && avaliacao.getDobraCoxa() != null;
    }
}