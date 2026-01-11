package br.com.sistema.dtos;

import lombok.Data;

@Data
public class ComparativoConsultasDTO {
	private ConsultaDetalhadaDTO consultaInicial;
    private ConsultaDetalhadaDTO consultaFinal;
    private DiferencasDTO diferencas;
}
