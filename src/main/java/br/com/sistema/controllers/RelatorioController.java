package br.com.sistema.controllers;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sistema.dtos.RelatorioRequestDTO;
import br.com.sistema.services.RelatorioService;

@RestController
@RequestMapping("/api/v1/relatorio")
public class RelatorioController {

	private static final Logger log = LoggerFactory.getLogger(RelatorioController.class);
	
    @Autowired
    private RelatorioService relatorioService;

    // ==============================================
    // # Método - gerarRelatorio (POST)
    // # Recebe dados do request, gera o PDF e retorna como InputStreamResource
    // ==============================================
    @PostMapping(value = "", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> gerarRelatorio(@RequestBody RelatorioRequestDTO request) throws Exception {

    	System.err.println("Gerando relatório nutricional...");
        log.info("Iniciando geração de relatório para o paciente ID: {}", request.getPacienteId());
    	
        byte[] pdfBytes = relatorioService.gerarRelatorioEmPDF(request);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=relatorio-nutricional.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(resource);
    }

    // ==============================================
    // # Método - testeComN8n
    // # Chama o serviço que gera o JSON do relatório e o envia para o webhook do n8n
    // ==============================================
    @PostMapping(value = "/testeComN8n", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> testeComN8n(@RequestBody RelatorioRequestDTO request) throws Exception {
        log.info("Invocando envio de relatório via n8n webhook para o paciente ID: {}", request.getPacienteId());
        String webhookUrl = "https://webhook.redelognet.com.br/webhook/springboot/nutrition-help";
        var response = relatorioService.enviarRelatorioJson(request, webhookUrl);
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }

    // ==============================================
    // # Método - handle GET (fallback explicito)
    // # Por padrão, se um cliente fizer GET neste caminho, o ResourceHttpRequestHandler pode tentar
    // # servir um recurso estático e lançar NoResourceFoundException se não existir. Para evitar isso
    // # e retornar uma resposta clara ao cliente, definimos explicitamente um handler GET que retorna 405.
    // ==============================================
    @GetMapping("")
    public ResponseEntity<String> handleGet() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "POST");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(headers)
                .body("Método não permitido. Use POST para gerar o relatório.");
    }
}