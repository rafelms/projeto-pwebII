-- =====================================================
-- Dados iniciais para testar a herança JPA (JOINED)
-- =====================================================
-- Com a estratégia JOINED, cada classe da hierarquia tem sua própria tabela.
-- Para inserir um Paciente, é preciso inserir nas 3 tabelas da hierarquia:
--   1. pessoa (atributos de Pessoa: id, email, telefone)
--   2. pessoa_fisica (atributos de PessoaFisica: nome, cpf)
--   3. paciente (atributos específicos do Paciente)
-- O mesmo vale para Medico.

-- =====================================================
-- Pacientes (inserindo nas 3 tabelas da hierarquia)
-- =====================================================

-- Paciente 1: João Silva
INSERT INTO pessoa (id, email, telefone) VALUES (1, 'joao.silva@email.com', '(11) 99999-1111');
INSERT INTO pessoa_fisica (id, nome, cpf) VALUES (1, 'João Silva', '111.222.333-44');
INSERT INTO paciente (id) VALUES (1);

-- Paciente 2: Maria Oliveira
INSERT INTO pessoa (id, email, telefone) VALUES (2, 'maria.oliveira@email.com', '(21) 98888-2222');
INSERT INTO pessoa_fisica (id, nome, cpf) VALUES (2, 'Maria Oliveira', '555.666.777-88');
INSERT INTO paciente (id) VALUES (2);

-- Paciente 3: Carlos Santos
INSERT INTO pessoa (id, email, telefone) VALUES (3, 'carlos.santos@email.com', '(31) 97777-3333');
INSERT INTO pessoa_fisica (id, nome, cpf) VALUES (3, 'Carlos Santos', '999.000.111-22');
INSERT INTO paciente (id) VALUES (3);

-- =====================================================
-- Médicos (inserindo nas 3 tabelas da hierarquia)
-- =====================================================

-- Medico 1: Dra. Ana Costa
INSERT INTO pessoa (id, email, telefone) VALUES (4, 'ana.costa@clinica.com', '(11) 91111-4444');
INSERT INTO pessoa_fisica (id, nome, cpf) VALUES (4, 'Dra. Ana Costa', '333.444.555-66');
INSERT INTO medico (id, crm) VALUES (4, 'CRM-SP 12345');

-- Medico 2: Dr. Pedro Lima
INSERT INTO pessoa (id, email, telefone) VALUES (5, 'pedro.lima@clinica.com', '(21) 92222-5555');
INSERT INTO pessoa_fisica (id, nome, cpf) VALUES (5, 'Dr. Pedro Lima', '777.888.999-00');
INSERT INTO medico (id, crm) VALUES (5, 'CRM-RJ 67890');

-- Medico 3: Dra. Fernanda Souza
INSERT INTO pessoa (id, email, telefone) VALUES (6, 'fernanda.souza@clinica.com', '(31) 93333-6666');
INSERT INTO pessoa_fisica (id, nome, cpf) VALUES (6, 'Dra. Fernanda Souza', '123.456.789-01');
INSERT INTO medico (id, crm) VALUES (6, 'CRM-MG 11223');

-- =====================================================
-- Pessoa Jurídica (para demonstrar que a herança funciona
-- também para outros tipos de pessoa)
-- =====================================================

INSERT INTO pessoa (id, email, telefone) VALUES (7, 'contato@clinicasaude.com.br', '(11) 3333-7777');
INSERT INTO pessoa_juridica (id, razao_social, cnpj) VALUES (7, 'Clínica Saúde Total LTDA', '12.345.678/0001-90');

-- =====================================================
-- Consultas (vinculando pacientes e médicos)
-- =====================================================
-- Nota: os IDs dos pacientes são 1, 2 e 3.
--       os IDs dos médicos são 4, 5 e 6.

INSERT INTO consulta (data, valor, observacao, paciente_id, medico_id) VALUES ('2026-09-01 09:00:00', 150.00, 'Consulta de rotina', 1, 4);
INSERT INTO consulta (data, valor, observacao, paciente_id, medico_id) VALUES ('2026-09-02 14:30:00', 200.00, 'Retorno exames', 2, 5);
INSERT INTO consulta (data, valor, observacao, paciente_id, medico_id) VALUES ('2026-09-03 10:00:00', 180.00, 'Primeira consulta', 3, 6);
INSERT INTO consulta (data, valor, observacao, paciente_id, medico_id) VALUES ('2026-09-05 11:00:00', 150.00, 'Acompanhamento', 1, 5);

-- =====================================================
-- Reinicia o contador de IDs da tabela pessoa para evitar
-- conflito de chave primária ao cadastrar novos registros.
-- O valor 8 é o próximo após o último ID inserido manualmente (7).
-- =====================================================
ALTER TABLE pessoa ALTER COLUMN id RESTART WITH 8;
