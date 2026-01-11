CREATE TABLE tbl_consultas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    data_consulta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_consultas_paciente FOREIGN KEY (paciente_id) 
        REFERENCES tbl_pacientes(id) ON DELETE CASCADE
) COMMENT='Registro de cada consulta/avaliação do paciente';

-- Índices atualizados com o padrão tbl_
CREATE INDEX idx_tbl_consultas_paciente ON tbl_consultas(paciente_id);
CREATE INDEX idx_tbl_consultas_data ON tbl_consultas(data_consulta);