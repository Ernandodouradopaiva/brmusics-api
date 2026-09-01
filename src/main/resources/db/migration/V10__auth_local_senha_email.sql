-- Autenticação própria: restaura senha e e-mail locais; remove vínculo Conecta/SSO.

ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS senha VARCHAR(255),
    ADD COLUMN IF NOT EXISTS email VARCHAR(255);

ALTER TABLE usuario_aud
    ADD COLUMN IF NOT EXISTS email VARCHAR(255);

ALTER TABLE usuario DROP COLUMN IF EXISTS usuario_conecta_id;
ALTER TABLE usuario_aud DROP COLUMN IF EXISTS usuario_conecta_id;
