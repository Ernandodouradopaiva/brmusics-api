-- Renomeia colunas e tabela de handoff SSO → Conecta.

ALTER TABLE usuario RENAME COLUMN usuario_sso_id TO usuario_conecta_id;
ALTER TABLE usuario_aud RENAME COLUMN usuario_sso_id TO usuario_conecta_id;

ALTER TABLE sso_handoff_ticket RENAME TO conecta_handoff_ticket;
ALTER INDEX idx_sso_handoff_ticket_hash RENAME TO idx_conecta_handoff_ticket_hash;
