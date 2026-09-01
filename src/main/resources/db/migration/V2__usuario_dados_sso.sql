-- Dados cadastrais e endereço do usuário permanecem no SSO.
-- O Projeto A mantém apenas vínculo local, grupos e status.

ALTER TABLE usuario
    DROP COLUMN IF EXISTS data_nascimento,
    DROP COLUMN IF EXISTS bairro_id,
    DROP COLUMN IF EXISTS logradouro,
    DROP COLUMN IF EXISTS logradouro_numero,
    DROP COLUMN IF EXISTS complemento,
    DROP COLUMN IF EXISTS cep,
    DROP COLUMN IF EXISTS telefone,
    DROP COLUMN IF EXISTS email,
    DROP COLUMN IF EXISTS senha;

ALTER TABLE usuario_aud
    DROP COLUMN IF EXISTS data_nascimento,
    DROP COLUMN IF EXISTS bairro_id,
    DROP COLUMN IF EXISTS logradouro,
    DROP COLUMN IF EXISTS logradouro_numero,
    DROP COLUMN IF EXISTS complemento,
    DROP COLUMN IF EXISTS cep,
    DROP COLUMN IF EXISTS telefone,
    DROP COLUMN IF EXISTS email,
    DROP COLUMN IF EXISTS senha;
