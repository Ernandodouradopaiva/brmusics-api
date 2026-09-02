-- Seed DEV/POC — preenche as tabelas de domínio para testes manuais.
-- Não usar em homologação/produção. Idempotente (reexecutar não duplica).
--
-- Logins criados (senha Teste@123):
--   10000000001  COORDENADOR TESTE   ADMINISTRADOR GERAL
--   10000000002  ANA VOCAL           MUSICO
--   10000000003  BRUNO VIOLAO        MUSICO
--   10000000004  CARLA TECLADO       MUSICO
--   10000000005  DIEGO BATERIA       MUSICO
--   10000000006  ELENA SAXOFONE      MUSICO
-- Músico sem usuário (só WhatsApp): FELIPE PERCUSSAO
--
-- Uso (Postgres 181.215.134.218:5432, banco brmusics):
--   docker run --rm -e PGPASSWORD=SENHA --network host ^
--     -v "%CD%/scripts/seed-dados-teste.sql:/seed.sql:ro" ^
--     postgres:16-alpine psql -h 181.215.134.218 -p 5432 -U postgres -d brmusics -v ON_ERROR_STOP=1 -f /seed.sql

BEGIN;

-- ---------------------------------------------------------------------------
-- Geo
-- ---------------------------------------------------------------------------
INSERT INTO estado (codigo, nome, ibge, sigla, data_cadastro, data_atualizacao)
SELECT '00000000-0000-4000-a000-000000000001'::uuid, 'CEARA', '23', 'CE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM estado WHERE codigo = '00000000-0000-4000-a000-000000000001'::uuid
                   OR sigla = 'CE');

INSERT INTO municipio (codigo, nome, codigoibge, estado_id, data_cadastro, data_atualizacao)
SELECT '00000000-0000-4000-a000-000000000002'::uuid, 'FORTALEZA', 2304400, e.id, NOW(), NOW()
FROM estado e
WHERE (e.codigo = '00000000-0000-4000-a000-000000000001'::uuid OR e.sigla = 'CE')
  AND NOT EXISTS (SELECT 1 FROM municipio WHERE codigo = '00000000-0000-4000-a000-000000000002'::uuid);

INSERT INTO bairro (codigo, nome, municipio_id, data_cadastro, data_atualizacao)
SELECT '00000000-0000-4000-a000-000000000003'::uuid, 'PAPICU', m.id, NOW(), NOW()
FROM municipio m
WHERE m.codigo = '00000000-0000-4000-a000-000000000002'::uuid
  AND NOT EXISTS (SELECT 1 FROM bairro WHERE codigo = '00000000-0000-4000-a000-000000000003'::uuid);

-- ---------------------------------------------------------------------------
-- Usuários de teste (não altera CPF já existente, ex.: ERNANDO)
-- Hash BCrypt de Teste@123
-- ---------------------------------------------------------------------------
INSERT INTO usuario (codigo, nome, cpf, email, senha, ativo, cargo, recebe_email, data_cadastro, data_atualizacao)
SELECT v.codigo::uuid, v.nome, v.cpf, v.email,
       '$2y$10$CTXRDmc6AYO3SGUT3YXAHOTtglIAC4qw4TaiWyf1Ic7eaFCVJMFjS',
       TRUE, v.cargo, TRUE, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-000000000010', 'COORDENADOR TESTE', '10000000001', 'coordenador@brmusic.test', 'COORDENADOR'),
    ('00000000-0000-4000-a000-000000000011', 'ANA VOCAL',         '10000000002', 'ana.vocal@brmusic.test',     'MUSICO'),
    ('00000000-0000-4000-a000-000000000012', 'BRUNO VIOLAO',      '10000000003', 'bruno.violao@brmusic.test',  'MUSICO'),
    ('00000000-0000-4000-a000-000000000013', 'CARLA TECLADO',     '10000000004', 'carla.teclado@brmusic.test', 'MUSICO'),
    ('00000000-0000-4000-a000-000000000014', 'DIEGO BATERIA',     '10000000005', 'diego.bateria@brmusic.test', 'MUSICO'),
    ('00000000-0000-4000-a000-000000000015', 'ELENA SAXOFONE',    '10000000006', 'elena.sax@brmusic.test',     'MUSICO')
) AS v(codigo, nome, cpf, email, cargo)
WHERE NOT EXISTS (SELECT 1 FROM usuario u WHERE u.codigo = v.codigo::uuid OR u.cpf = v.cpf);

INSERT INTO usuario_grupo (usuario_id, grupo_id)
SELECT u.id, g.id
FROM usuario u
JOIN grupo g ON g.codigo = '33333333-3333-3333-3333-333333333333'::uuid
WHERE u.cpf = '10000000001'
ON CONFLICT DO NOTHING;

