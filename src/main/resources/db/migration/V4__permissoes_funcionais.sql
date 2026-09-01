-- Permissões funcionais (recurso.acao) — padrão HubSocial para o Projeto A.

ALTER TABLE permissao DROP CONSTRAINT IF EXISTS uk_permissao_chave;
ALTER TABLE permissao ADD CONSTRAINT uk_permissao_chave UNIQUE (chave);

DELETE FROM grupo_permissao WHERE permissao_id IN (
    SELECT id FROM permissao WHERE chave IN ('USUARIO_LISTAR', 'USUARIO_GERENCIAR')
);
DELETE FROM permissao WHERE chave IN ('USUARIO_LISTAR', 'USUARIO_GERENCIAR');

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Página inicial', 'Menu — Página inicial', 'inicio.menu', 'geral', 'inicio', 'menu', 0, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Página inicial', 'Página — Página inicial', 'inicio.pagina', 'geral', 'inicio', 'pagina', 1, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Página inicial', 'Listar — Página inicial', 'inicio.listar', 'geral', 'inicio', 'listar', 2, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Página inicial', 'Visualizar — Página inicial', 'inicio.visualizar', 'geral', 'inicio', 'visualizar', 3, TRUE, TRUE, NOW(), NOW()),

    (gen_random_uuid(), 'Menu — Usuários', 'Menu — Usuários', 'usuario.menu', 'administracao', 'usuario', 'menu', 20, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Usuários', 'Página — Usuários', 'usuario.pagina', 'administracao', 'usuario', 'pagina', 21, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Usuários', 'Listar — Usuários', 'usuario.listar', 'administracao', 'usuario', 'listar', 22, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Usuários', 'Visualizar — Usuários', 'usuario.visualizar', 'administracao', 'usuario', 'visualizar', 23, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Criar — Usuários', 'Criar — Usuários', 'usuario.criar', 'administracao', 'usuario', 'criar', 24, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Editar — Usuários', 'Editar — Usuários', 'usuario.editar', 'administracao', 'usuario', 'editar', 25, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Excluir — Usuários', 'Excluir — Usuários', 'usuario.excluir', 'administracao', 'usuario', 'excluir', 26, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Alterar senha de usuário', 'Alterar senha de usuário', 'usuario.alterar-senha', 'administracao', 'usuario', 'alterar-senha', 27, TRUE, TRUE, NOW(), NOW()),

    (gen_random_uuid(), 'Menu — Grupos', 'Menu — Grupos', 'grupo.menu', 'administracao', 'grupo', 'menu', 40, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Grupos', 'Página — Grupos', 'grupo.pagina', 'administracao', 'grupo', 'pagina', 41, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Grupos', 'Listar — Grupos', 'grupo.listar', 'administracao', 'grupo', 'listar', 42, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Grupos', 'Visualizar — Grupos', 'grupo.visualizar', 'administracao', 'grupo', 'visualizar', 43, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Criar — Grupos', 'Criar — Grupos', 'grupo.criar', 'administracao', 'grupo', 'criar', 44, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Editar — Grupos', 'Editar — Grupos', 'grupo.editar', 'administracao', 'grupo', 'editar', 45, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Excluir — Grupos', 'Excluir — Grupos', 'grupo.excluir', 'administracao', 'grupo', 'excluir', 46, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Gerenciar permissões do grupo', 'Gerenciar permissões do grupo', 'grupo.gerenciar-permissoes', 'administracao', 'grupo', 'gerenciar-permissoes', 47, TRUE, TRUE, NOW(), NOW()),

    (gen_random_uuid(), 'Menu — Permissões', 'Menu — Permissões', 'permissao.menu', 'administracao', 'permissao', 'menu', 60, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Permissões', 'Página — Permissões', 'permissao.pagina', 'administracao', 'permissao', 'pagina', 61, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Permissões', 'Listar — Permissões', 'permissao.listar', 'administracao', 'permissao', 'listar', 62, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Permissões', 'Visualizar — Permissões', 'permissao.visualizar', 'administracao', 'permissao', 'visualizar', 63, TRUE, TRUE, NOW(), NOW())
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

-- Grupo ANALISTA (admin SSO) recebe todas as permissões funcionais.
DELETE FROM grupo_permissao WHERE grupo_id = (SELECT id FROM grupo WHERE codigo = '33333333-3333-3333-3333-333333333333');

INSERT INTO grupo_permissao (grupo_id, permissao_id)
SELECT g.id, p.id
FROM grupo g
CROSS JOIN permissao p
WHERE g.codigo = '33333333-3333-3333-3333-333333333333'
  AND p.chave IS NOT NULL
  AND p.chave NOT LIKE 'ROLE_%'
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
