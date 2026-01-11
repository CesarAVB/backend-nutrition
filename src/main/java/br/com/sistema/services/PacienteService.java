package br.com.sistema.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sistema.dtos.PacienteDTO;
import br.com.sistema.exceptions.BusinessException;
import br.com.sistema.exceptions.ResourceNotFoundException;
import br.com.sistema.models.Paciente;
import br.com.sistema.repositories.ConsultaRepository;
import br.com.sistema.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PacienteService {
    
    private final PacienteRepository pacienteRepository;
    private final ConsultaRepository consultaRepository;
    
    @Transactional
    public PacienteDTO cadastrarPaciente(PacienteDTO dto) {
        if (pacienteRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF já cadastrado no sistema");
        }
        
        Paciente paciente = new Paciente();
        paciente.setNomeCompleto(dto.getNomeCompleto());
        paciente.setCpf(dto.getCpf());
        paciente.setDataNascimento(dto.getDataNascimento());
        paciente.setTelefoneWhatsapp(dto.getTelefoneWhatsapp());
        paciente.setEmail(dto.getEmail());
        
        Paciente saved = pacienteRepository.save(paciente);
        return converterParaDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public PacienteDTO buscarPorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
        return converterParaDTO(paciente);
    }
    
    @Transactional(readOnly = true)
    public PacienteDTO buscarPorCpf(String cpf) {
        Paciente paciente = pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
        return converterParaDTO(paciente);
    }
    
    @Transactional(readOnly = true)
    public List<PacienteDTO> listarTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<PacienteDTO> buscarPorNome(String nome) {
        return pacienteRepository.findByNomeCompletoContainingIgnoreCase(nome)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }
    
    @Transactional
    public PacienteDTO atualizarPaciente(Long id, PacienteDTO dto) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
        
        paciente.setNomeCompleto(dto.getNomeCompleto());
        paciente.setTelefoneWhatsapp(dto.getTelefoneWhatsapp());
        paciente.setEmail(dto.getEmail());
        
        Paciente updated = pacienteRepository.save(paciente);
        return converterParaDTO(updated);
    }
    
    @Transactional
    public void deletarPaciente(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paciente não encontrado");
        }
        pacienteRepository.deleteById(id);
    }
    
    public PacienteDTO converterParaDTO(Paciente paciente) {
        PacienteDTO dto = new PacienteDTO();
        dto.setId(paciente.getId());
        dto.setNomeCompleto(paciente.getNomeCompleto());
        dto.setCpf(paciente.getCpf());
        dto.setDataNascimento(paciente.getDataNascimento());
        dto.setTelefoneWhatsapp(paciente.getTelefoneWhatsapp());
        dto.setEmail(paciente.getEmail());
        
        // Buscar dados calculados
        Long totalConsultas = consultaRepository.countByPacienteId(paciente.getId());
        dto.setTotalConsultas(totalConsultas.intValue());
        
        List<br.com.sistema.models.Consulta> consultas = consultaRepository
                .findTopByPacienteIdOrderByDataConsultaDesc(paciente.getId());
        
        if (!consultas.isEmpty()) {
            dto.setUltimaConsulta(consultas.get(0).getDataConsulta());
        }
        
        return dto;
    }
}