CREATE TABLE IF NOT EXISTS consultas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    data_consulta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_consultas_paciente FOREIGN KEY (paciente_id) 
        REFERENCES pacientes(id) ON DELETE CASCADE
) COMMENT='Registro de cada consulta/avaliação do paciente';

CREATE INDEX IF NOT EXISTS idx_consultas_paciente ON consultas(paciente_id);
CREATE INDEX IF NOT EXISTS idx_consultas_data ON consultas(data_consulta);
