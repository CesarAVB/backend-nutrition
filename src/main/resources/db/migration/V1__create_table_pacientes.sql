CREATE TABLE IF NOT EXISTS pacientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL COMMENT 'CPF sem formatação (apenas números)',
    data_nascimento DATE NOT NULL,
    telefone_whatsapp VARCHAR(15),
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='Tabela de cadastro de pacientes';

CREATE INDEX IF NOT EXISTS idx_pacientes_cpf ON pacientes(cpf);
CREATE INDEX IF NOT EXISTS idx_pacientes_nome ON pacientes(nome_completo);
