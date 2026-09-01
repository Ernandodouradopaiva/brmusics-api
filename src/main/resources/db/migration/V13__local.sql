-- Locais (igreja, capela, comunidade). Não altera migrations anteriores.

CREATE SEQUENCE IF NOT EXISTS local_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE local (
    id BIGINT PRIMARY KEY DEFAULT nextval('local_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255) NOT NULL,
    endereco VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    observacao VARCHAR(2000),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX uk_local_nome_lower ON local (LOWER(nome));
CREATE INDEX idx_local_cidade ON local (cidade);

CREATE TABLE local_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255),
    endereco VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    observacao VARCHAR(2000),
    ativo BOOLEAN,
    PRIMARY KEY (id, rev)
);

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Locais', 'Menu — Locais', 'local.menu', 'brmusic', 'local', 'menu', 120, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Locais', 'Página — Locais', 'local.pagina', 'brmusic', 'local', 'pagina', 121, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Locais', 'Listar — Locais', 'local.listar', 'brmusic', 'local', 'listar', 122, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Locais', 'Visualizar — Locais', 'local.visualizar', 'brmusic', 'local', 'visualizar', 123, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Criar — Locais', 'Criar — Locais', 'local.criar', 'brmusic', 'local', 'criar', 124, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Editar — Locais', 'Editar — Locais', 'local.editar', 'brmusic', 'local', 'editar', 125, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Excluir — Locais', 'Excluir — Locais', 'local.excluir', 'brmusic', 'local', 'excluir', 126, TRUE, TRUE, NOW(), NOW())
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
    'local.menu', 'local.pagina', 'local.listar', 'local.visualizar',
    'local.criar', 'local.editar', 'local.excluir'
  )
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
SELECT setval('local_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM local), 1));
