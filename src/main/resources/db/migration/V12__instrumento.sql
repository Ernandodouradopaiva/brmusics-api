-- Instrumentos/funções + N:N com músico. Não altera migrations anteriores.

CREATE SEQUENCE IF NOT EXISTS instrumento_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE instrumento (
    id BIGINT PRIMARY KEY DEFAULT nextval('instrumento_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(2000),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    ordem INTEGER NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uk_instrumento_nome_lower ON instrumento (LOWER(nome));
CREATE INDEX idx_instrumento_ordem ON instrumento (ordem);

CREATE TABLE instrumento_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255),
    descricao VARCHAR(2000),
    ativo BOOLEAN,
    ordem INTEGER,
    PRIMARY KEY (id, rev)
);

CREATE TABLE musico_instrumento (
    musico_id BIGINT NOT NULL REFERENCES musico(id) ON DELETE CASCADE,
    instrumento_id BIGINT NOT NULL REFERENCES instrumento(id),
    PRIMARY KEY (musico_id, instrumento_id)
);

CREATE TABLE musico_instrumento_aud (
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    musico_id BIGINT NOT NULL,
    instrumento_id BIGINT NOT NULL,
    revtype SMALLINT,
    PRIMARY KEY (rev, musico_id, instrumento_id)
);

INSERT INTO instrumento (codigo, nome, descricao, ativo, ordem, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'VOCAL', NULL, TRUE, 1, NOW(), NOW()),
    (gen_random_uuid(), 'VIOLÃO', NULL, TRUE, 2, NOW(), NOW()),
    (gen_random_uuid(), 'GUITARRA', NULL, TRUE, 3, NOW(), NOW()),
    (gen_random_uuid(), 'BAIXO', NULL, TRUE, 4, NOW(), NOW()),
    (gen_random_uuid(), 'TECLADO', NULL, TRUE, 5, NOW(), NOW()),
    (gen_random_uuid(), 'BATERIA', NULL, TRUE, 6, NOW(), NOW()),
    (gen_random_uuid(), 'PERCUSSÃO', NULL, TRUE, 7, NOW(), NOW()),
    (gen_random_uuid(), 'SAXOFONE', NULL, TRUE, 8, NOW(), NOW()),
    (gen_random_uuid(), 'FLAUTA', NULL, TRUE, 9, NOW(), NOW()),
    (gen_random_uuid(), 'REGÊNCIA', NULL, TRUE, 10, NOW(), NOW()),
    (gen_random_uuid(), 'OPERADOR DE SOM', NULL, TRUE, 11, NOW(), NOW());

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Instrumentos e funções', 'Menu — Instrumentos e funções', 'instrumento.menu', 'brmusic', 'instrumento', 'menu', 110, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Instrumentos e funções', 'Página — Instrumentos e funções', 'instrumento.pagina', 'brmusic', 'instrumento', 'pagina', 111, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Instrumentos e funções', 'Listar — Instrumentos e funções', 'instrumento.listar', 'brmusic', 'instrumento', 'listar', 112, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Instrumentos e funções', 'Visualizar — Instrumentos e funções', 'instrumento.visualizar', 'brmusic', 'instrumento', 'visualizar', 113, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Criar — Instrumentos e funções', 'Criar — Instrumentos e funções', 'instrumento.criar', 'brmusic', 'instrumento', 'criar', 114, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Editar — Instrumentos e funções', 'Editar — Instrumentos e funções', 'instrumento.editar', 'brmusic', 'instrumento', 'editar', 115, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Excluir — Instrumentos e funções', 'Excluir — Instrumentos e funções', 'instrumento.excluir', 'brmusic', 'instrumento', 'excluir', 116, TRUE, TRUE, NOW(), NOW())
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
  AND p.chave IN (
    'instrumento.menu', 'instrumento.pagina', 'instrumento.listar', 'instrumento.visualizar',
    'instrumento.criar', 'instrumento.editar', 'instrumento.excluir'
  )
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
SELECT setval('instrumento_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM instrumento), 1));
