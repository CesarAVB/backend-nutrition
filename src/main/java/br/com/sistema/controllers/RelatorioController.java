package br.com.sistema.controllers;

import br.com.sistema.dtos.RelatorioRequestDTO;
import br.com.sistema.services.PacienteService;
import br.com.sistema.services.ConsultaService;
import br.com.sistema.services.QuestionarioEstiloVidaService;
import br.com.sistema.services.RegistroFotograficoService;
import br.com.sistema.services.AvaliacaoFisicaService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/relatorio")
public class RelatorioController {
    
    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private ConsultaService consultaService;
    
    @Autowired
    private QuestionarioEstiloVidaService questionarioService;
    
    @Autowired
    private RegistroFotograficoService registroFotograficoService;
    
    @Autowired
    private AvaliacaoFisicaService avaliacaoFisicaService;
    
    @Autowired
    private SpringTemplateEngine templateEngine;

    @PostMapping(produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> gerarRelatorio(@RequestBody RelatorioRequestDTO request) throws Exception {
        
        // 1. Buscar dados
        var paciente = pacienteService.buscarPorId(request.getPacienteId());
        var consulta = consultaService.buscarConsultaCompleta(request.getConsultaId());
        var questionario = questionarioService.buscarPorConsulta(request.getConsultaId());
        var registroFotografico = registroFotograficoService.buscarPorConsulta(request.getConsultaId());
        var avaliacaoFisica = avaliacaoFisicaService.buscarPorConsulta(request.getConsultaId());

        // 2. DEBUG (mantido para verificação)
        System.out.println("[DEBUG] Paciente: " + paciente);
        System.out.println("[DEBUG] Consulta: " + consulta);
        System.out.println("[DEBUG] Questionario: " + questionario);
        System.out.println("[DEBUG] Fotos: " + registroFotografico);
        System.out.println("[DEBUG] Avaliacao: " + avaliacaoFisica);

        // 3. Formatar data da consulta
        String dataConsultaFormatada = "";
        if (consulta != null && consulta.getDataConsulta() != null) {
            dataConsultaFormatada = consulta.getDataConsulta()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        // 4. Calcular idade do paciente
        Integer idadePaciente = null;
        if (paciente != null && paciente.getDataNascimento() != null) {
            idadePaciente = Period.between(
                paciente.getDataNascimento(), 
                LocalDate.now()
            ).getYears();
        }

        // 5. Montar contexto Thymeleaf
        Context context = new Context();
        context.setVariable("paciente", paciente);
        context.setVariable("consulta", consulta);
        context.setVariable("avaliacaoFisica", avaliacaoFisica);
        context.setVariable("questionario", questionario);
        context.setVariable("registroFotografico", registroFotografico); // ⭐ CORRIGIDO!
        context.setVariable("dataConsultaFormatada", dataConsultaFormatada);
        context.setVariable("idadePaciente", idadePaciente);
        context.setVariable("logoDataUri", null); // Opcional: adicione logo se tiver

        // 6. Escolher template
        String template = switch (request.getTemplateType()) {
            case "simples" -> "relatorio-nutricional-simples";
            case "detalhado" -> "relatorio-nutricional-detalhado";
            case "teste" -> "teste-atributos";
            default -> "relatorio-nutricional-padrao";
        };

        // 7. Gerar HTML
        String html = templateEngine.process(template, context);
        System.out.println("[DEBUG] Template utilizado: " + template);

        // 8. Gerar PDF usando OpenHTMLtoPDF
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(pdfStream);
        builder.run();

        // 9. Retornar PDF
        InputStreamResource resource = new InputStreamResource(
            new ByteArrayInputStream(pdfStream.toByteArray())
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=relatorio-nutricional.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}