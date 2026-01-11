package br.com.sistema.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nomeCompleto;
    
    @Column(unique = true, nullable = false, length = 11)
    private String cpf;
    
    @Column(nullable = false)
    private LocalDate dataNascimento;
    
    @Column(length = 15)
    private String telefoneWhatsapp;
    
    private String email;
}