INSERT INTO usuario_grupo (usuario_id, grupo_id)
SELECT u.id, g.id
FROM usuario u
JOIN grupo g ON g.codigo = '44444444-4444-4444-4444-444444444444'::uuid
WHERE u.cpf IN ('10000000002', '10000000003', '10000000004', '10000000005', '10000000006')
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------------
-- Locais
-- ---------------------------------------------------------------------------
INSERT INTO local (codigo, nome, endereco, bairro, cidade, observacao, ativo, data_cadastro, data_atualizacao)
SELECT v.codigo::uuid, v.nome, v.endereco, v.bairro, v.cidade, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-000000000020', 'PAROQUIA NOSSA SENHORA DA PAZ', 'RUA RAMOS BOTELHO, 1370', 'PAPICU', 'FORTALEZA', 'SEDE PRINCIPAL'),
    ('00000000-0000-4000-a000-000000000021', 'CAPELA SAO JOSE', 'RUA DAS FLORES, 120', 'ALDEOTA', 'FORTALEZA', NULL),
    ('00000000-0000-4000-a000-000000000022', 'COMUNIDADE SAO FRANCISCO', 'AV BEIRA MAR, 800', 'MEIRELES', 'FORTALEZA', 'COMUNIDADE MISSIONARIA')
) AS v(codigo, nome, endereco, bairro, cidade, observacao)
WHERE NOT EXISTS (SELECT 1 FROM local l WHERE l.codigo = v.codigo::uuid OR LOWER(l.nome) = LOWER(v.nome));

-- ---------------------------------------------------------------------------
-- Músicos
-- ---------------------------------------------------------------------------
INSERT INTO musico (codigo, nome, nome_artistico, telefone, whatsapp, email, observacao, ativo, usuario_id, data_cadastro, data_atualizacao)
SELECT v.codigo::uuid, v.nome, v.nome_artistico, v.telefone, v.whatsapp, v.email, v.observacao, TRUE, u.id, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-000000000030', 'ANA VOCAL',      'ANA',     '85998001002', '85998001002', 'ana.vocal@brmusic.test',     'VOZ PRINCIPAL',     '10000000002'),
    ('00000000-0000-4000-a000-000000000031', 'BRUNO VIOLAO',   'BRUNO',   '85998001003', '85998001003', 'bruno.violao@brmusic.test',  'VIOLAO BASE',       '10000000003'),
    ('00000000-0000-4000-a000-000000000032', 'CARLA TECLADO',  'CARLA',   '85998001004', '85998001004', 'carla.teclado@brmusic.test', 'TECLAS E ORGAO',    '10000000004'),
    ('00000000-0000-4000-a000-000000000033', 'DIEGO BATERIA',  'DIEGO',   '85998001005', '85998001005', 'diego.bateria@brmusic.test', NULL,                '10000000005'),
    ('00000000-0000-4000-a000-000000000034', 'ELENA SAXOFONE', 'ELENA',   '85998001006', '85998001006', 'elena.sax@brmusic.test',     'SOPROS',            '10000000006'),
    ('00000000-0000-4000-a000-000000000035', 'COORDENADOR TESTE', 'COORD', '85998001001', '85998001001', 'coordenador@brmusic.test',  'TAMBEM CANTA',      '10000000001')
) AS v(codigo, nome, nome_artistico, telefone, whatsapp, email, observacao, cpf)
JOIN usuario u ON u.cpf = v.cpf
WHERE NOT EXISTS (SELECT 1 FROM musico m WHERE m.codigo = v.codigo::uuid OR m.whatsapp = v.whatsapp);

INSERT INTO musico (codigo, nome, nome_artistico, telefone, whatsapp, email, observacao, ativo, usuario_id, data_cadastro, data_atualizacao)
SELECT '00000000-0000-4000-a000-000000000036'::uuid, 'FELIPE PERCUSSAO', 'FELIPE', '85998001007', '85998001007',
       'felipe.perc@brmusic.test', 'SEM USUARIO NO SISTEMA — RECEBE WHATSAPP', TRUE, NULL, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM musico m
    WHERE m.codigo = '00000000-0000-4000-a000-000000000036'::uuid OR m.whatsapp = '85998001007'
);

