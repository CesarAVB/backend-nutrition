-- Inserindo pacientes de exemplo
INSERT INTO tbl_pacientes (nome_completo, cpf, data_nascimento, telefone_whatsapp, email) VALUES
('João Silva Santos', '12345678901', '1990-05-15', '21987654321', 'joao.silva@email.com'),
('Maria Oliveira Costa', '98765432109', '1985-08-22', '21976543210', 'maria.oliveira@email.com'),
('Carlos Alberto Lima', '45678912345', '1992-03-10', '21965432109', 'carlos.lima@email.com');

-- Inserindo consultas de exemplo
INSERT INTO tbl_consultas (paciente_id, data_consulta) VALUES
(1, '2024-01-15 09:00:00'),
(1, '2024-03-20 10:30:00'),
(2, '2024-02-10 14:00:00'),
(3, '2024-01-25 11:00:00');

-- Inserindo questionários de exemplo
INSERT INTO tbl_questionarios_estilo_vida (
    consulta_id, objetivo, frequencia_treino, tempo_treino,
    uso_anabolizantes, fuma, qualidade_sono, ingestao_agua_diaria
) VALUES
(1, 'Perda de gordura e ganho de massa magra', '5x por semana', '60 minutos', 0, 0, 'Boa', 2.5),
(2, 'Manutenção do peso e definição muscular', '5x por semana', '60 minutos', 0, 0, 'Ótima', 3.0),
(3, 'Emagrecimento', '3x por semana', '45 minutos', 0, 0, 'Regular', 1.5);

-- Inserindo avaliações físicas de exemplo
INSERT INTO tbl_avaliacoes_fisicas (
    consulta_id, peso_atual, perimetro_cintura, perimetro_quadril,
    dobra_triceps, dobra_abdominal, percentual_gordura, imc,
    massa_magra, massa_gorda
) VALUES
(1, 85.5, 95.0, 102.0, 18.0, 25.0, 22.5, 26.8, 66.3, 19.2),
(2, 82.0, 90.0, 100.0, 15.0, 20.0, 18.5, 25.7, 66.8, 15.2),
(3, 92.0, 105.0, 110.0, 22.0, 32.0, 28.0, 29.5, 66.2, 25.8);

-- Atualizando comentário
ALTER TABLE tbl_pacientes 
COMMENT = 'Dados de exemplo inseridos para teste do sistema';