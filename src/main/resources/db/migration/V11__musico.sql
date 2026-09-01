-- Módulo de músicos: tabela de domínio + permissões funcionais vinculadas ao grupo ANALISTA.
-- Premissa: V10 já existe (autenticação local); esta migration inicia em V11.

CREATE SEQUENCE IF NOT EXISTS musico_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE musico (
    id BIGINT PRIMARY KEY DEFAULT nextval('musico_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255) NOT NULL,
    nome_artistico VARCHAR(255),
    telefone VARCHAR(20),
    whatsapp VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    observacao VARCHAR(2000),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    usuario_id BIGINT UNIQUE REFERENCES usuario(id)
);

CREATE UNIQUE INDEX uk_musico_whatsapp ON musico (whatsapp);
CREATE INDEX idx_musico_nome ON musico (nome);

CREATE TABLE musico_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255),
    nome_artistico VARCHAR(255),
    telefone VARCHAR(20),
    whatsapp VARCHAR(20),
    email VARCHAR(255),
    observacao VARCHAR(2000),
    ativo BOOLEAN,
    usuario_id BIGINT,
    PRIMARY KEY (id, rev)
);

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Músicos', 'Menu — Músicos', 'musico.menu', 'brmusic', 'musico', 'menu', 100, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Músicos', 'Página — Músicos', 'musico.pagina', 'brmusic', 'musico', 'pagina', 101, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Músicos', 'Listar — Músicos', 'musico.listar', 'brmusic', 'musico', 'listar', 102, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Músicos', 'Visualizar — Músicos', 'musico.visualizar', 'brmusic', 'musico', 'visualizar', 103, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Criar — Músicos', 'Criar — Músicos', 'musico.criar', 'brmusic', 'musico', 'criar', 104, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Editar — Músicos', 'Editar — Músicos', 'musico.editar', 'brmusic', 'musico', 'editar', 105, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Excluir — Músicos', 'Excluir — Músicos', 'musico.excluir', 'brmusic', 'musico', 'excluir', 106, TRUE, TRUE, NOW(), NOW())
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
    'musico.menu', 'musico.pagina', 'musico.listar', 'musico.visualizar',
    'musico.criar', 'musico.editar', 'musico.excluir'
  )
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
SELECT setval('musico_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM musico), 1));