INSERT INTO musico_instrumento (musico_id, instrumento_id)
SELECT m.id, i.id
FROM (VALUES
    ('00000000-0000-4000-a000-000000000030', 'VOCAL'),
    ('00000000-0000-4000-a000-000000000030', 'REGÊNCIA'),
    ('00000000-0000-4000-a000-000000000031', 'VIOLÃO'),
    ('00000000-0000-4000-a000-000000000031', 'GUITARRA'),
    ('00000000-0000-4000-a000-000000000032', 'TECLADO'),
    ('00000000-0000-4000-a000-000000000033', 'BATERIA'),
    ('00000000-0000-4000-a000-000000000034', 'SAXOFONE'),
    ('00000000-0000-4000-a000-000000000034', 'FLAUTA'),
    ('00000000-0000-4000-a000-000000000035', 'VOCAL'),
    ('00000000-0000-4000-a000-000000000035', 'REGÊNCIA'),
    ('00000000-0000-4000-a000-000000000036', 'PERCUSSÃO')
) AS v(musico_codigo, instrumento_nome)
JOIN musico m ON m.codigo = v.musico_codigo::uuid
JOIN instrumento i ON i.nome = v.instrumento_nome
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------------
-- Músicas + anexo placeholder
-- ---------------------------------------------------------------------------
INSERT INTO musica (codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica, letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao)
SELECT v.codigo::uuid, v.titulo, v.autor, v.interprete, v.tom, v.categoria, v.letra, v.cifra, NULL, NULL, TRUE, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-000000000040', 'EIS ME AQUI SENHOR', 'PE. JOSE VICENTE', 'COMUNIDADE CATOLICA', 'G', 'ENTRADA',
     'Eis-me aqui, Senhor.' || chr(10) || 'Eis-me aqui, Senhor.', 'G  C  G  D'),
    ('00000000-0000-4000-a000-000000000041', 'SENHOR QUE EU VENHA TE LOUVAR', 'ANONIMO', NULL, 'A', 'GLORIA',
     'Senhor, que eu venha te louvar.', 'A  D  E'),
    ('00000000-0000-4000-a000-000000000042', 'O SENHOR E O MEU PASTOR', 'SALMO 23', NULL, 'D', 'SALMO',
     'O Senhor e o meu pastor, nada me faltara.', 'D  G  A'),
    ('00000000-0000-4000-a000-000000000043', 'ALELUIA', 'TRADICIONAL', NULL, 'C', 'ACLAMACAO',
     'Aleluias ao Senhor.', 'C  F  G'),
    ('00000000-0000-4000-a000-000000000044', 'MINHA VIDA TEM SENTIDO', 'PE. ZEZinho', NULL, 'E', 'OFERTORIO',
     'Minha vida tem sentido quando eu te encontro.', 'E  A  B'),
    ('00000000-0000-4000-a000-000000000045', 'SANTO, SANTO, SANTO', 'TRADICIONAL', NULL, 'G', 'SANTO',
     'Santo, Santo, Santo e o Senhor.', 'G  C  D'),
    ('00000000-0000-4000-a000-000000000046', 'CORDEIRO DE DEUS', 'TRADICIONAL', NULL, 'D', 'CORDEIRO',
     'Cordeiro de Deus que tirais o pecado do mundo.', 'D  A  G'),
    ('00000000-0000-4000-a000-000000000047', 'PAO DA VIDA', 'COMUNIDADE SHALOM', NULL, 'C', 'COMUNHAO',
     'Pao da vida, calice de salvacao.', 'C  G  Am  F'),
    ('00000000-0000-4000-a000-000000000048', 'FICA CONOSCO SENHOR', 'TRADICIONAL', NULL, 'A', 'POS_COMUNHAO',
     'Fica conosco, Senhor, pois a tarde ja vem.', 'A  E  D'),
    ('00000000-0000-4000-a000-000000000049', 'A BENCAO DO DEUS DE ISRAEL', 'ANONIMO', NULL, 'G', 'FINAL',
     'A bencao do Deus de Israel esteja sobre nos.', 'G  D  C'),
    ('00000000-0000-4000-a000-00000000004a', 'PERDAO SENHOR', 'ANONIMO', NULL, 'Em', 'ATO_PENITENCIAL',
     'Perdao, Senhor, eu pequei.', 'Em  Am  B7'),
    ('00000000-0000-4000-a000-00000000004b', 'CANTO MARIANO', 'TRADICIONAL', NULL, 'F', 'OUTRO',
     'Ave Maria, cheia de graca.', 'F  C  Bb')
) AS v(codigo, titulo, autor, interprete, tom, categoria, letra, cifra)
WHERE NOT EXISTS (SELECT 1 FROM musica m WHERE m.codigo = v.codigo::uuid);

INSERT INTO musica_anexo (codigo, musica_id, tipo, nome_original, content_type, caminho_relativo, tamanho_bytes, data_cadastro, data_atualizacao)
SELECT '00000000-0000-4000-a000-000000000060'::uuid, m.id, 'CIFRA', 'eis-me-aqui.pdf', 'application/pdf',
       'sistemas/brmusic/musicas/00000000-0000-4000-a000-000000000040/eis-me-aqui.pdf', 1024, NOW(), NOW()
