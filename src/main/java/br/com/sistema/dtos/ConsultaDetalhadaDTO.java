package br.com.sistema.dtos;

import java.time.LocalDateTime;

import br.com.sistema.models.AvaliacaoFisica;
import br.com.sistema.models.RegistroFotografico;
import lombok.Data;

@Data
public class ConsultaDetalhadaDTO {
	private Long id;
    private LocalDateTime dataConsulta;
    private AvaliacaoFisica avaliacaoFisica;
    private RegistroFotografico registroFotografico;
}
