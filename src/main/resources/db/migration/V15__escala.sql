-- Escala mensal de músicos. Não altera migrations anteriores.
-- Histórico operacional: linhas de escala_musico não são apagadas (ativo=false).

CREATE SEQUENCE IF NOT EXISTS escala_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS escala_musico_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE escala (
    id BIGINT PRIMARY KEY DEFAULT nextval('escala_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    celebracao_id BIGINT NOT NULL UNIQUE REFERENCES celebracao(id),
    status VARCHAR(20) NOT NULL
);

CREATE INDEX idx_escala_status ON escala (status);

CREATE TABLE escala_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    celebracao_id BIGINT,
    status VARCHAR(20),
    PRIMARY KEY (id, rev)
);

CREATE TABLE escala_musico (
    id BIGINT PRIMARY KEY DEFAULT nextval('escala_musico_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    escala_id BIGINT NOT NULL REFERENCES escala(id),
    musico_id BIGINT NOT NULL REFERENCES musico(id),
    instrumento_id BIGINT NOT NULL REFERENCES instrumento(id),
    observacao VARCHAR(2000),
    status_confirmacao VARCHAR(20) NOT NULL,
    ordem INTEGER NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX uk_escala_musico_funcao ON escala_musico (escala_id, musico_id, instrumento_id);
CREATE INDEX idx_escala_musico_escala ON escala_musico (escala_id);
CREATE INDEX idx_escala_musico_musico ON escala_musico (musico_id);

CREATE TABLE escala_musico_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    escala_id BIGINT,
    musico_id BIGINT,
    instrumento_id BIGINT,
    observacao VARCHAR(2000),
    status_confirmacao VARCHAR(20),
    ordem INTEGER,
    ativo BOOLEAN,
    PRIMARY KEY (id, rev)
);

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Escalas', 'Menu — Escalas', 'escala.menu', 'brmusic', 'escala', 'menu', 140, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Escalas', 'Página — Escalas', 'escala.pagina', 'brmusic', 'escala', 'pagina', 141, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Escalas', 'Listar — Escalas', 'escala.listar', 'brmusic', 'escala', 'listar', 142, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Escalas', 'Visualizar — Escalas', 'escala.visualizar', 'brmusic', 'escala', 'visualizar', 143, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Criar — Escalas', 'Criar — Escalas', 'escala.criar', 'brmusic', 'escala', 'criar', 144, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Editar — Escalas', 'Editar — Escalas', 'escala.editar', 'brmusic', 'escala', 'editar', 145, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Excluir — Escalas', 'Excluir — Escalas', 'escala.excluir', 'brmusic', 'escala', 'excluir', 146, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Publicar — Escalas', 'Publicar — Escalas', 'escala.publicar', 'brmusic', 'escala', 'publicar', 147, TRUE, TRUE, NOW(), NOW())
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
    'escala.menu', 'escala.pagina', 'escala.listar', 'escala.visualizar',
    'escala.criar', 'escala.editar', 'escala.excluir', 'escala.publicar'
  )
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
SELECT setval('escala_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala), 1));
SELECT setval('escala_musico_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_musico), 1));