FROM musica m
WHERE m.codigo = '00000000-0000-4000-a000-000000000040'::uuid
  AND NOT EXISTS (SELECT 1 FROM musica_anexo a WHERE a.codigo = '00000000-0000-4000-a000-000000000060'::uuid);

-- ---------------------------------------------------------------------------
-- Celebrações (datas relativas a hoje para o dashboard)
-- ---------------------------------------------------------------------------
INSERT INTO celebracao (codigo, local_id, titulo, data, hora_inicio, hora_fim, descricao, observacao, status, data_cadastro, data_atualizacao)
SELECT v.codigo::uuid, l.id, v.titulo, v.data, v.hora_inicio::time, v.hora_fim::time, v.descricao, NULL, v.status, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-000000000070', '00000000-0000-4000-a000-000000000020', 'MISSA DOMINICAL',           CURRENT_DATE - 14, '19:00', '20:15', 'MISSA REALIZADA',  'REALIZADA'),
    ('00000000-0000-4000-a000-000000000071', '00000000-0000-4000-a000-000000000020', 'MISSA DOMINICAL',           CURRENT_DATE + 3,  '19:00', '20:15', 'MISSA PRINCIPAL',   'PUBLICADA'),
    ('00000000-0000-4000-a000-000000000072', '00000000-0000-4000-a000-000000000021', 'MISSA VESPERTINA',          CURRENT_DATE + 6,  '18:00', '19:00', 'CAPELA',            'PUBLICADA'),
    ('00000000-0000-4000-a000-000000000073', '00000000-0000-4000-a000-000000000022', 'CELEBRACAO COMUNITARIA',    CURRENT_DATE + 10, '07:00', '08:00', 'MANHA',             'PUBLICADA'),
    ('00000000-0000-4000-a000-000000000074', '00000000-0000-4000-a000-000000000020', 'MISSA CANCELADA',           CURRENT_DATE + 8,  '19:00', '20:00', 'CANCELADA PARA TESTE', 'CANCELADA'),
    ('00000000-0000-4000-a000-000000000075', '00000000-0000-4000-a000-000000000021', 'MISSA EM RASCUNHO',         CURRENT_DATE + 21, '19:00', '20:15', 'AINDA NAO PUBLICADA', 'RASCUNHO')
) AS v(codigo, local_codigo, titulo, data, hora_inicio, hora_fim, descricao, status)
JOIN local l ON l.codigo = v.local_codigo::uuid
WHERE NOT EXISTS (SELECT 1 FROM celebracao c WHERE c.codigo = v.codigo::uuid);

-- ---------------------------------------------------------------------------
-- Escalas
-- ---------------------------------------------------------------------------
INSERT INTO escala (codigo, celebracao_id, status, data_cadastro, data_atualizacao)
SELECT v.codigo::uuid, c.id, v.status, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-000000000080', '00000000-0000-4000-a000-000000000070', 'PUBLICADA'),
    ('00000000-0000-4000-a000-000000000081', '00000000-0000-4000-a000-000000000071', 'PUBLICADA'),
    ('00000000-0000-4000-a000-000000000082', '00000000-0000-4000-a000-000000000072', 'PUBLICADA'),
    ('00000000-0000-4000-a000-000000000083', '00000000-0000-4000-a000-000000000073', 'PUBLICADA'),
    ('00000000-0000-4000-a000-000000000084', '00000000-0000-4000-a000-000000000075', 'RASCUNHO')
) AS v(codigo, celebracao_codigo, status)
JOIN celebracao c ON c.codigo = v.celebracao_codigo::uuid
WHERE NOT EXISTS (SELECT 1 FROM escala e WHERE e.codigo = v.codigo::uuid OR e.celebracao_id = c.id);

