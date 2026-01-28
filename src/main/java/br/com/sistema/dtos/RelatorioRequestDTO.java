package br.com.sistema.dtos;

public class RelatorioRequestDTO {
    private Long pacienteId;
    private Long consultaId;
    private String templateType; // "padrao", "simples", "detalhado"

    public Long getPacienteId() {
        return pacienteId;
    }
    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }
    public Long getConsultaId() {
        return consultaId;
    }
    public void setConsultaId(Long consultaId) {
        this.consultaId = consultaId;
    }
    public String getTemplateType() {
        return templateType;
    }
    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }
}