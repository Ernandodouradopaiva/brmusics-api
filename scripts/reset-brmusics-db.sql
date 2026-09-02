-- Reset completo do banco brmusics para desenvolvimento/POC.
-- Uso: psql -h 127.0.0.1 -U postgres -d brmusics -f scripts/reset-brmusics-db.sql

DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