INSERT INTO escala_musico (codigo, escala_id, musico_id, instrumento_id, observacao, status_confirmacao, ordem, ativo, data_cadastro, data_atualizacao)
SELECT v.codigo::uuid, e.id, m.id, i.id, v.observacao, v.confirmacao, v.ordem, TRUE, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-000000000090', '00000000-0000-4000-a000-000000000080', '00000000-0000-4000-a000-000000000030', 'VOCAL',    'CONFIRMADO', 0, NULL),
    ('00000000-0000-4000-a000-000000000091', '00000000-0000-4000-a000-000000000080', '00000000-0000-4000-a000-000000000031', 'VIOLÃO',   'CONFIRMADO', 1, NULL),
    ('00000000-0000-4000-a000-000000000092', '00000000-0000-4000-a000-000000000081', '00000000-0000-4000-a000-000000000030', 'VOCAL',    'CONFIRMADO', 0, NULL),
    ('00000000-0000-4000-a000-000000000093', '00000000-0000-4000-a000-000000000081', '00000000-0000-4000-a000-000000000031', 'VIOLÃO',   'PENDENTE',   1, NULL),
    ('00000000-0000-4000-a000-000000000094', '00000000-0000-4000-a000-000000000081', '00000000-0000-4000-a000-000000000032', 'TECLADO',  'CONFIRMADO', 2, NULL),
    ('00000000-0000-4000-a000-000000000095', '00000000-0000-4000-a000-000000000081', '00000000-0000-4000-a000-000000000033', 'BATERIA',  'PENDENTE',   3, NULL),
    ('00000000-0000-4000-a000-000000000096', '00000000-0000-4000-a000-000000000081', '00000000-0000-4000-a000-000000000036', 'PERCUSSÃO','CONFIRMADO', 4, 'SEM LOGIN'),
    ('00000000-0000-4000-a000-000000000097', '00000000-0000-4000-a000-000000000082', '00000000-0000-4000-a000-000000000030', 'VOCAL',    'CONFIRMADO', 0, NULL),
    ('00000000-0000-4000-a000-000000000098', '00000000-0000-4000-a000-000000000082', '00000000-0000-4000-a000-000000000034', 'SAXOFONE', 'RECUSADO',   1, 'VIAGEM'),
    ('00000000-0000-4000-a000-000000000099', '00000000-0000-4000-a000-000000000082', '00000000-0000-4000-a000-000000000032', 'TECLADO',  'PENDENTE',   2, NULL),
    ('00000000-0000-4000-a000-00000000009a', '00000000-0000-4000-a000-000000000083', '00000000-0000-4000-a000-000000000035', 'REGÊNCIA', 'CONFIRMADO', 0, NULL),
    ('00000000-0000-4000-a000-00000000009b', '00000000-0000-4000-a000-000000000083', '00000000-0000-4000-a000-000000000031', 'VIOLÃO',   'PENDENTE',   1, NULL),
    ('00000000-0000-4000-a000-00000000009c', '00000000-0000-4000-a000-000000000084', '00000000-0000-4000-a000-000000000030', 'VOCAL',    'PENDENTE',   0, 'RASCUNHO')
) AS v(codigo, escala_codigo, musico_codigo, instrumento_nome, confirmacao, ordem, observacao)
JOIN escala e ON e.codigo = v.escala_codigo::uuid
JOIN musico m ON m.codigo = v.musico_codigo::uuid
JOIN instrumento i ON i.nome = v.instrumento_nome
WHERE NOT EXISTS (SELECT 1 FROM escala_musico em WHERE em.codigo = v.codigo::uuid);

-- ---------------------------------------------------------------------------
-- Repertórios
-- ---------------------------------------------------------------------------
INSERT INTO repertorio (codigo, celebracao_id, observacao, status, data_cadastro, data_atualizacao)
SELECT v.codigo::uuid, c.id, v.observacao, v.status, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-0000000000a0', '00000000-0000-4000-a000-000000000070', 'REPERTORIO DA MISSA REALIZADA', 'PUBLICADA'),
    ('00000000-0000-4000-a000-0000000000a1', '00000000-0000-4000-a000-000000000071', 'REPERTORIO PRINCIPAL',          'PUBLICADA'),
    ('00000000-0000-4000-a000-0000000000a2', '00000000-0000-4000-a000-000000000072', 'CAPELA — REPERTORIO ENXUTO',   'PUBLICADA'),
    ('00000000-0000-4000-a000-0000000000a3', '00000000-0000-4000-a000-000000000073', 'COMUNIDADE',                    'PUBLICADA'),
    ('00000000-0000-4000-a000-0000000000a4', '00000000-0000-4000-a000-000000000075', 'AINDA EM MONTAGEM',             'RASCUNHO')
) AS v(codigo, celebracao_codigo, observacao, status)
JOIN celebracao c ON c.codigo = v.celebracao_codigo::uuid
WHERE NOT EXISTS (SELECT 1 FROM repertorio r WHERE r.codigo = v.codigo::uuid OR r.celebracao_id = c.id);

