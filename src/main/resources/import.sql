-- Dados iniciais de Pacientes
INSERT INTO paciente (nome, telefone) VALUES ('João Silva', '(11) 99999-1111');
INSERT INTO paciente (nome, telefone) VALUES ('Maria Oliveira', '(21) 98888-2222');
INSERT INTO paciente (nome, telefone) VALUES ('Carlos Santos', '(31) 97777-3333');

-- Dados iniciais de Médicos
INSERT INTO medico (nome, crm) VALUES ('Dr. Ana Costa', 'CRM-SP 12345');
INSERT INTO medico (nome, crm) VALUES ('Dr. Pedro Lima', 'CRM-RJ 67890');
INSERT INTO medico (nome, crm) VALUES ('Dra. Fernanda Souza', 'CRM-MG 11223');

-- Dados iniciais de Consultas
INSERT INTO consulta (data, valor, observacao, paciente_id, medico_id) VALUES ('2026-09-01 09:00:00', 150.00, 'Consulta de rotina', 1, 1);
INSERT INTO consulta (data, valor, observacao, paciente_id, medico_id) VALUES ('2026-09-02 14:30:00', 200.00, 'Retorno exames', 2, 2);
INSERT INTO consulta (data, valor, observacao, paciente_id, medico_id) VALUES ('2026-09-03 10:00:00', 180.00, 'Primeira consulta', 3, 3);
INSERT INTO consulta (data, valor, observacao, paciente_id, medico_id) VALUES ('2026-09-05 11:00:00', 150.00, 'Acompanhamento', 1, 2);
