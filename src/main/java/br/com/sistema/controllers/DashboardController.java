package br.com.sistema.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sistema.dtos.ConsultaHojeDTO;
import br.com.sistema.dtos.DashboardStatsDTO;
import br.com.sistema.dtos.PacienteDTO;
import br.com.sistema.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints para estatísticas do dashboard")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/stats")
    @Operation(summary = "Buscar estatísticas gerais")
    public ResponseEntity<DashboardStatsDTO> buscarEstatisticas() {
        DashboardStatsDTO stats = dashboardService.buscarEstatisticas();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/consultas-hoje")
    @Operation(summary = "Buscar consultas de hoje")
    public ResponseEntity<List<ConsultaHojeDTO>> buscarConsultasHoje() {
        List<ConsultaHojeDTO> consultas = dashboardService.buscarConsultasHoje();
        return ResponseEntity.ok(consultas);
    }
    
    @GetMapping("/pacientes-recentes")
    @Operation(summary = "Buscar pacientes mais recentes")
    public ResponseEntity<List<PacienteDTO>> buscarPacientesRecentes() {
        List<PacienteDTO> pacientes = dashboardService.buscarPacientesRecentes(5);
        return ResponseEntity.ok(pacientes);
    }
}