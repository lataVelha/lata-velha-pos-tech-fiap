-- CARGOS
INSERT INTO CARGO (NOME) VALUES ('ADMIN');
INSERT INTO CARGO (NOME) VALUES ('ATENDENTE');
INSERT INTO CARGO (NOME) VALUES ('MECANICO');

-- ROLES
INSERT INTO ROLE (ID, NOME) VALUES (gen_random_uuid(), 'ADMIN');
INSERT INTO ROLE (ID, NOME) VALUES (gen_random_uuid(), 'USER');
INSERT INTO ROLE (ID, NOME) VALUES (gen_random_uuid(), 'MECANICO');
INSERT INTO ROLE (ID, NOME) VALUES (gen_random_uuid(), 'ATENDENTE');

-- CARGO_ROLE
-- ADMIN → ADMIN, USER, MECANICO, ATENDENTE
INSERT INTO CARGO_ROLE (CARGO_ID, ROLE_ID)
SELECT C.ID, R.ID FROM CARGO C, ROLE R WHERE C.NOME = 'ADMIN' AND R.NOME = 'ADMIN'     UNION ALL
SELECT C.ID, R.ID FROM CARGO C, ROLE R WHERE C.NOME = 'ADMIN' AND R.NOME = 'USER'      UNION ALL
SELECT C.ID, R.ID FROM CARGO C, ROLE R WHERE C.NOME = 'ADMIN' AND R.NOME = 'MECANICO'  UNION ALL
SELECT C.ID, R.ID FROM CARGO C, ROLE R WHERE C.NOME = 'ADMIN' AND R.NOME = 'ATENDENTE';

-- ATENDENTE → USER, ATENDENTE
INSERT INTO CARGO_ROLE (CARGO_ID, ROLE_ID)
SELECT C.ID, R.ID FROM CARGO C, ROLE R WHERE C.NOME = 'ATENDENTE' AND R.NOME = 'USER'      UNION ALL
SELECT C.ID, R.ID FROM CARGO C, ROLE R WHERE C.NOME = 'ATENDENTE' AND R.NOME = 'ATENDENTE';

-- MECANICO → MECANICO
INSERT INTO CARGO_ROLE (CARGO_ID, ROLE_ID)
SELECT C.ID, R.ID FROM CARGO C, ROLE R WHERE C.NOME = 'MECANICO' AND R.NOME = 'MECANICO';

-- USERS (senha: Admin@123 / Atend@123 / Mecan@123)
INSERT INTO USERS (ID, USER_NAME, EMAIL, CREDENTIAL, ATIVO, CRIACAO_DATE) VALUES
    (gen_random_uuid(), 'admin@latavelha.com',     'admin@latavelha.com',     '$2a$10$1dSgICxSKMCZaDflzpaD.Ovyb34nyvz/NfvPsg70gBfNcZ9o4u3UW', TRUE, NOW()),
    (gen_random_uuid(), 'atendente@latavelha.com', 'atendente@latavelha.com', '$2a$10$TCdxfIklS2vW.M5mt1n.FutYI6aK5rE/vpDhNm7VpfS51O2KkpiLq', TRUE, NOW()),
    (gen_random_uuid(), 'mecanico@latavelha.com',  'mecanico@latavelha.com',  '$2a$10$/zxmjorM9.FdmAr6GZ/lcOJNwBodq5XGjtTwx9B7c4lkcYj.2DqDi', TRUE, NOW());

-- USER_ROLES
-- admin → ADMIN, USER, MECANICO, ATENDENTE
INSERT INTO USER_ROLES (USER_ID, ROLE_ID)
SELECT U.ID, R.ID FROM USERS U, ROLE R WHERE U.EMAIL = 'admin@latavelha.com'     AND R.NOME = 'ADMIN'     UNION ALL
SELECT U.ID, R.ID FROM USERS U, ROLE R WHERE U.EMAIL = 'admin@latavelha.com'     AND R.NOME = 'USER'      UNION ALL
SELECT U.ID, R.ID FROM USERS U, ROLE R WHERE U.EMAIL = 'admin@latavelha.com'     AND R.NOME = 'MECANICO'  UNION ALL
SELECT U.ID, R.ID FROM USERS U, ROLE R WHERE U.EMAIL = 'admin@latavelha.com'     AND R.NOME = 'ATENDENTE' UNION ALL
SELECT U.ID, R.ID FROM USERS U, ROLE R WHERE U.EMAIL = 'atendente@latavelha.com' AND R.NOME = 'USER'      UNION ALL
SELECT U.ID, R.ID FROM USERS U, ROLE R WHERE U.EMAIL = 'atendente@latavelha.com' AND R.NOME = 'ATENDENTE' UNION ALL
SELECT U.ID, R.ID FROM USERS U, ROLE R WHERE U.EMAIL = 'mecanico@latavelha.com'  AND R.NOME = 'MECANICO';

