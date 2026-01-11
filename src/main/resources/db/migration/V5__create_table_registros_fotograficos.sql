CREATE TABLE registros_fotograficos (
    id BIGSERIAL PRIMARY KEY,
    consulta_id BIGINT NOT NULL UNIQUE,
    
    foto_anterior VARCHAR(500),
    foto_posterior VARCHAR(500),
    foto_lateral_esquerda VARCHAR(500),
    foto_lateral_direita VARCHAR(500),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_registro_consulta FOREIGN KEY (consulta_id) 
        REFERENCES consultas(id) ON DELETE CASCADE
);

CREATE INDEX idx_registro_consulta ON registros_fotograficos(consulta_id);

COMMENT ON TABLE registros_fotograficos IS 'Registro fotográfico das avaliações físicas';
COMMENT ON COLUMN registros_fotograficos.foto_anterior IS 'URL/caminho da foto frontal';
COMMENT ON COLUMN registros_fotograficos.foto_posterior IS 'URL/caminho da foto das costas';
