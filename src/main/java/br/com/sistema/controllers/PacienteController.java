package br.com.sistema.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sistema.dtos.PacienteDTO;
import br.com.sistema.services.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes", description = "Endpoints para gestão de pacientes")
public class PacienteController {
    
    private final PacienteService pacienteService;
    
    @PostMapping
    @Operation(summary = "Cadastrar novo paciente", description = "Cria um novo paciente no sistema")
    public ResponseEntity<PacienteDTO> cadastrar(@Valid @RequestBody PacienteDTO dto) {
        PacienteDTO saved = pacienteService.cadastrarPaciente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar paciente por ID")
    public ResponseEntity<PacienteDTO> buscarPorId(@PathVariable Long id) {
        PacienteDTO paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(paciente);
    }
    
    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar paciente por CPF")
    public ResponseEntity<PacienteDTO> buscarPorCpf(@PathVariable String cpf) {
        PacienteDTO paciente = pacienteService.buscarPorCpf(cpf);
        return ResponseEntity.ok(paciente);
    }
    
    @GetMapping
    @Operation(summary = "Listar todos os pacientes")
    public ResponseEntity<List<PacienteDTO>> listarTodos() {
        List<PacienteDTO> pacientes = pacienteService.listarTodos();
        return ResponseEntity.ok(pacientes);
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar pacientes por nome")
    public ResponseEntity<List<PacienteDTO>> buscarPorNome(@RequestParam String nome) {
        List<PacienteDTO> pacientes = pacienteService.buscarPorNome(nome);
        return ResponseEntity.ok(pacientes);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do paciente")
    public ResponseEntity<PacienteDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PacienteDTO dto) {
        PacienteDTO updated = pacienteService.atualizarPaciente(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar paciente")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pacienteService.deletarPaciente(id);
        return ResponseEntity.noContent().build();
    }
}