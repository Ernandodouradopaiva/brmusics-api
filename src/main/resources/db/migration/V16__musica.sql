-- Catálogo de músicas. Categoria litúrgica em VARCHAR para evolução sem migration de enum.
-- Anexos (cifra PDF, áudio) ficam preparados para MinIO em musica_anexo; upload virá depois.

CREATE SEQUENCE IF NOT EXISTS musica_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS musica_anexo_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE musica (
    id BIGINT PRIMARY KEY DEFAULT nextval('musica_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255),
    interprete_referencia VARCHAR(255),
    tom_padrao VARCHAR(20),
    categoria_liturgica VARCHAR(40),
    letra TEXT,
    cifra TEXT,
    link_referencia VARCHAR(2000),
    observacao VARCHAR(2000),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_musica_titulo_lower ON musica (LOWER(titulo));
CREATE INDEX idx_musica_autor_lower ON musica (LOWER(autor));
CREATE INDEX idx_musica_categoria ON musica (categoria_liturgica);
CREATE INDEX idx_musica_ativo ON musica (ativo);

CREATE TABLE musica_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    titulo VARCHAR(255),
    autor VARCHAR(255),
    interprete_referencia VARCHAR(255),
    tom_padrao VARCHAR(20),
    categoria_liturgica VARCHAR(40),
    letra TEXT,
    cifra TEXT,
    link_referencia VARCHAR(2000),
    observacao VARCHAR(2000),
    ativo BOOLEAN,
    PRIMARY KEY (id, rev)
);

CREATE TABLE musica_anexo (
    id BIGINT PRIMARY KEY DEFAULT nextval('musica_anexo_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    musica_id BIGINT NOT NULL REFERENCES musica(id),
    tipo VARCHAR(40) NOT NULL,
    nome_original VARCHAR(255),
    content_type VARCHAR(120),
    caminho_relativo VARCHAR(500) NOT NULL,
    tamanho_bytes BIGINT
);

CREATE INDEX idx_musica_anexo_musica ON musica_anexo (musica_id);

CREATE TABLE musica_anexo_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    musica_id BIGINT,
    tipo VARCHAR(40),
    nome_original VARCHAR(255),
    content_type VARCHAR(120),
    caminho_relativo VARCHAR(500),
    tamanho_bytes BIGINT,
    PRIMARY KEY (id, rev)
);

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Músicas', 'Menu — Músicas', 'musica.menu', 'brmusic', 'musica', 'menu', 150, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Músicas', 'Página — Músicas', 'musica.pagina', 'brmusic', 'musica', 'pagina', 151, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Músicas', 'Listar — Músicas', 'musica.listar', 'brmusic', 'musica', 'listar', 152, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Músicas', 'Visualizar — Músicas', 'musica.visualizar', 'brmusic', 'musica', 'visualizar', 153, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Criar — Músicas', 'Criar — Músicas', 'musica.criar', 'brmusic', 'musica', 'criar', 154, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Editar — Músicas', 'Editar — Músicas', 'musica.editar', 'brmusic', 'musica', 'editar', 155, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Excluir — Músicas', 'Excluir — Músicas', 'musica.excluir', 'brmusic', 'musica', 'excluir', 156, TRUE, TRUE, NOW(), NOW())
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
    'musica.menu', 'musica.pagina', 'musica.listar', 'musica.visualizar',
    'musica.criar', 'musica.editar', 'musica.excluir'
  )
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
SELECT setval('musica_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM musica), 1));
SELECT setval('musica_anexo_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM musica_anexo), 1));
