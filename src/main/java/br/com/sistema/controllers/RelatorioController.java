package br.com.sistema.controllers;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sistema.dtos.RelatorioRequestDTO;
import br.com.sistema.services.RelatorioService;

@RestController
@RequestMapping("/api/v1/relatorio")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    // ==============================================
    // # Método - gerarRelatorio
    // # Recebe dados do request, gera o PDF e retorna como InputStreamResource
    // ==============================================
    @PostMapping(value = "", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> gerarRelatorio(@RequestBody RelatorioRequestDTO request) throws Exception {

        byte[] pdfBytes = relatorioService.gerarRelatorioEmPDF(request);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(pdfBytes));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=relatorio-nutricional.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(resource);
    }
}