-- ====================================
-- V6: Inserir Dados de Exemplo
-- ====================================

-- 1. Inserir Pacientes de Exemplo
INSERT INTO tbl_pacientes (nome_completo, cpf, data_nascimento, telefone_whatsapp, email) VALUES
('João Silva Santos', '12345678901', '1990-05-15', '21987654321', 'joao.silva@email.com'),
('Maria Oliveira Costa', '98765432109', '1985-08-22', '21976543210', 'maria.oliveira@email.com'),
('Carlos Alberto Lima', '45678912345', '1992-03-10', '21965432109', 'carlos.lima@email.com'),
('Ana Paula Ferreira', '78912345678', '1988-11-30', '21954321098', 'ana.ferreira@email.com');

-- 2. Inserir Consultas de Exemplo
INSERT INTO tbl_consultas (paciente_id, data_consulta) VALUES
(1, '2024-01-15 09:00:00'),
(1, '2024-03-20 10:30:00'),
(1, '2024-06-10 14:00:00'),
(2, '2024-02-10 14:00:00'),
(2, '2024-05-15 11:30:00'),
(3, '2024-01-25 11:00:00'),
(4, '2024-03-05 15:00:00');

-- 3. Inserir Questionários de Exemplo
INSERT INTO tbl_questionarios_estilo_vida (
    consulta_id, objetivo, frequencia_treino, tempo_treino,
    uso_anabolizantes, fuma, qualidade_sono, ingestao_agua_diaria
) VALUES
(1, 'Perda de gordura e ganho de massa magra', '5x por semana', '60 minutos', FALSE, FALSE, 'Boa', 2.5),
(2, 'Manutenção do peso e definição muscular', '5x por semana', '60 minutos', FALSE, FALSE, 'Ótima', 3.0),
(3, 'Hipertrofia muscular', '6x por semana', '90 minutos', FALSE, FALSE, 'Ótima', 3.5),
(4, 'Emagrecimento', '3x por semana', '45 minutos', FALSE, FALSE, 'Regular', 1.5),
(5, 'Perda de peso', '4x por semana', '50 minutos', FALSE, FALSE, 'Boa', 2.0),
(6, 'Ganho de massa muscular', '5x por semana', '75 minutos', FALSE, FALSE, 'Boa', 2.8);

-- 4. Inserir Avaliações Físicas de Exemplo
INSERT INTO tbl_avaliacoes_fisicas (
    consulta_id, peso_atual, perimetro_cintura, perimetro_quadril,
    perimetro_torax, perimetro_braco_direito_contr,
    dobra_triceps, dobra_abdominal, dobra_subescapular,
    percentual_gordura, imc, massa_magra, massa_gorda
) VALUES
-- João - Consulta 1 (Inicial)
(1, 85.5, 95.0, 102.0, 98.0, 35.0, 18.0, 25.0, 20.0, 22.5, 26.8, 66.3, 19.2),
-- João - Consulta 2 (Após 2 meses)
(2, 82.0, 90.0, 100.0, 96.0, 36.0, 15.0, 20.0, 17.0, 18.5, 25.7, 66.8, 15.2),
-- João - Consulta 3 (Após 5 meses)
(3, 80.5, 88.0, 99.0, 98.0, 37.0, 13.0, 18.0, 15.0, 16.0, 25.2, 67.6, 12.9),
-- Maria - Consulta 1
(4, 92.0, 105.0, 110.0, 95.0, 32.0, 22.0, 32.0, 24.0, 28.0, 29.5, 66.2, 25.8),
-- Maria - Consulta 2
(5, 88.0, 100.0, 108.0, 94.0, 31.0, 20.0, 28.0, 22.0, 25.0, 28.2, 66.0, 22.0),
-- Carlos - Consulta 1
(6, 78.0, 82.0, 95.0, 92.0, 33.0, 10.0, 15.0, 12.0, 12.5, 24.5, 68.3, 9.7);

-- 5. Inserir Registros Fotográficos de Exemplo
INSERT INTO tbl_registros_fotograficos (consulta_id, foto_anterior, foto_posterior) VALUES
(1, 'paciente_1_consulta_1_anterior.jpg', 'paciente_1_consulta_1_posterior.jpg'),
(2, 'paciente_1_consulta_2_anterior.jpg', 'paciente_1_consulta_2_posterior.jpg'),
(3, 'paciente_1_consulta_3_anterior.jpg', 'paciente_1_consulta_3_posterior.jpg'),
(4, 'paciente_2_consulta_1_anterior.jpg', 'paciente_2_consulta_1_posterior.jpg'),
(5, 'paciente_2_consulta_2_anterior.jpg', 'paciente_2_consulta_2_posterior.jpg'),
(6, 'paciente_3_consulta_1_anterior.jpg', 'paciente_3_consulta_1_posterior.jpg');