-- FUNCIONARIO
INSERT INTO FUNCIONARIO (NOME, CARGO_ID, USER_ID)
SELECT 'Fiap',  C.ID, U.ID FROM CARGO C, USERS U WHERE C.NOME = 'ADMIN'     AND U.EMAIL = 'admin@latavelha.com'     UNION ALL
SELECT 'Maria', C.ID, U.ID FROM CARGO C, USERS U WHERE C.NOME = 'ATENDENTE' AND U.EMAIL = 'atendente@latavelha.com' UNION ALL
SELECT 'Jose',  C.ID, U.ID FROM CARGO C, USERS U WHERE C.NOME = 'MECANICO'  AND U.EMAIL = 'mecanico@latavelha.com';

-- PROPRIETARIOS
INSERT INTO PROPRIETARIO (NOME, EMAIL, DOCUMENTO, NUMERO_CELULAR, RUA, CEP, NUMERO_CASA, ATIVO) VALUES
('Carlos Silva',     'carlos@email.com',       '46924388000', '11999990001', 'Rua das Flores',   '01234567', '100',  TRUE),
('Ana Oliveira',     'ana@email.com',           '57126491018', '11999990002', 'Av Paulista',      '01310100', '1500', TRUE),
('Auto Center Ltda', 'contato@autocenter.com',  '91555965091', '11999990003', 'Rua da Industria', '09876543', '250',  TRUE);

-- VEICULOS
INSERT INTO VEICULO (PROPRIETARIO_ID, PLACA, MARCA, MODELO, ANO, COR, ATIVO) VALUES
(1, 'ABC1D23', 'Fiat',       'Uno',     2020, 'Prata',    TRUE),
(1, 'XYZ4E56', 'Volkswagen', 'Gol',     2019, 'Branco',   TRUE),
(2, 'DEF7G89', 'Toyota',     'Corolla', 2023, 'Preto',    TRUE),
(3, 'GHI1J23', 'Chevrolet',  'Onix',    2022, 'Vermelho', TRUE),
(3, 'JKL4M56', 'Hyundai',    'HB20',    2021, 'Azul',     FALSE),
(3, 'MNO7P89', 'Honda',      'Civic',   2024, 'Cinza',    TRUE);

-- SERVICOS
INSERT INTO SERVICO (ID, NOME, DESCRICAO) VALUES
(1, 'Balanceamento',    'Balanceamento das rodas'),
(2, 'Alinhamento',      'Alinhamento de direção'),
(3, 'Troca de óleo',    'Substituição do óleo do motor'),
(4, 'Troca amortecedor','Substituição do amortecedor dianteiro'),
(5, 'Troca freio',      'Substituição do freio dianteiro'),
(6, 'Troca pivor',      'Substituição pivor');

-- PECAS
INSERT INTO PECA (ID, NOME, DESCRICAO, VALOR) VALUES
(1, 'Filtro de óleo', 'Filtro para motor',                35.00),
(2, 'Óleo 5W30',      'Óleo sintético',                   59.90),
(3, 'Parafuso roda',  'Parafuso fixação roda',             5.00),
(4, 'Disco freio',    'Disco freio roda dianteira',       200.00),
(5, 'Pastilha freio', 'Pastilha de freio roda dianteira', 100.00),
(6, 'Pivor',          'Pivor roda dianteira',              70.00),
(7, 'Amortecedor',    'Amortecedor roda dianteira',       270.00);

-- ESTOQUE
INSERT INTO PECA_ESTOQUE (PECA_ID, QUANTIDADE_ARMAZENADA, QUANTIDADE_DISPONIVEL) VALUES
(1, 50, 50),
(2, 10, 10),
(3, 50, 50),
(4,  5,  5),
(6, 10, 10),
(7,  8,  8);

-- SEQUENCE SYNC (tables seeded with explicit IDs)
SELECT setval('servico_id_seq', COALESCE((SELECT MAX(ID) FROM SERVICO), 1), true);
SELECT setval('peca_id_seq',    COALESCE((SELECT MAX(ID) FROM PECA),    1), true);
