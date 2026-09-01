-- Grupo ANALISTA: maior acesso local, atribuído a administradores SSO do PROJETO_A.

INSERT INTO grupo (id, codigo, nome, ativo, data_cadastro, data_atualizacao)
VALUES (
    1,
    '33333333-3333-3333-3333-333333333333',
    'ANALISTA',
    TRUE,
    NOW(),
    NOW()
)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO grupo_permissao (grupo_id, permissao_id)
SELECT 3, p.id
FROM permissao p
WHERE p.chave IN ('USUARIO_LISTAR', 'USUARIO_GERENCIAR')
ON CONFLICT DO NOTHING;

SELECT setval('grupo_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM grupo), 1));