INSERT INTO repertorio_item (codigo, repertorio_id, musica_id, momento_liturgico, ordem, tom, observacao, ativo, data_cadastro, data_atualizacao)
SELECT v.codigo::uuid, r.id, mu.id, v.momento, v.ordem, v.tom, NULL, TRUE, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-0000000000b0', '00000000-0000-4000-a000-0000000000a1', '00000000-0000-4000-a000-000000000040', 'ENTRADA',          0, 'G'),
    ('00000000-0000-4000-a000-0000000000b1', '00000000-0000-4000-a000-0000000000a1', '00000000-0000-4000-a000-00000000004a', 'ATO_PENITENCIAL', 1, 'Em'),
    ('00000000-0000-4000-a000-0000000000b2', '00000000-0000-4000-a000-0000000000a1', '00000000-0000-4000-a000-000000000041', 'GLORIA',           2, 'A'),
    ('00000000-0000-4000-a000-0000000000b3', '00000000-0000-4000-a000-0000000000a1', '00000000-0000-4000-a000-000000000043', 'ACLAMACAO',        3, 'C'),
    ('00000000-0000-4000-a000-0000000000b4', '00000000-0000-4000-a000-0000000000a1', '00000000-0000-4000-a000-000000000044', 'OFERTORIO',        4, 'E'),
    ('00000000-0000-4000-a000-0000000000b5', '00000000-0000-4000-a000-0000000000a1', '00000000-0000-4000-a000-000000000045', 'SANTO',            5, 'G'),
    ('00000000-0000-4000-a000-0000000000b6', '00000000-0000-4000-a000-0000000000a1', '00000000-0000-4000-a000-000000000047', 'COMUNHAO',         6, 'C'),
    ('00000000-0000-4000-a000-0000000000b7', '00000000-0000-4000-a000-0000000000a1', '00000000-0000-4000-a000-000000000049', 'FINAL',            7, 'G'),
    ('00000000-0000-4000-a000-0000000000b8', '00000000-0000-4000-a000-0000000000a0', '00000000-0000-4000-a000-000000000040', 'ENTRADA',          0, 'G'),
    ('00000000-0000-4000-a000-0000000000b9', '00000000-0000-4000-a000-0000000000a0', '00000000-0000-4000-a000-000000000047', 'COMUNHAO',         1, 'C'),
    ('00000000-0000-4000-a000-0000000000ba', '00000000-0000-4000-a000-0000000000a2', '00000000-0000-4000-a000-000000000040', 'ENTRADA',          0, 'A'),
    ('00000000-0000-4000-a000-0000000000bb', '00000000-0000-4000-a000-0000000000a2', '00000000-0000-4000-a000-000000000047', 'COMUNHAO',         1, 'D'),
    ('00000000-0000-4000-a000-0000000000bc', '00000000-0000-4000-a000-0000000000a3', '00000000-0000-4000-a000-000000000040', 'ENTRADA',          0, 'G'),
    ('00000000-0000-4000-a000-0000000000bd', '00000000-0000-4000-a000-0000000000a4', '00000000-0000-4000-a000-00000000004b', 'OUTRO',            0, 'F')
) AS v(codigo, repertorio_codigo, musica_codigo, momento, ordem, tom)
JOIN repertorio r ON r.codigo = v.repertorio_codigo::uuid
JOIN musica mu ON mu.codigo = v.musica_codigo::uuid
WHERE NOT EXISTS (SELECT 1 FROM repertorio_item ri WHERE ri.codigo = v.codigo::uuid);

-- ---------------------------------------------------------------------------
-- Publicação mensal (snapshot)
-- ---------------------------------------------------------------------------
INSERT INTO escala_publicacao (
    codigo, ano, mes, versao, publicado_em, publicado_por_id, publicado_por_nome,
    quantidade_celebracoes, quantidade_musicos, quantidade_escalas,
    data_cadastro, data_atualizacao
)
SELECT '00000000-0000-4000-a000-0000000000c0'::uuid,
       EXTRACT(YEAR FROM CURRENT_DATE)::int,
       EXTRACT(MONTH FROM CURRENT_DATE)::int,
       1,
       NOW(),
       u.id,
       u.nome,
       3, 6, 3,
       NOW(), NOW()
FROM usuario u
WHERE u.cpf = '10000000001'
  AND NOT EXISTS (
      SELECT 1 FROM escala_publicacao p
      WHERE p.codigo = '00000000-0000-4000-a000-0000000000c0'::uuid
         OR (p.ano = EXTRACT(YEAR FROM CURRENT_DATE)::int
             AND p.mes = EXTRACT(MONTH FROM CURRENT_DATE)::int
             AND p.versao = 1)
  );

INSERT INTO escala_publicacao_celebracao (
    codigo, publicacao_id, celebracao_codigo, celebracao_titulo, data, hora_inicio, hora_fim, local_nome,
    data_cadastro, data_atualizacao
)
SELECT v.codigo::uuid, p.id, c.codigo, c.titulo, c.data, c.hora_inicio, c.hora_fim, l.nome, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-0000000000c1', '00000000-0000-4000-a000-000000000071'),
    ('00000000-0000-4000-a000-0000000000c2', '00000000-0000-4000-a000-000000000072'),
    ('00000000-0000-4000-a000-0000000000c3', '00000000-0000-4000-a000-000000000073')
) AS v(codigo, celebracao_codigo)
JOIN escala_publicacao p ON p.codigo = '00000000-0000-4000-a000-0000000000c0'::uuid
JOIN celebracao c ON c.codigo = v.celebracao_codigo::uuid
JOIN local l ON l.id = c.local_id
WHERE NOT EXISTS (SELECT 1 FROM escala_publicacao_celebracao x WHERE x.codigo = v.codigo::uuid);

