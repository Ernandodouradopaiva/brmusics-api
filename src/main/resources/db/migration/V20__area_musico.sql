-- Área do músico: permissões próprias + grupo MUSICO.
-- Não altera migrations anteriores. ANALISTA recebe as novas chaves (coordenador também músico).

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Minha escala', 'Menu — Minha escala', 'minha-escala.menu', 'brmusic', 'minha-escala', 'menu', 180, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Minha escala', 'Página — Minha escala', 'minha-escala.pagina', 'brmusic', 'minha-escala', 'pagina', 181, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Minha escala', 'Listar — Minha escala', 'minha-escala.listar', 'brmusic', 'minha-escala', 'listar', 182, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Minha escala', 'Visualizar — Minha escala', 'minha-escala.visualizar', 'brmusic', 'minha-escala', 'visualizar', 183, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Menu — Meu repertório', 'Menu — Meu repertório', 'meu-repertorio.menu', 'brmusic', 'meu-repertorio', 'menu', 190, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Meu repertório', 'Página — Meu repertório', 'meu-repertorio.pagina', 'brmusic', 'meu-repertorio', 'pagina', 191, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Meu repertório', 'Listar — Meu repertório', 'meu-repertorio.listar', 'brmusic', 'meu-repertorio', 'listar', 192, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Meu repertório', 'Visualizar — Meu repertório', 'meu-repertorio.visualizar', 'brmusic', 'meu-repertorio', 'visualizar', 193, TRUE, TRUE, NOW(), NOW())
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

INSERT INTO grupo (codigo, nome, ativo, data_cadastro, data_atualizacao)
VALUES (
    '44444444-4444-4444-4444-444444444444',
    'MUSICO',
    TRUE,
    NOW(),
    NOW()
)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO grupo_permissao (grupo_id, permissao_id)
SELECT g.id, p.id
FROM grupo g
CROSS JOIN permissao p
WHERE g.codigo = '44444444-4444-4444-4444-444444444444'
  AND p.chave IN (
    'minha-escala.menu', 'minha-escala.pagina', 'minha-escala.listar', 'minha-escala.visualizar',
    'meu-repertorio.menu', 'meu-repertorio.pagina', 'meu-repertorio.listar', 'meu-repertorio.visualizar'
  )
ON CONFLICT DO NOTHING;

INSERT INTO grupo_permissao (grupo_id, permissao_id)
SELECT g.id, p.id
FROM grupo g
CROSS JOIN permissao p
WHERE g.codigo = '33333333-3333-3333-3333-333333333333'
  AND p.chave IN (
    'minha-escala.menu', 'minha-escala.pagina', 'minha-escala.listar', 'minha-escala.visualizar',
    'meu-repertorio.menu', 'meu-repertorio.pagina', 'meu-repertorio.listar', 'meu-repertorio.visualizar'
  )
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
SELECT setval('grupo_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM grupo), 1));
