-- Novos relatórios BRMusics + vínculo ao ADMINISTRADOR GERAL.

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Relatório de músicas', 'Menu — Relatório de músicas', 'relatorio-musicas.menu', 'relatorios', 'relatorio-musicas', 'menu', 100, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Relatório de músicas', 'Página — Relatório de músicas', 'relatorio-musicas.pagina', 'relatorios', 'relatorio-musicas', 'pagina', 101, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Relatório de músicas', 'Listar — Relatório de músicas', 'relatorio-musicas.listar', 'relatorios', 'relatorio-musicas', 'listar', 102, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Relatório de músicas', 'Visualizar — Relatório de músicas', 'relatorio-musicas.visualizar', 'relatorios', 'relatorio-musicas', 'visualizar', 103, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Gerar PDF — Relatório de músicas', 'Gerar PDF — Relatório de músicas', 'relatorio-musicas.gerar', 'relatorios', 'relatorio-musicas', 'gerar', 104, TRUE, TRUE, NOW(), NOW()),

    (gen_random_uuid(), 'Menu — Relatório de músicos', 'Menu — Relatório de músicos', 'relatorio-musicos.menu', 'relatorios', 'relatorio-musicos', 'menu', 110, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Relatório de músicos', 'Página — Relatório de músicos', 'relatorio-musicos.pagina', 'relatorios', 'relatorio-musicos', 'pagina', 111, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Relatório de músicos', 'Listar — Relatório de músicos', 'relatorio-musicos.listar', 'relatorios', 'relatorio-musicos', 'listar', 112, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Relatório de músicos', 'Visualizar — Relatório de músicos', 'relatorio-musicos.visualizar', 'relatorios', 'relatorio-musicos', 'visualizar', 113, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Gerar PDF — Relatório de músicos', 'Gerar PDF — Relatório de músicos', 'relatorio-musicos.gerar', 'relatorios', 'relatorio-musicos', 'gerar', 114, TRUE, TRUE, NOW(), NOW()),

    (gen_random_uuid(), 'Menu — Relatório de músicos por escala', 'Menu — Relatório de músicos por escala', 'relatorio-musicos-escala.menu', 'relatorios', 'relatorio-musicos-escala', 'menu', 120, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Relatório de músicos por escala', 'Página — Relatório de músicos por escala', 'relatorio-musicos-escala.pagina', 'relatorios', 'relatorio-musicos-escala', 'pagina', 121, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Relatório de músicos por escala', 'Listar — Relatório de músicos por escala', 'relatorio-musicos-escala.listar', 'relatorios', 'relatorio-musicos-escala', 'listar', 122, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Relatório de músicos por escala', 'Visualizar — Relatório de músicos por escala', 'relatorio-musicos-escala.visualizar', 'relatorios', 'relatorio-musicos-escala', 'visualizar', 123, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Gerar PDF — Relatório de músicos por escala', 'Gerar PDF — Relatório de músicos por escala', 'relatorio-musicos-escala.gerar', 'relatorios', 'relatorio-musicos-escala', 'gerar', 124, TRUE, TRUE, NOW(), NOW()),

    (gen_random_uuid(), 'Menu — Relatório de escalas', 'Menu — Relatório de escalas', 'relatorio-escalas.menu', 'relatorios', 'relatorio-escalas', 'menu', 130, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Relatório de escalas', 'Página — Relatório de escalas', 'relatorio-escalas.pagina', 'relatorios', 'relatorio-escalas', 'pagina', 131, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Relatório de escalas', 'Listar — Relatório de escalas', 'relatorio-escalas.listar', 'relatorios', 'relatorio-escalas', 'listar', 132, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Relatório de escalas', 'Visualizar — Relatório de escalas', 'relatorio-escalas.visualizar', 'relatorios', 'relatorio-escalas', 'visualizar', 133, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Gerar PDF — Relatório de escalas', 'Gerar PDF — Relatório de escalas', 'relatorio-escalas.gerar', 'relatorios', 'relatorio-escalas', 'gerar', 134, TRUE, TRUE, NOW(), NOW()),

    (gen_random_uuid(), 'Menu — Relatório de repertórios', 'Menu — Relatório de repertórios', 'relatorio-repertorios.menu', 'relatorios', 'relatorio-repertorios', 'menu', 140, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Relatório de repertórios', 'Página — Relatório de repertórios', 'relatorio-repertorios.pagina', 'relatorios', 'relatorio-repertorios', 'pagina', 141, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Relatório de repertórios', 'Listar — Relatório de repertórios', 'relatorio-repertorios.listar', 'relatorios', 'relatorio-repertorios', 'listar', 142, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Relatório de repertórios', 'Visualizar — Relatório de repertórios', 'relatorio-repertorios.visualizar', 'relatorios', 'relatorio-repertorios', 'visualizar', 143, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Gerar PDF — Relatório de repertórios', 'Gerar PDF — Relatório de repertórios', 'relatorio-repertorios.gerar', 'relatorios', 'relatorio-repertorios', 'gerar', 144, TRUE, TRUE, NOW(), NOW()),

    (gen_random_uuid(), 'Menu — Relatório de celebrações por mês', 'Menu — Relatório de celebrações por mês', 'relatorio-celebracoes-mes.menu', 'relatorios', 'relatorio-celebracoes-mes', 'menu', 150, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Relatório de celebrações por mês', 'Página — Relatório de celebrações por mês', 'relatorio-celebracoes-mes.pagina', 'relatorios', 'relatorio-celebracoes-mes', 'pagina', 151, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Relatório de celebrações por mês', 'Listar — Relatório de celebrações por mês', 'relatorio-celebracoes-mes.listar', 'relatorios', 'relatorio-celebracoes-mes', 'listar', 152, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Relatório de celebrações por mês', 'Visualizar — Relatório de celebrações por mês', 'relatorio-celebracoes-mes.visualizar', 'relatorios', 'relatorio-celebracoes-mes', 'visualizar', 153, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Gerar PDF — Relatório de celebrações por mês', 'Gerar PDF — Relatório de celebrações por mês', 'relatorio-celebracoes-mes.gerar', 'relatorios', 'relatorio-celebracoes-mes', 'gerar', 154, TRUE, TRUE, NOW(), NOW()),

    (gen_random_uuid(), 'Menu — Relatório de músicos por funções', 'Menu — Relatório de músicos por funções', 'relatorio-musicos-funcoes.menu', 'relatorios', 'relatorio-musicos-funcoes', 'menu', 160, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Relatório de músicos por funções', 'Página — Relatório de músicos por funções', 'relatorio-musicos-funcoes.pagina', 'relatorios', 'relatorio-musicos-funcoes', 'pagina', 161, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Relatório de músicos por funções', 'Listar — Relatório de músicos por funções', 'relatorio-musicos-funcoes.listar', 'relatorios', 'relatorio-musicos-funcoes', 'listar', 162, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Relatório de músicos por funções', 'Visualizar — Relatório de músicos por funções', 'relatorio-musicos-funcoes.visualizar', 'relatorios', 'relatorio-musicos-funcoes', 'visualizar', 163, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Gerar PDF — Relatório de músicos por funções', 'Gerar PDF — Relatório de músicos por funções', 'relatorio-musicos-funcoes.gerar', 'relatorios', 'relatorio-musicos-funcoes', 'gerar', 164, TRUE, TRUE, NOW(), NOW())
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

INSERT INTO grupo_permissao (grupo_id, permissao_id)
SELECT g.id, p.id
FROM grupo g
CROSS JOIN permissao p
WHERE g.codigo = '33333333-3333-3333-3333-333333333333'
  AND p.chave LIKE 'relatorio-%'
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
