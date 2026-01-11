package br.com.sistema.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoFisicaDTO {
    
    private Long id;
    private Long consultaId;
    
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