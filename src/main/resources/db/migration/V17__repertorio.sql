-- Repertório da celebração. Tom do item NÃO altera tom_padrao da música.

CREATE SEQUENCE IF NOT EXISTS repertorio_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS repertorio_item_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE repertorio (
    id BIGINT PRIMARY KEY DEFAULT nextval('repertorio_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    celebracao_id BIGINT NOT NULL UNIQUE REFERENCES celebracao(id),
    observacao VARCHAR(2000),
    status VARCHAR(20) NOT NULL
);

CREATE INDEX idx_repertorio_status ON repertorio (status);

CREATE TABLE repertorio_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    celebracao_id BIGINT,
    observacao VARCHAR(2000),
    status VARCHAR(20),
    PRIMARY KEY (id, rev)
);

CREATE TABLE repertorio_item (
    id BIGINT PRIMARY KEY DEFAULT nextval('repertorio_item_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    repertorio_id BIGINT NOT NULL REFERENCES repertorio(id),
    musica_id BIGINT NOT NULL REFERENCES musica(id),
    momento_liturgico VARCHAR(40) NOT NULL,
    ordem INTEGER NOT NULL DEFAULT 0,
    tom VARCHAR(20),
    observacao VARCHAR(2000),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_repertorio_item_repertorio ON repertorio_item (repertorio_id);
CREATE INDEX idx_repertorio_item_musica ON repertorio_item (musica_id);
CREATE INDEX idx_repertorio_item_momento ON repertorio_item (repertorio_id, momento_liturgico, ordem);

CREATE TABLE repertorio_item_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    repertorio_id BIGINT,
    musica_id BIGINT,
    momento_liturgico VARCHAR(40),
    ordem INTEGER,
    tom VARCHAR(20),
    observacao VARCHAR(2000),
    ativo BOOLEAN,
    PRIMARY KEY (id, rev)
);

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Repertórios', 'Menu — Repertórios', 'repertorio.menu', 'brmusic', 'repertorio', 'menu', 160, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Repertórios', 'Página — Repertórios', 'repertorio.pagina', 'brmusic', 'repertorio', 'pagina', 161, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Repertórios', 'Listar — Repertórios', 'repertorio.listar', 'brmusic', 'repertorio', 'listar', 162, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Repertórios', 'Visualizar — Repertórios', 'repertorio.visualizar', 'brmusic', 'repertorio', 'visualizar', 163, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Criar — Repertórios', 'Criar — Repertórios', 'repertorio.criar', 'brmusic', 'repertorio', 'criar', 164, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Editar — Repertórios', 'Editar — Repertórios', 'repertorio.editar', 'brmusic', 'repertorio', 'editar', 165, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Excluir — Repertórios', 'Excluir — Repertórios', 'repertorio.excluir', 'brmusic', 'repertorio', 'excluir', 166, TRUE, TRUE, NOW(), NOW())
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
    'repertorio.menu', 'repertorio.pagina', 'repertorio.listar', 'repertorio.visualizar',
    'repertorio.criar', 'repertorio.editar', 'repertorio.excluir'
  )
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
SELECT setval('repertorio_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM repertorio), 1));
SELECT setval('repertorio_item_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM repertorio_item), 1));