INSERT INTO escala_publicacao_participacao (
    codigo, publicacao_celebracao_id, musico_codigo, musico_nome, instrumento_codigo, instrumento_nome, ordem,
    data_cadastro, data_atualizacao
)
SELECT v.codigo::uuid, pc.id, m.codigo, m.nome, i.codigo, i.nome, v.ordem, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-0000000000d1', '00000000-0000-4000-a000-0000000000c1', '00000000-0000-4000-a000-000000000030', 'VOCAL',     0),
    ('00000000-0000-4000-a000-0000000000d2', '00000000-0000-4000-a000-0000000000c1', '00000000-0000-4000-a000-000000000031', 'VIOLÃO',    1),
    ('00000000-0000-4000-a000-0000000000d3', '00000000-0000-4000-a000-0000000000c1', '00000000-0000-4000-a000-000000000032', 'TECLADO',   2),
    ('00000000-0000-4000-a000-0000000000d4', '00000000-0000-4000-a000-0000000000c1', '00000000-0000-4000-a000-000000000033', 'BATERIA',   3),
    ('00000000-0000-4000-a000-0000000000d5', '00000000-0000-4000-a000-0000000000c1', '00000000-0000-4000-a000-000000000036', 'PERCUSSÃO', 4),
    ('00000000-0000-4000-a000-0000000000d6', '00000000-0000-4000-a000-0000000000c2', '00000000-0000-4000-a000-000000000030', 'VOCAL',     0),
    ('00000000-0000-4000-a000-0000000000d7', '00000000-0000-4000-a000-0000000000c2', '00000000-0000-4000-a000-000000000032', 'TECLADO',   1),
    ('00000000-0000-4000-a000-0000000000d8', '00000000-0000-4000-a000-0000000000c3', '00000000-0000-4000-a000-000000000035', 'REGÊNCIA',  0),
    ('00000000-0000-4000-a000-0000000000d9', '00000000-0000-4000-a000-0000000000c3', '00000000-0000-4000-a000-000000000031', 'VIOLÃO',    1)
) AS v(codigo, pub_cel_codigo, musico_codigo, instrumento_nome, ordem)
JOIN escala_publicacao_celebracao pc ON pc.codigo = v.pub_cel_codigo::uuid
JOIN musico m ON m.codigo = v.musico_codigo::uuid
JOIN instrumento i ON i.nome = v.instrumento_nome
WHERE NOT EXISTS (SELECT 1 FROM escala_publicacao_participacao x WHERE x.codigo = v.codigo::uuid);

INSERT INTO escala_publicacao_repertorio (
    codigo, publicacao_celebracao_id, musica_codigo, musica_titulo, momento_liturgico, ordem, tom,
    data_cadastro, data_atualizacao
)
SELECT v.codigo::uuid, pc.id, mu.codigo, mu.titulo, v.momento, v.ordem, v.tom, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-0000000000e1', '00000000-0000-4000-a000-0000000000c1', '00000000-0000-4000-a000-000000000040', 'ENTRADA',  0, 'G'),
    ('00000000-0000-4000-a000-0000000000e2', '00000000-0000-4000-a000-0000000000c1', '00000000-0000-4000-a000-000000000047', 'COMUNHAO', 1, 'C'),
    ('00000000-0000-4000-a000-0000000000e3', '00000000-0000-4000-a000-0000000000c1', '00000000-0000-4000-a000-000000000049', 'FINAL',    2, 'G'),
    ('00000000-0000-4000-a000-0000000000e4', '00000000-0000-4000-a000-0000000000c2', '00000000-0000-4000-a000-000000000040', 'ENTRADA',  0, 'A'),
    ('00000000-0000-4000-a000-0000000000e5', '00000000-0000-4000-a000-0000000000c3', '00000000-0000-4000-a000-000000000040', 'ENTRADA',  0, 'G')
) AS v(codigo, pub_cel_codigo, musica_codigo, momento, ordem, tom)
JOIN escala_publicacao_celebracao pc ON pc.codigo = v.pub_cel_codigo::uuid
JOIN musica mu ON mu.codigo = v.musica_codigo::uuid
WHERE NOT EXISTS (SELECT 1 FROM escala_publicacao_repertorio x WHERE x.codigo = v.codigo::uuid);

