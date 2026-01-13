package br.com.sistema.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sistema.dtos.QuestionarioEstiloVidaDTO;
import br.com.sistema.exceptions.BusinessException;
import br.com.sistema.exceptions.ResourceNotFoundException;
import br.com.sistema.models.Consulta;
import br.com.sistema.models.QuestionarioEstiloVida;
import br.com.sistema.repositories.ConsultaRepository;
import br.com.sistema.repositories.QuestionarioEstiloVidaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionarioEstiloVidaService {
    
    private final QuestionarioEstiloVidaRepository questionarioRepository;
    private final ConsultaRepository consultaRepository;
    
    @Transactional
    public QuestionarioEstiloVidaDTO salvarQuestionario(Long consultaId, QuestionarioEstiloVidaDTO dto) {
        
    	Consulta consulta = consultaRepository.findById(consultaId).orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
    	
    	try {
    		var existe = questionarioRepository.findByConsultaId(consultaId);
    		
    		if (existe.isPresent()) {
    			throw new BusinessException("Já existe um questionário para esta consulta");
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}

        QuestionarioEstiloVida questionario = new QuestionarioEstiloVida();
        questionario.setConsulta(consulta);
        mapearDTOParaEntidade(dto, questionario);

        QuestionarioEstiloVida saved = questionarioRepository.save(questionario);
        return converterParaDTO(saved);
    }
    
    @Transactional
    public QuestionarioEstiloVidaDTO atualizarQuestionario(Long consultaId, QuestionarioEstiloVidaDTO dto) {
        QuestionarioEstiloVida questionario = questionarioRepository.findByConsultaId(consultaId).orElseThrow(() -> new ResourceNotFoundException("Questionário não encontrado"));
        
        mapearDTOParaEntidade(dto, questionario);
        
        QuestionarioEstiloVida updated = questionarioRepository.save(questionario);
        return converterParaDTO(updated);
    }
    
    @Transactional(readOnly = true)
    public QuestionarioEstiloVidaDTO buscarPorConsulta(Long consultaId) {
        QuestionarioEstiloVida questionario = questionarioRepository.findByConsultaId(consultaId).orElseThrow(() -> new ResourceNotFoundException("Questionário não encontrado"));
        return converterParaDTO(questionario);
    }
    
    @Transactional
    public void deletarQuestionario(Long consultaId) {
        if (!questionarioRepository.findByConsultaId(consultaId).isPresent()) {
            throw new ResourceNotFoundException("Questionário não encontrado");
        }
        questionarioRepository.deleteByConsultaId(consultaId);
    }
    
    private void mapearDTOParaEntidade(QuestionarioEstiloVidaDTO dto, QuestionarioEstiloVida entidade) {
        entidade.setObjetivo(dto.getObjetivo());
        entidade.setFrequenciaTreino(dto.getFrequenciaTreino());
        entidade.setTempoTreino(dto.getTempoTreino());
        entidade.setCirurgias(dto.getCirurgias());
        entidade.setDoencas(dto.getDoencas());
        entidade.setHistoricoFamiliar(dto.getHistoricoFamiliar());
        entidade.setMedicamentos(dto.getMedicamentos());
        entidade.setSuplementos(dto.getSuplementos());
        entidade.setUsoAnabolizantes(dto.getUsoAnabolizantes());
        entidade.setCicloAnabolizantes(dto.getCicloAnabolizantes());
        entidade.setDuracaoAnabolizantes(dto.getDuracaoAnabolizantes());
        entidade.setFuma(dto.getFuma());
        entidade.setFrequenciaAlcool(dto.getFrequenciaAlcool());
        entidade.setFuncionamentoIntestino(dto.getFuncionamentoIntestino());
        entidade.setQualidadeSono(dto.getQualidadeSono());
        entidade.setIngestaoAguaDiaria(dto.getIngestaoAguaDiaria());
        entidade.setAlimentosNaoGosta(dto.getAlimentosNaoGosta());
        entidade.setFrutasPreferidas(dto.getFrutasPreferidas());
        entidade.setNumeroRefeicoesDesejadas(dto.getNumeroRefeicoesDesejadas());
        entidade.setHorarioMaiorFome(dto.getHorarioMaiorFome());
        entidade.setPressaoArterial(dto.getPressaoArterial());
        entidade.setIntolerancias(dto.getIntolerancias());
    }
    
    private QuestionarioEstiloVidaDTO converterParaDTO(QuestionarioEstiloVida questionario) {
        QuestionarioEstiloVidaDTO dto = new QuestionarioEstiloVidaDTO();
        dto.setId(questionario.getId());
        dto.setConsultaId(questionario.getConsulta().getId());
        dto.setObjetivo(questionario.getObjetivo());
        dto.setFrequenciaTreino(questionario.getFrequenciaTreino());
        dto.setTempoTreino(questionario.getTempoTreino());
        dto.setCirurgias(questionario.getCirurgias());
        dto.setDoencas(questionario.getDoencas());
        dto.setHistoricoFamiliar(questionario.getHistoricoFamiliar());
        dto.setMedicamentos(questionario.getMedicamentos());
        dto.setSuplementos(questionario.getSuplementos());
        dto.setUsoAnabolizantes(questionario.getUsoAnabolizantes());
        dto.setCicloAnabolizantes(questionario.getCicloAnabolizantes());
        dto.setDuracaoAnabolizantes(questionario.getDuracaoAnabolizantes());
        dto.setFuma(questionario.getFuma());
        dto.setFrequenciaAlcool(questionario.getFrequenciaAlcool());
        dto.setFuncionamentoIntestino(questionario.getFuncionamentoIntestino());
        dto.setQualidadeSono(questionario.getQualidadeSono());
        dto.setIngestaoAguaDiaria(questionario.getIngestaoAguaDiaria());
        dto.setAlimentosNaoGosta(questionario.getAlimentosNaoGosta());
        dto.setFrutasPreferidas(questionario.getFrutasPreferidas());
        dto.setNumeroRefeicoesDesejadas(questionario.getNumeroRefeicoesDesejadas());
        dto.setHorarioMaiorFome(questionario.getHorarioMaiorFome());
        dto.setPressaoArterial(questionario.getPressaoArterial());
        dto.setIntolerancias(questionario.getIntolerancias());
        return dto;
    }
}