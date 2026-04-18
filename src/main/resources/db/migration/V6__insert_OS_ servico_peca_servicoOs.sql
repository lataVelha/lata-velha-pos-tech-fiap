-- =========================
-- SERVICOS
-- =========================
INSERT INTO SERVICO (ID, NOME, DESCRICAO) VALUES
(1, 'Balanceamento', 'Balanceamento das rodas'),
(2, 'Alinhamento', 'Alinhamento de direção'),
(3, 'Troca de óleo', 'Substituição do óleo do motor'),
(4, 'Troca amortecedor', 'Substituição do amortecedor dianteiro'),
(5, 'Troca freio', 'Substituição do freio dianteiro'),
(6, 'Troca pivor', 'Substituição pivor');

-- =========================
-- PECAS
-- =========================
INSERT INTO PECA (ID, NOME, DESCRICAO, VALOR) VALUES
(1, 'Filtro de óleo', 'Filtro para motor', 35.00),
(2, 'Óleo 5W30', 'Óleo sintético', 59.90),
(3, 'Parafuso roda', 'Parafuso fixação roda', 5.00),
(4, 'Disco freio', 'disco freio roda dianteira', 200.00),
(5, 'Pastilha freio', 'pastilha de  freio roda dianteira', 100.00),
(6, 'Pivor', 'Pivor roda dianteira', 70.00),
(7, 'Amortecedor', 'Amortecedor roda dianteira', 270.00);

-- =========================
-- ESTOQUE
-- =========================
INSERT INTO PECA_ESTOQUE (PECA_ID, QUANTIDADE_ARMAZENADA) VALUES
(1, 50),
(2, 10),
(3, 50),
(4, 5),
(6, 10),
(7, 8);

-- =========================
-- ORDEM SERVICO
-- =========================
INSERT INTO ORDEM_SERVICO (
    ID,
    PROPRIETARIO_ID,
    VEICULO_ID,
    RECLAMACAO_CLIENTE,
    STATUS,
    INICIADO_EM,
    ATUALIZADO_EM,
    ATENDENTE_INICIO_ID,
    VALOR_TOTAL
) VALUES (
    1,
    1,
    1,
    'Barulho na suspensão',
    'RECEBIDA',
    NOW(),
    NOW(),
    2,
    0
);

-- =========================
-- SERVICOS DA OS
-- =========================
INSERT INTO EXECUCAO_SERVICO (
    ID,
    STATUS_SERVICO,
    ATENDENTE_ID,
    SERVICO_ID,
    OS_ID,
    INICIADO_EM,
    MECANICO_RESPONSAVEL_ID,
    VALOR_MAO_DE_OBRA,
    ATUALIZADO_EM
) VALUES
(
    1,
    'PENDENTE',
    2,
    1,
    1,
    NOW(),
    3,
    120.00,
    NOW()
),
(
    2,
    'PENDENTE',
    2,
    3,
    1,
    NULL,
    3,
    80.00,
    NOW()
);

-- =========================
-- PECAS ALOCADAS
-- =========================
INSERT INTO PECA_ALOCADA (
    EXECUCAO_SERVICO_ID,
    PECA_ID,
    STATUS,
    ATUALIZADO_EM,
    QTD_SOLICITADA,
    QTD_RESERVADA,
    QTD_ENCOMENDADA
) VALUES
(1, 3, 'RESERVADA', NOW(), 4, 4, 0),
(2, 1, 'RESERVADA', NOW(), 1, 1, 0),
(2, 2, 'RESERVADA', NOW(), 4, 2, 2);