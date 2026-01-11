CREATE TABLE consultas (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    data_consulta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_consultas_paciente FOREIGN KEY (paciente_id) 
        REFERENCES pacientes(id) ON DELETE CASCADE
);

CREATE INDEX idx_consultas_paciente ON consultas(paciente_id);
CREATE INDEX idx_consultas_data ON consultas(data_consulta DESC);

COMMENT ON TABLE consultas IS 'Registro de cada consulta/avaliação do paciente';