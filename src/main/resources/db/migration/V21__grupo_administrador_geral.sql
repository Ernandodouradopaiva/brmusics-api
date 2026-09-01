-- Perfil administrativo local: ANALISTA passa a se chamar ADMINISTRADOR GERAL.
-- Preserva o UUID do grupo sistema (33333333-...) e todos os vínculos de permissão.

UPDATE grupo
SET nome = 'ADMINISTRADOR GERAL',
    data_atualizacao = NOW()
WHERE codigo = '33333333-3333-3333-3333-333333333333';
