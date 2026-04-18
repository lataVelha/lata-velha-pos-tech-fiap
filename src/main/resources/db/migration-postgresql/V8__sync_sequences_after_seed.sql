-- Sincroniza sequences de IDs após inserts com ID explícito.
-- Não altera os dados existentes; apenas corrige o próximo valor automático.

SELECT setval('role_id_seq', COALESCE((SELECT MAX(id) FROM "role"), 1), true);
SELECT setval('cargo_id_seq', COALESCE((SELECT MAX(id) FROM cargo), 1), true);
SELECT setval('funcionario_id_seq', COALESCE((SELECT MAX(id) FROM funcionario), 1), true);
SELECT setval('proprietario_id_seq', COALESCE((SELECT MAX(id) FROM proprietario), 1), true);
SELECT setval('veiculo_id_seq', COALESCE((SELECT MAX(id) FROM veiculo), 1), true);
SELECT setval('servico_id_seq', COALESCE((SELECT MAX(id) FROM servico), 1), true);
SELECT setval('peca_id_seq', COALESCE((SELECT MAX(id) FROM peca), 1), true);
SELECT setval('ordem_servico_id_seq', COALESCE((SELECT MAX(id) FROM ordem_servico), 1), true);
SELECT setval('EXECUCAO_SERVICO_id_seq', COALESCE((SELECT MAX(id) FROM EXECUCAO_SERVICO), 1), true);
SELECT setval('peca_alocada_id_seq', COALESCE((SELECT MAX(id) FROM peca_alocada), 1), true);