INSERT INTO escala_publicacao_alteracao (
    codigo, publicacao_id, celebracao_codigo, celebracao_titulo, tipo, descricao,
    data_cadastro, data_atualizacao
)
SELECT '00000000-0000-4000-a000-0000000000f0'::uuid, p.id, c.codigo, c.titulo,
       'MUSICO_ADICIONADO', 'FELIPE PERCUSSAO incluido na missa principal.', NOW(), NOW()
FROM escala_publicacao p
JOIN celebracao c ON c.codigo = '00000000-0000-4000-a000-000000000071'::uuid
WHERE p.codigo = '00000000-0000-4000-a000-0000000000c0'::uuid
  AND NOT EXISTS (SELECT 1 FROM escala_publicacao_alteracao a WHERE a.codigo = '00000000-0000-4000-a000-0000000000f0'::uuid);

-- ---------------------------------------------------------------------------
-- WhatsApp (histórico)
-- ---------------------------------------------------------------------------
INSERT INTO whatsapp_envio (
    codigo, musico_id, telefone, tipo_mensagem, mensagem, status, provider_message_id,
    tentativas, erro, data_solicitacao, data_envio, chave_idempotencia,
    data_cadastro, data_atualizacao
)
SELECT v.codigo::uuid, m.id, m.whatsapp, v.tipo, v.mensagem, v.status, v.provider_id,
       v.tentativas, v.erro, NOW() - v.atraso, CASE WHEN v.status = 'ENVIADO' THEN NOW() - v.atraso ELSE NULL END,
       v.chave, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-a000-0000000000f1', '00000000-0000-4000-a000-000000000030', 'PUBLICACAO_ESCALA',
     'ANA, a escala do mes foi publicada. Confira sua participacao.', 'ENVIADO', 'wamid-seed-001', 1, NULL::text,
     interval '2 days', 'seed-wa-ana-pub-1'),
    ('00000000-0000-4000-a000-0000000000f2', '00000000-0000-4000-a000-000000000031', 'NOVA_ESCALA',
     'BRUNO, voce foi escalado na missa principal.', 'ENVIADO', 'wamid-seed-002', 1, NULL::text,
     interval '1 day', 'seed-wa-bruno-nova-1'),
    ('00000000-0000-4000-a000-0000000000f3', '00000000-0000-4000-a000-000000000036', 'PUBLICACAO_ESCALA',
     'FELIPE, a escala foi publicada (musico sem usuario).', 'ENVIADO', 'wamid-seed-003', 1, NULL::text,
     interval '2 days', 'seed-wa-felipe-pub-1'),
    ('00000000-0000-4000-a000-0000000000f4', '00000000-0000-4000-a000-000000000034', 'ALTERACAO_ESCALA',
     'ELENA, houve alteracao na sua escala.', 'ERRO', NULL, 3, 'Falha simulada no provedor.',
     interval '12 hours', 'seed-wa-elena-erro-1'),
    ('00000000-0000-4000-a000-0000000000f5', '00000000-0000-4000-a000-000000000032', 'LEMBRETE',
     'CARLA, lembrete da missa desta semana.', 'PENDENTE', NULL, 0, NULL::text,
     interval '1 hour', 'seed-wa-carla-lemb-1')
) AS v(codigo, musico_codigo, tipo, mensagem, status, provider_id, tentativas, erro, atraso, chave)
JOIN musico m ON m.codigo = v.musico_codigo::uuid
WHERE NOT EXISTS (SELECT 1 FROM whatsapp_envio w WHERE w.codigo = v.codigo::uuid OR w.chave_idempotencia = v.chave);

-- ---------------------------------------------------------------------------
-- Sequences
-- ---------------------------------------------------------------------------
SELECT setval('estado_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM estado), 1));
SELECT setval('municipio_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM municipio), 1));
SELECT setval('bairro_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM bairro), 1));
SELECT setval('usuario_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM usuario), 1));
SELECT setval('musico_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM musico), 1));
SELECT setval('local_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM local), 1));
SELECT setval('celebracao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM celebracao), 1));
SELECT setval('escala_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala), 1));
SELECT setval('escala_musico_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_musico), 1));
SELECT setval('musica_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM musica), 1));
SELECT setval('musica_anexo_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM musica_anexo), 1));
SELECT setval('repertorio_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM repertorio), 1));
SELECT setval('repertorio_item_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM repertorio_item), 1));
SELECT setval('escala_publicacao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_publicacao), 1));
SELECT setval('escala_publicacao_celebracao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_publicacao_celebracao), 1));
SELECT setval('escala_publicacao_participacao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_publicacao_participacao), 1));
SELECT setval('escala_publicacao_repertorio_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_publicacao_repertorio), 1));
SELECT setval('escala_publicacao_alteracao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_publicacao_alteracao), 1));
SELECT setval('whatsapp_envio_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM whatsapp_envio), 1));

COMMIT;
