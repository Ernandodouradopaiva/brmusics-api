-- Frequência semanal dos músicos (4 reuniões/mês) + permissões.

CREATE SEQUENCE IF NOT EXISTS frequencia_mensal_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE frequencia_mensal (
    id BIGINT PRIMARY KEY DEFAULT nextval('frequencia_mensal_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    musico_id BIGINT NOT NULL REFERENCES musico(id),
    ano INTEGER NOT NULL,
    mes INTEGER NOT NULL,
    semana1 VARCHAR(30),
    semana2 VARCHAR(30),
    semana3 VARCHAR(30),
    semana4 VARCHAR(30),
    CONSTRAINT uk_frequencia_mensal_musico_periodo UNIQUE (musico_id, ano, mes),
    CONSTRAINT ck_frequencia_mensal_mes CHECK (mes BETWEEN 1 AND 12),
    CONSTRAINT ck_frequencia_mensal_semana1 CHECK (semana1 IS NULL OR semana1 IN ('PRESENTE', 'FALTOU', 'DISPENSADO', 'NAO_MINISTERIO')),
    CONSTRAINT ck_frequencia_mensal_semana2 CHECK (semana2 IS NULL OR semana2 IN ('PRESENTE', 'FALTOU', 'DISPENSADO', 'NAO_MINISTERIO')),
    CONSTRAINT ck_frequencia_mensal_semana3 CHECK (semana3 IS NULL OR semana3 IN ('PRESENTE', 'FALTOU', 'DISPENSADO', 'NAO_MINISTERIO')),
    CONSTRAINT ck_frequencia_mensal_semana4 CHECK (semana4 IS NULL OR semana4 IN ('PRESENTE', 'FALTOU', 'DISPENSADO', 'NAO_MINISTERIO'))
);

CREATE INDEX idx_frequencia_mensal_periodo ON frequencia_mensal (ano, mes);

CREATE TABLE frequencia_mensal_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    musico_id BIGINT,
    ano INTEGER,
    mes INTEGER,
    semana1 VARCHAR(30),
    semana2 VARCHAR(30),
    semana3 VARCHAR(30),
    semana4 VARCHAR(30),
    PRIMARY KEY (id, rev)
);

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — Frequência', 'Menu — Frequência', 'frequencia.menu', 'brmusic', 'frequencia', 'menu', 210, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — Frequência', 'Página — Frequência', 'frequencia.pagina', 'brmusic', 'frequencia', 'pagina', 211, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — Frequência', 'Listar — Frequência', 'frequencia.listar', 'brmusic', 'frequencia', 'listar', 212, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — Frequência', 'Visualizar — Frequência', 'frequencia.visualizar', 'brmusic', 'frequencia', 'visualizar', 213, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Editar — Frequência', 'Editar — Frequência', 'frequencia.editar', 'brmusic', 'frequencia', 'editar', 214, TRUE, TRUE, NOW(), NOW())
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
    'frequencia.menu', 'frequencia.pagina', 'frequencia.listar',
    'frequencia.visualizar', 'frequencia.editar'
  )
ON CONFLICT DO NOTHING;

-- Propaga para grupos que já editam músicos (coordenação).
INSERT INTO grupo_permissao (grupo_id, permissao_id)
SELECT DISTINCT g.id, p.id
FROM grupo g
JOIN grupo_permissao gp ON gp.grupo_id = g.id
JOIN permissao musico_editar ON musico_editar.id = gp.permissao_id AND musico_editar.chave = 'musico.editar'
CROSS JOIN permissao p
WHERE p.chave IN (
    'frequencia.menu', 'frequencia.pagina', 'frequencia.listar',
    'frequencia.visualizar', 'frequencia.editar'
)
  AND NOT EXISTS (
    SELECT 1 FROM grupo_permissao gp2
    WHERE gp2.grupo_id = g.id AND gp2.permissao_id = p.id
  )
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
SELECT setval('frequencia_mensal_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM frequencia_mensal), 1));
