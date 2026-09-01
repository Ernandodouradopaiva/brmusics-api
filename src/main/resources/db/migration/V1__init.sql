-- Schema inicial projetoA
-- Requer banco vazio. Para reset em desenvolvimento: scripts/reset-projetoa-db.sql

CREATE SEQUENCE revinfo_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE estado_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE municipio_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE bairro_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE usuario_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE grupo_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE permissao_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE revinfo (
    id INTEGER NOT NULL PRIMARY KEY DEFAULT nextval('revinfo_seq'),
    timestamp BIGINT,
    login_usuario VARCHAR(255),
    nome_usuario VARCHAR(255),
    data_cadastro TIMESTAMPTZ
);

CREATE TABLE estado (
    id BIGINT PRIMARY KEY DEFAULT nextval('estado_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255) NOT NULL,
    ibge VARCHAR(255) NOT NULL,
    sigla VARCHAR(255) NOT NULL
);

CREATE TABLE municipio (
    id BIGINT PRIMARY KEY DEFAULT nextval('municipio_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(60) NOT NULL,
    codigoibge BIGINT NOT NULL,
    estado_id BIGINT NOT NULL REFERENCES estado(id)
);

CREATE TABLE bairro (
    id BIGINT PRIMARY KEY DEFAULT nextval('bairro_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255) NOT NULL,
    municipio_id BIGINT NOT NULL REFERENCES municipio(id)
);

CREATE TABLE usuario (
    id BIGINT PRIMARY KEY DEFAULT nextval('usuario_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255),
    data_nascimento TIMESTAMPTZ,
    cpf VARCHAR(255),
    bairro_id BIGINT REFERENCES bairro(id),
    logradouro VARCHAR(255),
    logradouro_numero VARCHAR(20),
    complemento VARCHAR(100),
    cep VARCHAR(255),
    telefone VARCHAR(255),
    email VARCHAR(255),
    usuario_sso_id UUID UNIQUE,
    senha VARCHAR(255),
    cargo VARCHAR(255),
    recebe_email BOOLEAN DEFAULT TRUE,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE grupo (
    id BIGINT PRIMARY KEY DEFAULT nextval('grupo_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255),
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE permissao (
    id BIGINT PRIMARY KEY DEFAULT nextval('permissao_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255),
    descricao VARCHAR(255),
    ativo BOOLEAN DEFAULT TRUE,
    chave VARCHAR(120),
    modulo VARCHAR(60),
    recurso VARCHAR(60),
    acao VARCHAR(60),
    ordem INTEGER,
    sistema BOOLEAN DEFAULT FALSE
);

CREATE TABLE usuario_grupo (
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    grupo_id BIGINT NOT NULL REFERENCES grupo(id),
    PRIMARY KEY (usuario_id, grupo_id)
);

CREATE TABLE grupo_permissao (
    grupo_id BIGINT NOT NULL REFERENCES grupo(id),
    permissao_id BIGINT NOT NULL REFERENCES permissao(id),
    PRIMARY KEY (grupo_id, permissao_id)
);

CREATE TABLE usuario_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255),
    data_nascimento TIMESTAMPTZ,
    cpf VARCHAR(255),
    bairro_id BIGINT,
    logradouro VARCHAR(255),
    logradouro_numero VARCHAR(20),
    complemento VARCHAR(100),
    cep VARCHAR(255),
    telefone VARCHAR(255),
    email VARCHAR(255),
    usuario_sso_id UUID,
    senha VARCHAR(255),
    cargo VARCHAR(255),
    recebe_email BOOLEAN,
    ativo BOOLEAN,
    PRIMARY KEY (id, rev)
);

CREATE TABLE grupo_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255),
    ativo BOOLEAN,
    PRIMARY KEY (id, rev)
);

CREATE TABLE permissao_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255),
    descricao VARCHAR(255),
    ativo BOOLEAN,
    chave VARCHAR(120),
    modulo VARCHAR(60),
    recurso VARCHAR(60),
    acao VARCHAR(60),
    ordem INTEGER,
    sistema BOOLEAN,
    PRIMARY KEY (id, rev)
);

CREATE TABLE bairro_aud (
    id BIGINT NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    revtype SMALLINT,
    codigo UUID,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    nome VARCHAR(255),
    municipio_id BIGINT,
    PRIMARY KEY (id, rev)
);

CREATE TABLE usuario_grupo_aud (
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    usuario_id BIGINT NOT NULL,
    grupo_id BIGINT NOT NULL,
    revtype SMALLINT,
    PRIMARY KEY (rev, usuario_id, grupo_id)
);

CREATE TABLE grupo_permissao_aud (
    rev INTEGER NOT NULL REFERENCES revinfo(id),
    grupo_id BIGINT NOT NULL,
    permissao_id BIGINT NOT NULL,
    revtype SMALLINT,
    PRIMARY KEY (rev, grupo_id, permissao_id)
);
