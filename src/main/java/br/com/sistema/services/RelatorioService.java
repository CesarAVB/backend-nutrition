package br.com.sistema.services;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;

import br.com.sistema.dtos.AvaliacaoFisicaDTO;
import br.com.sistema.dtos.ConsultaDetalhadaDTO;
import br.com.sistema.dtos.PacienteDTO;
import br.com.sistema.dtos.QuestionarioEstiloVidaDTO;
import br.com.sistema.dtos.RegistroFotograficoDTO;
import br.com.sistema.dtos.RelatorioRequestDTO;

@Service
public class RelatorioService {

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

    @Autowired
    private ObjectMapper objectMapper;
    
    private static final Logger log = LoggerFactory.getLogger(RelatorioService.class);	

    // ==============================================
    // # Método - gerarRelatorioEmPDF
    // # Gera um PDF para um relatório: busca dados, processa e converte o template Thymeleaf em PDF
    // ==============================================
    public byte[] gerarRelatorioEmPDF(RelatorioRequestDTO request) throws Exception {
        
    	System.err.println("Iniciando geração de relatório nutricional...");
    	log.info("### INICIANDO GERAÇÃO:");
    	
    	// 1. Buscar dados
        var paciente = pacienteService.buscarPorId(request.getPacienteId());
        var consulta = consultaService.buscarConsultaCompleta(request.getConsultaId());
        var questionario = questionarioService.buscarPorConsulta(request.getConsultaId());
        var registroFotografico = registroFotograficoService.buscarPorConsulta(request.getConsultaId());
        var avaliacaoFisica = avaliacaoFisicaService.buscarPorConsulta(request.getConsultaId());

        // 2. DEBUG
        System.out.println("[DEBUG RELATÓRIO] Paciente: " + paciente);
        System.out.println("[DEBUG RELATÓRIO] Consulta: " + consulta);
        System.out.println("[DEBUG RELATÓRIO] Questionario: " + questionario);
        System.out.println("[DEBUG RELATÓRIO] Fotos: " + registroFotografico);
        System.out.println("[DEBUG RELATÓRIO] Avaliacao: " + avaliacaoFisica);

        // 3. Processar dados
        escaparUrlsFotos(registroFotografico);
        String dataConsultaFormatada = formatarDataConsulta(consulta);
        Integer idadePaciente = calcularIdadePaciente(paciente);

        // 4. Montar contexto Thymeleaf
        Context context = montarContextoThymeleaf(
            paciente, consulta, avaliacaoFisica, 
            questionario, registroFotografico, 
            dataConsultaFormatada, idadePaciente
        );

        // 5. Gerar HTML
        String template = selecionarTemplate(request.getTemplateType());
        log.info("### TEMPLATE SELECIONADO: {}", template);
        
        String html = templateEngine.process(template, context);
        log.info("### HTML PROCESSADO COM SUCESSO");
        System.out.println("[DEBUG RELATORIO] Template utilizado: " + template);

        // 6. Gerar e retornar PDF
        return gerarPDF(html);
    }

    // ==============================================
    // # Método - escaparUrlsFotos
    // # Escapa/normaliza as URLs das fotos dentro de um RegistroFotograficoDTO (se presente)
    // ==============================================
    private void escaparUrlsFotos(RegistroFotograficoDTO registroFotografico) {
        if (registroFotografico != null) {
            if (registroFotografico.getFotoAnterior() != null) {
                registroFotografico.setFotoAnterior(escapeUrl(registroFotografico.getFotoAnterior()));
            }
            if (registroFotografico.getFotoPosterior() != null) {
                registroFotografico.setFotoPosterior(escapeUrl(registroFotografico.getFotoPosterior()));
            }
            if (registroFotografico.getFotoLateralEsquerda() != null) {
                registroFotografico.setFotoLateralEsquerda(escapeUrl(registroFotografico.getFotoLateralEsquerda()));
            }
            if (registroFotografico.getFotoLateralDireita() != null) {
                registroFotografico.setFotoLateralDireita(escapeUrl(registroFotografico.getFotoLateralDireita()));
            }
        }
    }

