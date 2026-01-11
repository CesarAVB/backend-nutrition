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
    @JoinColumn(name = "consulta_id", nullable = false, unique = true)
    private Consulta consulta;
    
    // Rotina
    @Column(length = 500)
    private String objetivo;
    private String frequenciaTreino;
    private String tempoTreino;
    
    // Saúde
    @Column(columnDefinition = "TEXT")
    private String cirurgias;
    @Column(columnDefinition = "TEXT")
    private String doencas;
    @Column(columnDefinition = "TEXT")
    private String historicoFamiliar;
    @Column(columnDefinition = "TEXT")
    private String medicamentos;
    @Column(columnDefinition = "TEXT")
    private String suplementos;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean usoAnabolizantes;
    
    private String cicloAnabolizantes;
    private String duracaoAnabolizantes;
    
    // Hábitos
    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean fuma;
    
    private String frequenciaAlcool;
    private String funcionamentoIntestino;
    private String qualidadeSono;

    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double ingestaoAguaDiaria;
    
    // Preferências
    @Column(columnDefinition = "TEXT")
    private String alimentosNaoGosta;
    
    @Column(length = 500)
    private String frutasPreferidas;
    private Integer numeroRefeicoesDesejadas;
    private String horarioMaiorFome;
    
    // Clínico
    private String pressaoArterial;
    
    @Column(columnDefinition = "TEXT")
    private String intolerancias;
}