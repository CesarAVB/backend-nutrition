package br.com.sistema.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_avaliacoes_fisicas")
public class AvaliacaoFisica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;
    
    // Perímetros (cm)
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroOmbro;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroTorax;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroCintura;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroAbdominal;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroQuadril;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroBracoDireitoRelax;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroBracoDireitoContr;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroBracoEsquerdoRelax;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroBracoEsquerdoContr;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroAntebracoDireito;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroAntebracoEsquerdo;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroCoxa;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroCoxaDireita;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroCoxaEsquerda;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroPanturrilhaDireita;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double perimetroPanturrilhaEsquerda;
    
    // Dobras Cutâneas (mm)
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double dobraTriceps;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double dobraPeito;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double dobraAxilarMedia;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double dobraSubescapular;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double dobraAbdominal;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double dobraSupraIliaca;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double dobraCoxa;
    
    // Composição Corporal
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double pesoAtual;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double massaMagra;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double massaGorda;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double percentualGordura;
    
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double imc;
}