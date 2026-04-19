-- Dados adicionais para cenarios mais ricos de ordem de servico

-- Complementa estoque de item existente sem reduzir quantidade.
INSERT INTO PECA_ESTOQUE (PECA_ID, QUANTIDADE_ARMAZENADA)
VALUES (5, 20)
ON CONFLICT (PECA_ID) DO UPDATE
SET QUANTIDADE_ARMAZENADA = GREATEST(PECA_ESTOQUE.QUANTIDADE_ARMAZENADA, EXCLUDED.QUANTIDADE_ARMAZENADA);

INSERT INTO ORDEM_SERVICO (
    ID,
    PROPRIETARIO_ID,
    VEICULO_ID,
    RECLAMACAO_CLIENTE,
    STATUS,
    INICIADO_EM,
    FINALIZADO_EM,
    ENTREGUE_EM,
    ATUALIZADO_EM,
    ATENDENTE_INICIO_ID,
    MECANICO_FINAL_ID,
    VALOR_TOTAL
) VALUES
(2, 1, 2, 'Puxando para a direita ao frear', 'EM_DIAGNOSTICO', NOW() - INTERVAL '4 days', NULL, NULL, NOW() - INTERVAL '2 days', 2, 3, 350.00),
(3, 2, 3, 'Consumo alto e perda de potencia', 'AGUARDANDO_APROVACAO', NOW() - INTERVAL '7 days', NULL, NULL, NOW() - INTERVAL '6 days', 2, 3, 420.00),
(4, 3, 4, 'Ruido ao esterçar e vibracao no volante', 'EM_EXECUCAO', NOW() - INTERVAL '10 days', NULL, NULL, NOW() - INTERVAL '1 day', 2, 3, 870.00),
(5, 3, 6, 'Troca completa de freios e revisao', 'FINALIZADA', NOW() - INTERVAL '16 days', NOW() - INTERVAL '13 days', NULL, NOW() - INTERVAL '13 days', 2, 3, 1450.00),
(6, 2, 3, 'Revisao preventiva e alinhamento geral', 'ENTREGUE', NOW() - INTERVAL '25 days', NOW() - INTERVAL '22 days', NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days', 2, 3, 980.00)
ON CONFLICT DO NOTHING;

INSERT INTO EXECUCAO_SERVICO (
    ID,
    STATUS_SERVICO,
    ATENDENTE_ID,
    SERVICO_ID,
    OS_ID,
    INICIADO_EM,
    MECANICO_RESPONSAVEL_ID,
    VALOR_MAO_DE_OBRA,
    TERMINADO_EM,
    ATUALIZADO_EM
) VALUES
(3, 'EM_EXECUCAO', 2, 2, 2, NOW() - INTERVAL '3 days', 3, 150.00, NULL, NOW() - INTERVAL '2 days'),
(4, 'AGUARDANDO_PECA', 2, 6, 2, NOW() - INTERVAL '3 days', 3, 110.00, NULL, NOW() - INTERVAL '2 days'),
(5, 'APROVADO', 2, 3, 3, NOW() - INTERVAL '6 days', 3, 130.00, NULL, NOW() - INTERVAL '5 days'),
(6, 'RECUSADO', 2, 4, 3, NOW() - INTERVAL '6 days', 3, 180.00, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(7, 'EM_EXECUCAO', 2, 1, 4, NOW() - INTERVAL '2 days', 3, 140.00, NULL, NOW() - INTERVAL '1 day'),
(8, 'FINALIZADO', 2, 5, 4, NOW() - INTERVAL '4 days', 3, 260.00, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
(9, 'FINALIZADO', 2, 5, 5, NOW() - INTERVAL '15 days', 3, 300.00, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days'),
(10, 'FINALIZADO', 2, 2, 6, NOW() - INTERVAL '24 days', 3, 160.00, NOW() - INTERVAL '22 days', NOW() - INTERVAL '22 days'),
(11, 'FINALIZADO', 2, 1, 5, NOW() - INTERVAL '40 days', 3, 95.00, NOW() - INTERVAL '39 days 23 hours 42 minutes', NOW() - INTERVAL '39 days 23 hours 42 minutes'),
(12, 'FINALIZADO', 2, 2, 6, NOW() - INTERVAL '70 days', 3, 145.00, NOW() - INTERVAL '69 days 22 hours 15 minutes', NOW() - INTERVAL '69 days 22 hours 15 minutes'),
(13, 'FINALIZADO', 2, 3, 5, NOW() - INTERVAL '110 days', 3, 230.00, NOW() - INTERVAL '109 days 19 hours 50 minutes', NOW() - INTERVAL '109 days 19 hours 50 minutes'),
(14, 'FINALIZADO', 2, 4, 6, NOW() - INTERVAL '140 days', 3, 420.00, NOW() - INTERVAL '139 days 12 hours 30 minutes', NOW() - INTERVAL '139 days 12 hours 30 minutes'),
(15, 'FINALIZADO', 2, 5, 5, NOW() - INTERVAL '170 days', 3, 560.00, NOW() - INTERVAL '168 days 10 hours', NOW() - INTERVAL '168 days 10 hours'),
(16, 'FINALIZADO', 2, 6, 6, NOW() - INTERVAL '200 days', 3, 710.00, NOW() - INTERVAL '197 days 16 hours', NOW() - INTERVAL '197 days 16 hours')
ON CONFLICT DO NOTHING;

INSERT INTO PECA_ALOCADA (
    ID,
    EXECUCAO_SERVICO_ID,
    PECA_ID,
    STATUS,
    ATUALIZADO_EM,
    QTD_SOLICITADA,
    QTD_RESERVADA,
    QTD_ENCOMENDADA
) VALUES
(4, 3, 3, 'RESERVADA', NOW() - INTERVAL '2 days', 2, 2, 0),
(5, 4, 6, 'ENCOMENDA', NOW() - INTERVAL '2 days', 1, 0, 1),
(6, 5, 2, 'ORCAMENTO', NOW() - INTERVAL '5 days', 3, 0, 3),
(7, 7, 7, 'PARCIAL', NOW() - INTERVAL '1 day', 2, 1, 1),
(8, 8, 4, 'INSTALADA', NOW() - INTERVAL '2 days', 2, 0, 0),
(9, 9, 5, 'INSTALADA', NOW() - INTERVAL '13 days', 1, 0, 0),
(10, 10, 1, 'INSTALADA', NOW() - INTERVAL '22 days', 1, 0, 0),
(11, 11, 3, 'INSTALADA', NOW() - INTERVAL '39 days 23 hours', 1, 1, 0),
(12, 12, 2, 'INSTALADA', NOW() - INTERVAL '69 days 22 hours', 1, 1, 0),
(13, 13, 1, 'INSTALADA', NOW() - INTERVAL '109 days 19 hours', 1, 1, 0),
(14, 14, 4, 'INSTALADA', NOW() - INTERVAL '139 days 12 hours', 2, 2, 0),
(15, 15, 5, 'INSTALADA', NOW() - INTERVAL '168 days 10 hours', 2, 2, 0),
(16, 16, 6, 'INSTALADA', NOW() - INTERVAL '197 days 16 hours', 1, 1, 0)
ON CONFLICT DO NOTHING;
