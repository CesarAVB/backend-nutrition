package br.com.sistema.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PacienteDTO {
	private Long id;
    private String nomeCompleto;
    private String cpf;
    private LocalDate dataNascimento;
    private String telefoneWhatsapp;
    private String email;
    private Integer totalConsultas;
    private LocalDateTime ultimaConsulta;
}
