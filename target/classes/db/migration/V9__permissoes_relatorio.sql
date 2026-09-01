-- Permissões de Relatórios (padrão HubSocial) + vínculo ao grupo ANALISTA.

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Relatórios', 'Menu — Relatórios', 'relatorio.menu', 'relatorios', 'relatorio', 'menu', 80, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Relatórios', 'Página — Relatórios', 'relatorio.pagina', 'relatorios', 'relatorio', 'pagina', 81, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Relatórios', 'Listar — Relatórios', 'relatorio.listar', 'relatorios', 'relatorio', 'listar', 82, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Relatórios', 'Visualizar — Relatórios', 'relatorio.visualizar', 'relatorios', 'relatorio', 'visualizar', 83, TRUE, TRUE, NOW(), NOW()),

    (gen_random_uuid(), 'Menu — Relatório de usuários', 'Menu — Relatório de usuários', 'relatorio-usuarios.menu', 'relatorios', 'relatorio-usuarios', 'menu', 90, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Relatório de usuários', 'Página — Relatório de usuários', 'relatorio-usuarios.pagina', 'relatorios', 'relatorio-usuarios', 'pagina', 91, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Relatório de usuários', 'Listar — Relatório de usuários', 'relatorio-usuarios.listar', 'relatorios', 'relatorio-usuarios', 'listar', 92, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Relatório de usuários', 'Visualizar — Relatório de usuários', 'relatorio-usuarios.visualizar', 'relatorios', 'relatorio-usuarios', 'visualizar', 93, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Gerar PDF — Relatório de usuários', 'Gerar PDF — Relatório de usuários', 'relatorio-usuarios.gerar', 'relatorios', 'relatorio-usuarios', 'gerar', 94, TRUE, TRUE, NOW(), NOW())
ON CONFLICT (chave) DO UPDATE SET
    nome = EXCLUDED.nome,
    descricao = EXCLUDED.descricao,
    modulo = EXCLUDED.modulo,
    recurso = EXCLUDED.recurso,
    acao = EXCLUDED.acao,
    ordem = EXCLUDED.ordem,
    sistema = EXCLUDED.sistema,
    ativo = EXCLUDED.ativo,
    data_atualizacao = NOW();

-- Grupo ANALISTA recebe as novas permissões de relatório.
INSERT INTO grupo_permissao (grupo_id, permissao_id)
SELECT g.id, p.id
FROM grupo g
CROSS JOIN permissao p
WHERE g.codigo = '33333333-3333-3333-3333-333333333333'
  AND p.chave IN (
    'relatorio.menu', 'relatorio.pagina', 'relatorio.listar', 'relatorio.visualizar',
    'relatorio-usuarios.menu', 'relatorio-usuarios.pagina', 'relatorio-usuarios.listar',
    'relatorio-usuarios.visualizar', 'relatorio-usuarios.gerar'
  )
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
