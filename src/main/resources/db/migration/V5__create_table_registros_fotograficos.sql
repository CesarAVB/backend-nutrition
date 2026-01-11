CREATE TABLE registros_fotograficos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    consulta_id BIGINT NOT NULL UNIQUE,
    
    foto_anterior VARCHAR(500) COMMENT 'URL/caminho da foto frontal',
    foto_posterior VARCHAR(500) COMMENT 'URL/caminho da foto das costas',
    foto_lateral_esquerda VARCHAR(500),
    foto_lateral_direita VARCHAR(500),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_registro_consulta FOREIGN KEY (consulta_id) 
        REFERENCES consultas(id) ON DELETE CASCADE
) COMMENT='Registro fotográfico das avaliações físicas';

CREATE INDEX idx_registro_consulta ON registros_fotograficos(consulta_id);