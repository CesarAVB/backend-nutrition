package br.com.sistema.models;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_consultas")
public class Consulta {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;
    
    @Column(nullable = false)
    private LocalDateTime dataConsulta;
    
    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL)
    private QuestionarioEstiloVida questionario;
    
    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL)
    private AvaliacaoFisica avaliacaoFisica;
    
    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL)
    private RegistroFotografico registroFotografico;
	
}
