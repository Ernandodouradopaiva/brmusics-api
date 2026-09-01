-- Celebrações (missa e demais). Não altera migrations anteriores.

CREATE SEQUENCE IF NOT EXISTS celebracao_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE celebracao (
    id BIGINT PRIMARY KEY DEFAULT nextval('celebracao_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    local_id BIGINT NOT NULL REFERENCES local(id),
    titulo VARCHAR(255) NOT NULL,
    data DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME,
    descricao VARCHAR(2000),
    observacao VARCHAR(2000),
    status VARCHAR(20) NOT NULL
);

CREATE INDEX idx_celebracao_data ON celebracao (data);
CREATE INDEX idx_celebracao_local ON celebracao (local_id);
CREATE INDEX idx_celebracao_status ON celebracao (status);

CREATE TABLE celebracao_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    local_id BIGINT,
    titulo VARCHAR(255),
    data DATE,
    hora_inicio TIME,
    hora_fim TIME,
    descricao VARCHAR(2000),
    observacao VARCHAR(2000),
    status VARCHAR(20),
    PRIMARY KEY (id, rev)
);

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Celebrações', 'Menu — Celebrações', 'celebracao.menu', 'brmusic', 'celebracao', 'menu', 130, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Celebrações', 'Página — Celebrações', 'celebracao.pagina', 'brmusic', 'celebracao', 'pagina', 131, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Celebrações', 'Listar — Celebrações', 'celebracao.listar', 'brmusic', 'celebracao', 'listar', 132, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Celebrações', 'Visualizar — Celebrações', 'celebracao.visualizar', 'brmusic', 'celebracao', 'visualizar', 133, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Criar — Celebrações', 'Criar — Celebrações', 'celebracao.criar', 'brmusic', 'celebracao', 'criar', 134, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Editar — Celebrações', 'Editar — Celebrações', 'celebracao.editar', 'brmusic', 'celebracao', 'editar', 135, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Excluir — Celebrações', 'Excluir — Celebrações', 'celebracao.excluir', 'brmusic', 'celebracao', 'excluir', 136, TRUE, TRUE, NOW(), NOW())
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
    'celebracao.menu', 'celebracao.pagina', 'celebracao.listar', 'celebracao.visualizar',
    'celebracao.criar', 'celebracao.editar', 'celebracao.excluir'
  )
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
SELECT setval('celebracao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM celebracao), 1));
