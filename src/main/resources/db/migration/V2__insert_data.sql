-- ROLES
INSERT INTO ROLE (NOME) VALUES ('ADMIN');
INSERT INTO ROLE (NOME) VALUES ('USER');
INSERT INTO ROLE (NOME) VALUES ('MECANICO');

-- CARGOS
INSERT INTO CARGO (NOME) VALUES ('ADMIN');
INSERT INTO CARGO (NOME) VALUES ('ATENDENTE');
INSERT INTO CARGO (NOME) VALUES ('MECANICO');

-- RELACIONAMENTO CARGO x ROLE

-- ADMIN → tudo
INSERT INTO CARGO_ROLE (CARGO_ID, ROLE_ID) VALUES (1, 1);
INSERT INTO CARGO_ROLE (CARGO_ID, ROLE_ID) VALUES (1, 2);
INSERT INTO CARGO_ROLE (CARGO_ID, ROLE_ID) VALUES (1, 3);

-- ATENDENTE → USER
INSERT INTO CARGO_ROLE (CARGO_ID, ROLE_ID) VALUES (2, 2);

-- MECANICO → MECANICO
INSERT INTO CARGO_ROLE (CARGO_ID, ROLE_ID) VALUES (3, 3);

--NSERTs para o PostgreSQL com a senha 123456 já BCryptada,
-- Usuário Admin
INSERT INTO funcionario (nome, user_name, password, cargo_id)
VALUES (
    'Fiap',
    'admin',
    '$2a$10$rURPebMFMB.LnQm4PN1BTO9f4n2F3Z.yqeMD0z5Y8VMkVX5/ILNVW', -- senha: 123456
    1
);

-- Usuário Atendente
INSERT INTO funcionario (nome, user_name, password, cargo_id)
VALUES (
    'Maria',
    'atendente',
    '$2a$10$rURPebMFMB.LnQm4PN1BTO9f4n2F3Z.yqeMD0z5Y8VMkVX5/ILNVW', -- senha: 123456
    2
);

-- Usuário Mecânico
INSERT INTO funcionario (nome, user_name, password, cargo_id)
VALUES (
    'Jose',
    'mecanico',
    '$2a$10$rURPebMFMB.LnQm4PN1BTO9f4n2F3Z.yqeMD0z5Y8VMkVX5/ILNVW', -- senha: 123456
    3
);