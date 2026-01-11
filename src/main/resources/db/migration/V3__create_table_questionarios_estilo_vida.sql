CREATE TABLE IF NOT EXISTS questionarios_estilo_vida (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    consulta_id BIGINT NOT NULL UNIQUE,
    
    -- Rotina
    objetivo VARCHAR(500),
    frequencia_treino VARCHAR(255),
    tempo_treino VARCHAR(255),
    
    -- Saúde
    cirurgias TEXT,
    doencas TEXT,
    historico_familiar TEXT,
    medicamentos TEXT,
    suplementos TEXT,
    uso_anabolizantes TINYINT(1) DEFAULT 0,
    ciclo_anabolizantes VARCHAR(255),
    duracao_anabolizantes VARCHAR(255),
    
    -- Hábitos
    fuma TINYINT(1) DEFAULT 0,
    frequencia_alcool VARCHAR(255),
    funcionamento_intestino VARCHAR(255),
    qualidade_sono VARCHAR(255),
    ingestao_agua_diaria DECIMAL(5,2),
    
    -- Preferências
    alimentos_nao_gosta TEXT,
    frutas_preferidas VARCHAR(500),
    numero_refeicoes_desejadas INT,
    horario_maior_fome VARCHAR(255),
    
    -- Clínico
    pressao_arterial VARCHAR(50),
    intolerancias TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_questionario_consulta FOREIGN KEY (consulta_id) 
        REFERENCES consultas(id) ON DELETE CASCADE
) COMMENT='Anamnese subjetiva - questionário de estilo de vida';

CREATE INDEX IF NOT EXISTS idx_questionario_consulta ON questionarios_estilo_vida(consulta_id);
