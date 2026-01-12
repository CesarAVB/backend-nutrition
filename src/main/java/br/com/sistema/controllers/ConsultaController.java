package br.com.sistema.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sistema.dtos.ComparativoConsultasDTO;
import br.com.sistema.dtos.ConsultaDetalhadaDTO;
import br.com.sistema.dtos.ConsultaResumoDTO;
import br.com.sistema.services.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas", description = "Endpoints para gestão de consultas e avaliações")
public class ConsultaController {
    
    private final ConsultaService consultaService;
    
    @PostMapping("/paciente/{pacienteId}")
    @Operation(summary = "Criar nova consulta", description = "Cria uma nova consulta para o paciente")
    public ResponseEntity<ConsultaResumoDTO> criar(@PathVariable Long pacienteId) {
        ConsultaResumoDTO saved = consultaService.criarConsulta(pacienteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Listar consultas do paciente", description = "Retorna o histórico de consultas ordenado por data")
    public ResponseEntity<List<ConsultaResumoDTO>> listarPorPaciente(@PathVariable Long pacienteId) {
        List<ConsultaResumoDTO> consultas = consultaService.listarConsultasPorPaciente(pacienteId);
        return ResponseEntity.ok(consultas);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar consulta completa", description = "Retorna todos os detalhes da consulta incluindo avaliações e fotos")
    public ResponseEntity<ConsultaDetalhadaDTO> buscarCompleta(@PathVariable Long id) {
        ConsultaDetalhadaDTO consulta = consultaService.buscarConsultaCompleta(id);
        return ResponseEntity.ok(consulta);
    }
    
    @GetMapping("/comparar/{pacienteId}")
    @Operation(summary = "Comparar duas consultas", description = "Compara avaliações entre duas consultas do mesmo paciente")
    public ResponseEntity<ComparativoConsultasDTO> comparar(
            @PathVariable Long pacienteId,
            @RequestParam Long consultaInicialId,
            @RequestParam Long consultaFinalId) {
        ComparativoConsultasDTO comparativo = consultaService.compararConsultas(pacienteId, consultaInicialId, consultaFinalId);
        return ResponseEntity.ok(comparativo);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar consulta", description = "Remove uma consulta e seus dados relacionados")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        consultaService.deletarConsulta(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    @Operation(summary = "Listar todas as consultas", description = "Retorna todas as consultas do sistema ordenadas por data")
    public ResponseEntity<List<ConsultaResumoDTO>> listarTodas() {
        List<ConsultaResumoDTO> consultas = consultaService.listarTodasConsultas();
        return ResponseEntity.ok(consultas);
    }
    
    @PutMapping("/{id}/data")
    @Operation(summary = "Atualizar data da consulta", description = "Permite remarcar a data de uma consulta")
    public ResponseEntity<ConsultaResumoDTO> atualizarData(
            @PathVariable Long id, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime novaData) {
        ConsultaResumoDTO updated = consultaService.atualizarDataConsulta(id, novaData);
        return ResponseEntity.ok(updated);
    }
}