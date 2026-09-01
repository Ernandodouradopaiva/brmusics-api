-- Histórico de envios WhatsApp + permissões do módulo.

CREATE SEQUENCE IF NOT EXISTS whatsapp_envio_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE whatsapp_envio (
    id BIGINT PRIMARY KEY DEFAULT nextval('whatsapp_envio_id_seq'),
    codigo UUID NOT NULL UNIQUE,
    data_cadastro TIMESTAMPTZ,
    data_atualizacao TIMESTAMPTZ,
    musico_id BIGINT REFERENCES musico(id),
    telefone VARCHAR(20) NOT NULL,
    tipo_mensagem VARCHAR(40) NOT NULL,
    mensagem VARCHAR(4000) NOT NULL,
    status VARCHAR(20) NOT NULL,
    provider_message_id VARCHAR(120),
    tentativas INTEGER NOT NULL DEFAULT 0,
    erro VARCHAR(2000),
    data_solicitacao TIMESTAMPTZ NOT NULL,
    data_envio TIMESTAMPTZ,
    chave_idempotencia VARCHAR(160) NOT NULL UNIQUE
);

CREATE INDEX idx_whatsapp_envio_status ON whatsapp_envio (status, data_solicitacao);
CREATE INDEX idx_whatsapp_envio_musico ON whatsapp_envio (musico_id);
CREATE INDEX idx_whatsapp_envio_provider ON whatsapp_envio (provider_message_id);

INSERT INTO permissao (codigo, nome, descricao, chave, modulo, recurso, acao, ordem, sistema, ativo, data_cadastro, data_atualizacao)
VALUES
    (gen_random_uuid(), 'Menu — WhatsApp', 'Menu — WhatsApp', 'whatsapp.menu', 'brmusic', 'whatsapp', 'menu', 170, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Página — WhatsApp', 'Página — WhatsApp', 'whatsapp.pagina', 'brmusic', 'whatsapp', 'pagina', 171, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Listar — WhatsApp', 'Listar — WhatsApp', 'whatsapp.listar', 'brmusic', 'whatsapp', 'listar', 172, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Visualizar — WhatsApp', 'Visualizar — WhatsApp', 'whatsapp.visualizar', 'brmusic', 'whatsapp', 'visualizar', 173, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Enviar — WhatsApp', 'Enviar — WhatsApp', 'whatsapp.enviar', 'brmusic', 'whatsapp', 'enviar', 174, TRUE, TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'Reenviar — WhatsApp', 'Reenviar — WhatsApp', 'whatsapp.reenviar', 'brmusic', 'whatsapp', 'reenviar', 175, TRUE, TRUE, NOW(), NOW())
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
    'whatsapp.menu', 'whatsapp.pagina', 'whatsapp.listar', 'whatsapp.visualizar',
    'whatsapp.enviar', 'whatsapp.reenviar'
  )
ON CONFLICT DO NOTHING;

SELECT setval('permissao_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM permissao), 1));
SELECT setval('whatsapp_envio_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM whatsapp_envio), 1));
