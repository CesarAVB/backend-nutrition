package br.com.sistema.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_questionarios_estilo_vida")
public class QuestionarioEstiloVida {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;
    
    // Rotina
    private String objetivo;
    private String frequenciaTreino;
    private String tempoTreino;
    
    // Saúde
    private String cirurgias;
    private String doencas;
    private String historicoFamiliar;
    private String medicamentos;
    private String suplementos;
    private Boolean usoAnabolizantes;
    private String cicloAnabolizantes;
    private String duracaoAnabolizantes;
    
    // Hábitos
    private Boolean fuma;
    private String frequenciaAlcool;
    private String funcionamentoIntestino;
    private String qualidadeSono;
    private Double ingestaoAguaDiaria;
    
    // Preferências
    @Column(length = 1000)
    private String alimentosNaoGosta;
    private String frutasPreferidas;
    private Integer numeroRefeicoesDesejadas;
    private String horarioMaiorFome;
    
    // Clínico
    private String pressaoArterial;
    private String intolerancias;
	
}
