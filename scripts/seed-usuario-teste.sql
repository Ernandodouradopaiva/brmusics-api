-- Referência DEV/POC — não usar em produção.
-- Seed completo (músicos, escalas, repertórios, publicação, WhatsApp):
--   scripts/seed-dados-teste.sql
--
-- Em profile=dev o DevAuthBootstrap cria o admin (CPF 00000000000 / senha Admin@123)
-- se nenhum usuário tiver senha local.

BEGIN;

INSERT INTO usuario (
    id, codigo, nome, cpf, email, ativo, cargo, data_cadastro, data_atualizacao
) VALUES (
    1,
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    'ADMINISTRADOR TESTE',
    '00000000000',
    'admin@local.test',
    TRUE,
    'ADMINISTRADOR',
    NOW(),
    NOW()
)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO usuario_grupo (usuario_id, grupo_id)
SELECT 1, g.id FROM grupo g WHERE g.codigo = '33333333-3333-3333-3333-333333333333'
ON CONFLICT DO NOTHING;

SELECT setval('usuario_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM usuario), 1));

COMMIT;
