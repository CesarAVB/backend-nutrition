package br.com.sistema.dtos;

import java.util.Map;

import lombok.Data;

@Data
public class DiferencasDTO {
	private Double diferencaPeso;
	private Double diferencaPercentualGordura;
	private Double diferencaMassaMagra;
	private Double diferencaMassaGorda;
	private Map<String, Double> diferencasPerimetros;
}
