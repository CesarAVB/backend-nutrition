CREATE TABLE avaliacoes_fisicas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    consulta_id BIGINT NOT NULL UNIQUE,
    
    -- Perímetros (cm)
    perimetro_ombro DECIMAL(5,2),
    perimetro_torax DECIMAL(5,2),
    perimetro_cintura DECIMAL(5,2),
    perimetro_abdominal DECIMAL(5,2),
    perimetro_quadril DECIMAL(5,2),
    perimetro_braco_direito_relax DECIMAL(5,2),
    perimetro_braco_direito_contr DECIMAL(5,2),
    perimetro_braco_esquerdo_relax DECIMAL(5,2),
    perimetro_braco_esquerdo_contr DECIMAL(5,2),
    perimetro_antebraco_direito DECIMAL(5,2),
    perimetro_antebraco_esquerdo DECIMAL(5,2),
    perimetro_coxa DECIMAL(5,2),
    perimetro_coxa_direita DECIMAL(5,2),
    perimetro_coxa_esquerda DECIMAL(5,2),
    perimetro_panturrilha_direita DECIMAL(5,2),
    perimetro_panturrilha_esquerda DECIMAL(5,2),
    
    -- Dobras Cutâneas (mm)
    dobra_triceps DECIMAL(5,2),
    dobra_peito DECIMAL(5,2),
    dobra_axilar_media DECIMAL(5,2),
    dobra_subescapular DECIMAL(5,2),
    dobra_abdominal DECIMAL(5,2),
    dobra_supra_iliaca DECIMAL(5,2),
    dobra_coxa DECIMAL(5,2),
    
    -- Composição Corporal
    peso_atual DECIMAL(6,2),
    massa_magra DECIMAL(6,2),
    massa_gorda DECIMAL(6,2),
    percentual_gordura DECIMAL(5,2),
    imc DECIMAL(5,2),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_avaliacao_consulta FOREIGN KEY (consulta_id) 
        REFERENCES consultas(id) ON DELETE CASCADE
) COMMENT='Anamnese objetiva - medidas antropométricas e composição corporal';

CREATE INDEX idx_avaliacao_consulta ON avaliacoes_fisicas(consulta_id);