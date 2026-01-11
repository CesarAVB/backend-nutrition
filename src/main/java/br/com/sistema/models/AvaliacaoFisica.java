package br.com.sistema.models;

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
@Table(name = "tbl_avaliacoes_fisicas")
public class AvaliacaoFisica {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;
    
    // Perímetros (cm)
    private Double perimetroOmbro;
    private Double perimetroTorax;
    private Double perimetroCintura;
    private Double perimetroAbdominal;
    private Double perimetroQuadril;
    private Double perimetroBracoDireitoRelax;
    private Double perimetroBracoDireitoContr;
    private Double perimetroBracoEsquerdoRelax;
    private Double perimetroBracoEsquerdoContr;
    private Double perimetroAntebracoDireito;
    private Double perimetroAntebracoEsquerdo;
    private Double perimetroCoxa;
    private Double perimetroCoxaDireita;
    private Double perimetroCoxaEsquerda;
    private Double perimetroPanturrilhaDireita;
    private Double perimetroPanturrilhaEsquerda;
    
    // Dobras Cutâneas (mm)
    private Double dobraTriceps;
    private Double dobraPeito;
    private Double dobraAxilarMedia;
    private Double dobraSubescapular;
    private Double dobraAbdominal;
    private Double dobraSupraIliaca;
    private Double dobraCoxa;
    
    // Composição Corporal
    private Double pesoAtual;
    private Double massaMagra;
    private Double massaGorda;
    private Double percentualGordura;
    private Double imc;
	
}
