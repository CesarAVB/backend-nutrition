package br.com.sistema.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sistema.dtos.ComparativoConsultasDTO;
import br.com.sistema.dtos.ConsultaDetalhadaDTO;
import br.com.sistema.dtos.ConsultaResumoDTO;
import br.com.sistema.dtos.DiferencasDTO;
import br.com.sistema.exceptions.BusinessException;
import br.com.sistema.exceptions.ResourceNotFoundException;
import br.com.sistema.models.AvaliacaoFisica;
import br.com.sistema.models.Consulta;
import br.com.sistema.models.Paciente;
import br.com.sistema.repositories.AvaliacaoFisicaRepository;
import br.com.sistema.repositories.ConsultaRepository;
import br.com.sistema.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultaService {
    
    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    
    @Transactional
    public ConsultaResumoDTO criarConsulta(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
        
        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setDataConsulta(LocalDateTime.now());
        
        Consulta saved = consultaRepository.save(consulta);
        return converterParaResumoDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public List<ConsultaResumoDTO> listarConsultasPorPaciente(Long pacienteId) {
        return consultaRepository.findByPacienteIdOrderByDataConsultaDesc(pacienteId)
                .stream()
                .map(this::converterParaResumoDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public ConsultaDetalhadaDTO buscarConsultaCompleta(Long consultaId) {
        Consulta consulta = consultaRepository.findByIdCompleta(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
        return converterParaDetalhadaDTO(consulta);
    }
    
    @Transactional(readOnly = true)
    public ComparativoConsultasDTO compararConsultas(Long pacienteId, Long consultaInicialId, Long consultaFinalId) {
        Consulta consultaInicial = consultaRepository.findByIdCompleta(consultaInicialId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta inicial não encontrada"));
        
        Consulta consultaFinal = consultaRepository.findByIdCompleta(consultaFinalId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta final não encontrada"));
        
        if (!consultaInicial.getPaciente().getId().equals(pacienteId) ||
            !consultaFinal.getPaciente().getId().equals(pacienteId)) {
            throw new BusinessException("Consultas não pertencem ao mesmo paciente");
        }
        
        ComparativoConsultasDTO comparativo = new ComparativoConsultasDTO();
        comparativo.setConsultaInicial(converterParaDetalhadaDTO(consultaInicial));
        comparativo.setConsultaFinal(converterParaDetalhadaDTO(consultaFinal));
        comparativo.setDiferencas(calcularDiferencas(consultaInicial, consultaFinal));
        
        return comparativo;
    }
    
    private DiferencasDTO calcularDiferencas(Consulta inicial, Consulta finalConsulta) {
        DiferencasDTO diferencas = new DiferencasDTO();
        
        AvaliacaoFisica avalInicial = inicial.getAvaliacaoFisica();
        AvaliacaoFisica avalFinal = finalConsulta.getAvaliacaoFisica();
        
        if (avalInicial != null && avalFinal != null) {
            diferencas.setDiferencaPeso(avalFinal.getPesoAtual() - avalInicial.getPesoAtual());
            diferencas.setDiferencaPercentualGordura(avalFinal.getPercentualGordura() - avalInicial.getPercentualGordura());
            diferencas.setDiferencaMassaMagra(avalFinal.getMassaMagra() - avalInicial.getMassaMagra());
            diferencas.setDiferencaMassaGorda(avalFinal.getMassaGorda() - avalInicial.getMassaGorda());
            
            Map<String, Double> diferencasPerimetros = new HashMap<>();
            diferencasPerimetros.put("cintura", avalFinal.getPerimetroCintura() - avalInicial.getPerimetroCintura());
            diferencasPerimetros.put("quadril", avalFinal.getPerimetroQuadril() - avalInicial.getPerimetroQuadril());
            diferencas.setDiferencasPerimetros(diferencasPerimetros);
        }
        
        return diferencas;
    }
    
    private ConsultaResumoDTO converterParaResumoDTO(Consulta consulta) {
        ConsultaResumoDTO dto = new ConsultaResumoDTO();
        dto.setId(consulta.getId());
        dto.setDataConsulta(consulta.getDataConsulta());
        
        if (consulta.getAvaliacaoFisica() != null) {
            dto.setPeso(consulta.getAvaliacaoFisica().getPesoAtual());
            dto.setPercentualGordura(consulta.getAvaliacaoFisica().getPercentualGordura());
        }
        
        if (consulta.getQuestionario() != null) {
            dto.setObjetivo(consulta.getQuestionario().getObjetivo());
        }
        
        return dto;
    }
    
    private ConsultaDetalhadaDTO converterParaDetalhadaDTO(Consulta consulta) {
        ConsultaDetalhadaDTO dto = new ConsultaDetalhadaDTO();
        dto.setId(consulta.getId());
        dto.setDataConsulta(consulta.getDataConsulta());
        dto.setAvaliacaoFisica(consulta.getAvaliacaoFisica());
        dto.setRegistroFotografico(consulta.getRegistroFotografico());
        return dto;
    }
}