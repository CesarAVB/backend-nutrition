CREATE TABLE questionarios_estilo_vida (
    id BIGSERIAL PRIMARY KEY,
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
    uso_anabolizantes BOOLEAN DEFAULT FALSE,
    ciclo_anabolizantes VARCHAR(255),
    duracao_anabolizantes VARCHAR(255),
    
    -- Hábitos
    fuma BOOLEAN DEFAULT FALSE,
    frequencia_alcool VARCHAR(255),
    funcionamento_intestino VARCHAR(255),
    qualidade_sono VARCHAR(255),
    ingestao_agua_diaria DECIMAL(5,2),
    
    -- Preferências
    alimentos_nao_gosta TEXT,
    frutas_preferidas VARCHAR(500),
    numero_refeicoes_desejadas INTEGER,
    horario_maior_fome VARCHAR(255),
    
    -- Clínico
    pressao_arterial VARCHAR(50),
    intolerancias TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_questionario_consulta FOREIGN KEY (consulta_id) 
        REFERENCES consultas(id) ON DELETE CASCADE
);

CREATE INDEX idx_questionario_consulta ON questionarios_estilo_vida(consulta_id);

COMMENT ON TABLE questionarios_estilo_vida IS 'Anamnese subjetiva - questionário de estilo de vida';
