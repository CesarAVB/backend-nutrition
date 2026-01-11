package br.com.sistema.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ConsultaResumoDTO {
	private Long id;
    private LocalDateTime dataConsulta;
    private Double peso;
    private Double percentualGordura;
    private String objetivo;
}
