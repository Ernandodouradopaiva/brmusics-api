-- Reset completo do banco projetoa para desenvolvimento/POC.
-- Uso: psql -h 127.0.0.1 -U postgres -d projetoa -f scripts/reset-projetoa-db.sql

DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
