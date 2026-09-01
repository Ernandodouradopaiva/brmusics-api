-- Publicação mensal das escalas: versão + snapshot para diff/comunicação.
-- Envers permanece na escala operacional; este snapshot é a fonte das regras de negócio.

CREATE SEQUENCE IF NOT EXISTS escala_publicacao_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS escala_publicacao_celebracao_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS escala_publicacao_participacao_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS escala_publicacao_repertorio_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS escala_publicacao_alteracao_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE escala_publicacao (
    id BIGINT PRIMARY KEY DEFAULT nextval('escala_publicacao_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    ano INTEGER NOT NULL,
    mes INTEGER NOT NULL,
    versao INTEGER NOT NULL,
    publicado_em TIMESTAMPTZ NOT NULL,
    publicado_por_id BIGINT NOT NULL REFERENCES usuario(id),
    publicado_por_nome VARCHAR(255) NOT NULL,
    quantidade_celebracoes INTEGER NOT NULL,
    quantidade_musicos INTEGER NOT NULL,
    quantidade_escalas INTEGER NOT NULL,
    UNIQUE (ano, mes, versao)
);

CREATE INDEX idx_escala_publicacao_mes ON escala_publicacao (ano, mes);

CREATE TABLE escala_publicacao_celebracao (
    id BIGINT PRIMARY KEY DEFAULT nextval('escala_publicacao_celebracao_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    publicacao_id BIGINT NOT NULL REFERENCES escala_publicacao(id),
    celebracao_codigo UUID NOT NULL,
    celebracao_titulo VARCHAR(255) NOT NULL,
    data DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fim TIME,
    local_nome VARCHAR(255)
);

CREATE INDEX idx_escala_pub_cel_publicacao ON escala_publicacao_celebracao (publicacao_id);

CREATE TABLE escala_publicacao_participacao (
    id BIGINT PRIMARY KEY DEFAULT nextval('escala_publicacao_participacao_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    publicacao_celebracao_id BIGINT NOT NULL REFERENCES escala_publicacao_celebracao(id),
    musico_codigo UUID NOT NULL,
    musico_nome VARCHAR(255) NOT NULL,
    instrumento_codigo UUID NOT NULL,
    instrumento_nome VARCHAR(255) NOT NULL,
    ordem INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_escala_pub_part_cel ON escala_publicacao_participacao (publicacao_celebracao_id);

CREATE TABLE escala_publicacao_repertorio (
    id BIGINT PRIMARY KEY DEFAULT nextval('escala_publicacao_repertorio_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    publicacao_celebracao_id BIGINT NOT NULL REFERENCES escala_publicacao_celebracao(id),
    musica_codigo UUID NOT NULL,
    musica_titulo VARCHAR(255) NOT NULL,
    momento_liturgico VARCHAR(40) NOT NULL,
    ordem INTEGER NOT NULL DEFAULT 0,
    tom VARCHAR(20)
);

CREATE INDEX idx_escala_pub_rep_cel ON escala_publicacao_repertorio (publicacao_celebracao_id);

CREATE TABLE escala_publicacao_alteracao (
    id BIGINT PRIMARY KEY DEFAULT nextval('escala_publicacao_alteracao_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    publicacao_id BIGINT NOT NULL REFERENCES escala_publicacao(id),
    celebracao_codigo UUID,
    celebracao_titulo VARCHAR(255),
    tipo VARCHAR(40) NOT NULL,
    descricao VARCHAR(2000) NOT NULL
);

CREATE INDEX idx_escala_pub_alt_publicacao ON escala_publicacao_alteracao (publicacao_id);

SELECT setval('escala_publicacao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_publicacao), 1));
SELECT setval('escala_publicacao_celebracao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_publicacao_celebracao), 1));
SELECT setval('escala_publicacao_participacao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_publicacao_participacao), 1));
SELECT setval('escala_publicacao_repertorio_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_publicacao_repertorio), 1));
SELECT setval('escala_publicacao_alteracao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM escala_publicacao_alteracao), 1));