    // ==============================================
    // # Método - formatarDataConsulta
    // # Formata a data/hora da consulta para exibição no relatório (dd/MM/yyyy HH:mm)
    // ==============================================
    private String formatarDataConsulta(ConsultaDetalhadaDTO consulta) {
        if (consulta != null && consulta.getDataConsulta() != null) {
            return consulta.getDataConsulta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "";
    }

    // ==============================================
    // # Método - calcularIdadePaciente
    // # Calcula a idade do paciente em anos com base na data de nascimento
    // ==============================================
    private Integer calcularIdadePaciente(PacienteDTO paciente) {
        if (paciente != null && paciente.getDataNascimento() != null) {
            return Period.between(paciente.getDataNascimento(), LocalDate.now()).getYears();
        }
        return null;
    }

    // ==============================================
    // # Método - montarContextoThymeleaf
    // # Monta o objeto Context do Thymeleaf com todas as variáveis necessárias para o template
    // ==============================================
    private Context montarContextoThymeleaf(PacienteDTO paciente, ConsultaDetalhadaDTO consulta, 
            AvaliacaoFisicaDTO avaliacaoFisica, QuestionarioEstiloVidaDTO questionario,
            RegistroFotograficoDTO registroFotografico, String dataConsultaFormatada, 
            Integer idadePaciente) {
        Context context = new Context();
        context.setVariable("paciente", paciente);
        context.setVariable("consulta", consulta);
        context.setVariable("avaliacaoFisica", avaliacaoFisica);
        context.setVariable("questionario", questionario);
        context.setVariable("registroFotografico", registroFotografico);
        context.setVariable("dataConsultaFormatada", dataConsultaFormatada);
        context.setVariable("idadePaciente", idadePaciente);
        context.setVariable("logoDataUri", null);
        return context;
    }

    // ==============================================
    // # Método - selecionarTemplate
    // # Retorna o nome do template Thymeleaf a ser usado com base no tipo fornecido
    // ==============================================
    private String selecionarTemplate(String templateType) {
        return switch (templateType) {
            case "simples" -> "relatorio-nutricional-simples";
            case "detalhado" -> "relatorio-nutricional-detalhado";
            case "teste" -> "teste-atributos";
            default -> "relatorio-nutricional-padrao";
        };
    }

    // ==============================================
    // # Método - gerarPDF
    // # Converte o HTML gerado em um array de bytes PDF usando OpenHTMLToPDF (PdfRendererBuilder)
    // ==============================================
    private byte[] gerarPDF(String html) throws Exception {
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(pdfStream);
        builder.run();
        return pdfStream.toByteArray();
    }

    // ==============================================
    // # Método - escapeUrl
    // # Escapa caracteres especiais em uma URL (tratamento mínimo atualmente)
    // ==============================================
    private String escapeUrl(String url) {
        if (url == null) return null;
        // Keep URLs as-is; Thymeleaf link expressions will produce XML-safe attributes.
        return url;
    }

    /**
     * Gera um JSON com todos os dados do relatório e envia via HTTP POST para a URL fornecida.
     * Retorna o HttpResponse<String> recebido do servidor de destino.
     */
    public HttpResponse<String> enviarRelatorioJson(RelatorioRequestDTO request, String destinationUrl) throws Exception {
        // 1. Buscar dados
        var paciente = pacienteService.buscarPorId(request.getPacienteId());
        var consulta = consultaService.buscarConsultaCompleta(request.getConsultaId());
        var questionario = questionarioService.buscarPorConsulta(request.getConsultaId());
        var registroFotografico = registroFotograficoService.buscarPorConsulta(request.getConsultaId());
        var avaliacaoFisica = avaliacaoFisicaService.buscarPorConsulta(request.getConsultaId());

        // 2. Processar dados
        escaparUrlsFotos(registroFotografico);
        String dataConsultaFormatada = formatarDataConsulta(consulta);
        Integer idadePaciente = calcularIdadePaciente(paciente);

        // 3. Montar payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("paciente", paciente);
        payload.put("consulta", consulta);
        payload.put("avaliacaoFisica", avaliacaoFisica);
        payload.put("questionario", questionario);
        payload.put("registroFotografico", registroFotografico);
        payload.put("dataConsultaFormatada", dataConsultaFormatada);
        payload.put("idadePaciente", idadePaciente);
        payload.put("templateType", request.getTemplateType());
        payload.put("generatedAt", OffsetDateTime.now());

        // 4. Serializar e enviar
        String json = objectMapper.writeValueAsString(payload);
        log.info("Enviando JSON do relatório para {} - {} bytes", destinationUrl, json.getBytes().length);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(destinationUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.info("Resposta do endpoint: status={} body={}", response.statusCode(), response.body());
        // Imprime no console a resposta do n8n (útil para debug local)
        System.out.println("[N8N RESPONSE] status=" + response.statusCode() + " body=" + response.body());
        return response;
    }
}