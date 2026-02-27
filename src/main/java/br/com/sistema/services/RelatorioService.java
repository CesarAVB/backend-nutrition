package br.com.sistema.services;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

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
import br.com.sistema.dtos.ConsultaComparativaItemDTO;
import br.com.sistema.dtos.ConsultaDetalhadaDTO;
import br.com.sistema.dtos.ConsultaResumoDTO;
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
    private PlaywrightPdfService playwrightPdfService;

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
    // # MÃ©todo - gerarRelatorioEmPDF
    // # Gera um PDF para um relatÃ³rio: busca dados, processa e converte o template Thymeleaf em PDF
    // ==============================================
    public byte[] gerarRelatorioEmPDF(RelatorioRequestDTO request) throws Exception {
        
    	System.err.println("Iniciando geraÃ§Ã£o de relatÃ³rio nutricional...");
    	log.info("### INICIANDO GERAÃ‡ÃƒO:");
    	
    	// 1. Buscar dados
        var paciente = pacienteService.buscarPorId(request.getPacienteId());
        var consulta = consultaService.buscarConsultaCompleta(request.getConsultaId());
        var questionario = questionarioService.buscarPorConsulta(request.getConsultaId());
        var registroFotografico = registroFotograficoService.buscarPorConsulta(request.getConsultaId());
        var avaliacaoFisica = avaliacaoFisicaService.buscarPorConsulta(request.getConsultaId());

        // 2. DEBUG
        System.out.println("[DEBUG RELATÃ“RIO] Paciente: " + paciente);
        System.out.println("[DEBUG RELATÃ“RIO] Consulta: " + consulta);
        System.out.println("[DEBUG RELATÃ“RIO] Questionario: " + questionario);
        System.out.println("[DEBUG RELATÃ“RIO] Fotos: " + registroFotografico);
        System.out.println("[DEBUG RELATÃ“RIO] Avaliacao: " + avaliacaoFisica);

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
        if ("relatorio-comparativo".equals(template)) {
            log.info("### PDF COMPARATIVO via Playwright");
            return playwrightPdfService.generatePdf(html);
        }
        return gerarPDF(html);
    }

    // ==============================================
    // # MÃ©todo - escaparUrlsFotos
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
    // # MÃ©todo - formatarDataConsulta
    // # Formata a data/hora da consulta para exibiÃ§Ã£o no relatÃ³rio (dd/MM/yyyy HH:mm)
    // ==============================================
    private String formatarDataConsulta(ConsultaDetalhadaDTO consulta) {
        if (consulta != null && consulta.getDataConsulta() != null) {
            return consulta.getDataConsulta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "";
    }

    // ==============================================
    // # MÃ©todo - calcularIdadePaciente
    // # Calcula a idade do paciente em anos com base na data de nascimento
    // ==============================================
    private Integer calcularIdadePaciente(PacienteDTO paciente) {
        if (paciente != null && paciente.getDataNascimento() != null) {
            return Period.between(paciente.getDataNascimento(), LocalDate.now()).getYears();
        }
        return null;
    }

    // ==============================================
    // # MÃ©todo - montarContextoThymeleaf
    // # Monta o objeto Context do Thymeleaf com todas as variÃ¡veis necessÃ¡rias para o template
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
    // # MÃ©todo - selecionarTemplate
    // # Retorna o nome do template Thymeleaf a ser usado com base no tipo fornecido
    // ==============================================
    private String selecionarTemplate(String templateType) {
        return switch (templateType) {
            case "simples"      -> "relatorio-nutricional-simples";
            case "detalhado"    -> "relatorio-nutricional-detalhado";
            case "comparativo"  -> "relatorio-comparativo";
            case "teste"        -> "teste-atributos";
            default             -> "relatorio-nutricional-padrao";
        };
    }

    // ==============================================================
    // # MÃ©todo - gerarRelatorioComparativoEmPDF
    // # Gera um PDF comparativo com o histÃ³rico evolutivo do paciente
    // # (todas as consultas com avaliaÃ§Ã£o fÃ­sica registrada)
    // ==============================================================
    public byte[] gerarRelatorioComparativoEmPDF(Long pacienteId) throws Exception {
        log.info("### INICIANDO RELATÃ“RIO COMPARATIVO para pacienteId={}", pacienteId);

        // 1. Buscar paciente
        var paciente = pacienteService.buscarPorId(pacienteId);
        Integer idadePaciente = calcularIdadePaciente(paciente);

        // 2. Listar todas as consultas (desc) e inverter para crescente
        var consultasDesc = consultaService.listarConsultasPorPaciente(pacienteId);
        List<ConsultaResumoDTO> consultasAsc = new ArrayList<>(consultasDesc);
        Collections.reverse(consultasAsc);

        // 3. Montar itens comparativos (apenas consultas com avaliaÃ§Ã£o fÃ­sica)
        List<ConsultaComparativaItemDTO> itens = new ArrayList<>();
        int numero = 1;
        DateTimeFormatter fmtData    = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtAbrev   = DateTimeFormatter.ofPattern("MM/yy");

        for (var resumo : consultasAsc) {
            if (!Boolean.TRUE.equals(resumo.getTemAvaliacaoFisica())) continue;
            try {
                AvaliacaoFisicaDTO aval = avaliacaoFisicaService.buscarPorConsulta(resumo.getId());

                QuestionarioEstiloVidaDTO questionario = null;
                try {
                    questionario = questionarioService.buscarPorConsulta(resumo.getId());
                } catch (Exception e) {
                    log.debug("QuestionÃ¡rio ausente na consulta {}", resumo.getId());
                }

                ConsultaComparativaItemDTO item = new ConsultaComparativaItemDTO();
                item.setConsultaId(resumo.getId());
                item.setDataConsulta(resumo.getDataConsulta());
                item.setDataFormatada(resumo.getDataConsulta().format(fmtData));
                item.setDataAbreviada(resumo.getDataConsulta().format(fmtAbrev));
                item.setNumeroConsulta(numero++);
                item.setPeso(aval.getPesoAtual());
                item.setImc(aval.getImc());
                item.setPercentualGordura(aval.getPercentualGordura());
                item.setMassaMagra(aval.getMassaMagra());
                item.setMassaGorda(aval.getMassaGorda());
                item.setPerimetroCintura(aval.getPerimetroCintura());
                item.setPerimetroAbdominal(aval.getPerimetroAbdominal());
                item.setPerimetroQuadril(aval.getPerimetroQuadril());
                item.setPerimetroBracoDireitoRelax(aval.getPerimetroBracoDireitoRelax());
                item.setPerimetroPanturrilhaDireita(aval.getPerimetroPanturrilhaDireita());
                item.setObjetivo(questionario != null ? questionario.getObjetivo() : null);
                itens.add(item);

            } catch (Exception e) {
                log.warn("Erro ao processar consulta {} no comparativo: {}", resumo.getId(), e.getMessage());
            }
        }

        // 4. Montar dados para Chart.js (injetados via th:inline="javascript")
        Map<String, Object> reportData = new LinkedHashMap<>();
        reportData.put("labels",            itens.stream().map(ConsultaComparativaItemDTO::getDataAbreviada).toList());
        reportData.put("peso",              itens.stream().map(ConsultaComparativaItemDTO::getPeso).toList());
        reportData.put("imc",               itens.stream().map(ConsultaComparativaItemDTO::getImc).toList());
        reportData.put("percentualGordura", itens.stream().map(ConsultaComparativaItemDTO::getPercentualGordura).toList());
        reportData.put("massaMagra",        itens.stream().map(ConsultaComparativaItemDTO::getMassaMagra).toList());
        reportData.put("massaGorda",        itens.stream().map(ConsultaComparativaItemDTO::getMassaGorda).toList());
        reportData.put("cintura",           itens.stream().map(ConsultaComparativaItemDTO::getPerimetroCintura).toList());
        reportData.put("abdominal",         itens.stream().map(ConsultaComparativaItemDTO::getPerimetroAbdominal).toList());
        reportData.put("quadril",           itens.stream().map(ConsultaComparativaItemDTO::getPerimetroQuadril).toList());

        // 5. Calcular resumo evolutivo (1Âª vs Ãºltima consulta)
        Map<String, Object> evolucao = new HashMap<>();
        if (itens.size() >= 2) {
            ConsultaComparativaItemDTO primeiro = itens.get(0);
            ConsultaComparativaItemDTO ultimo   = itens.get(itens.size() - 1);
            evolucao.put("primeiraData",  primeiro.getDataFormatada());
            evolucao.put("ultimaData",    ultimo.getDataFormatada());
            evolucao.put("diffPeso",      diffFmt(ultimo.getPeso(), primeiro.getPeso()));
            evolucao.put("diffImc",       diffFmt(ultimo.getImc(), primeiro.getImc()));
            evolucao.put("diffGordura",   diffFmt(ultimo.getPercentualGordura(), primeiro.getPercentualGordura()));
            evolucao.put("diffMassaMagra",diffFmt(ultimo.getMassaMagra(), primeiro.getMassaMagra()));
            evolucao.put("diffMassaGorda",diffFmt(ultimo.getMassaGorda(), primeiro.getMassaGorda()));
            evolucao.put("diffCintura",   diffFmt(ultimo.getPerimetroCintura(), primeiro.getPerimetroCintura()));
            evolucao.put("diffAbdominal", diffFmt(ultimo.getPerimetroAbdominal(), primeiro.getPerimetroAbdominal()));
            evolucao.put("diffQuadril",   diffFmt(ultimo.getPerimetroQuadril(), primeiro.getPerimetroQuadril()));
            // sinais: true = melhora (depende da mÃ©trica)
            evolucao.put("melhoriaPeso",    isMelhora("peso",     ultimo.getPeso(), primeiro.getPeso()));
            evolucao.put("melhoriaGordura", isMelhora("gordura",  ultimo.getPercentualGordura(), primeiro.getPercentualGordura()));
            evolucao.put("melhoriaMagra",   isMelhora("magra",    ultimo.getMassaMagra(), primeiro.getMassaMagra()));
            evolucao.put("melhoriaImc",     isMelhora("imc",      ultimo.getImc(), primeiro.getImc()));
        }

        // 6. Montar contexto Thymeleaf e gerar PDF via Playwright
        Context context = new Context();
        context.setVariable("paciente",             paciente);
        context.setVariable("idadePaciente",         idadePaciente);
        context.setVariable("consultasComparativas", itens);
        context.setVariable("evolucao",              evolucao);
        context.setVariable("totalConsultas",        itens.size());
        context.setVariable("reportData",            reportData);

        String html = templateEngine.process("relatorio-comparativo", context);
        log.info("### RELATÃ“RIO COMPARATIVO gerado com Playwright â€” {} consultas", itens.size());
        return playwrightPdfService.generatePdf(html);
    }


    // ==============================================================
    // # MÃ©todo auxiliar - diffFmt
    // # Formata a diferenÃ§a

    private String diffFmt(Double vFinal, Double vInicial) {
        if (vFinal == null || vInicial == null) return "—";
        double diff = vFinal - vInicial;
        return String.format("%+.2f", diff);
    }

    // ==============================================================
    // # MÃ©todo auxiliar - isMelhora
    // # Retorna true se a variaÃ§Ã£o representa uma melhora clÃ­nica para a mÃ©trica
    // ==============================================================
    private boolean isMelhora(String metrica, Double vFinal, Double vInicial) {
        if (vFinal == null || vInicial == null) return false;
        double diff = vFinal - vInicial;
        return switch (metrica) {
            case "magra" -> diff > 0;           // mais massa magra = melhora
            default      -> diff < 0;           // menos gordura/peso/imc = melhora (geral)
        };
    }

    // ==============================================
    // # MÃ©todo - gerarPDF
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
    // # MÃ©todo - escapeUrl
    // # Escapa caracteres especiais em uma URL (tratamento mÃ­nimo atualmente)
    // ==============================================
    private String escapeUrl(String url) {
        if (url == null) return null;
        // Keep URLs as-is; Thymeleaf link expressions will produce XML-safe attributes.
        return url;
    }

    /**
     * Gera um JSON com todos os dados do relatÃ³rio e envia via HTTP POST para a URL fornecida.
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
        log.info("Enviando JSON do relatÃ³rio para {} - {} bytes", destinationUrl, json.getBytes().length);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(destinationUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        log.info("Resposta do endpoint: status={} body={}", response.statusCode(), response.body());
        // Imprime no console a resposta do n8n (Ãºtil para debug local)
        System.out.println("[N8N RESPONSE] status=" + response.statusCode() + " body=" + response.body());
        return response;
    }
}
