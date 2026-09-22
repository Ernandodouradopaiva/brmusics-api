-- Celebração fixa (série semanal no ano) vs extraordinária (ocorrência única).

ALTER TABLE celebracao
    ADD COLUMN IF NOT EXISTS tipo VARCHAR(20) NOT NULL DEFAULT 'EXTRAORDINARIA',
    ADD COLUMN IF NOT EXISTS serie_codigo UUID;

ALTER TABLE celebracao_aud
    ADD COLUMN IF NOT EXISTS tipo VARCHAR(20),
    ADD COLUMN IF NOT EXISTS serie_codigo UUID;

CREATE INDEX IF NOT EXISTS idx_celebracao_serie ON celebracao (serie_codigo);
CREATE INDEX IF NOT EXISTS idx_celebracao_tipo ON celebracao (tipo);

COMMENT ON COLUMN celebracao.tipo IS 'FIXA = ocorrência de série semanal; EXTRAORDINARIA = data única';
COMMENT ON COLUMN celebracao.serie_codigo IS 'Agrupa ocorrências geradas pela mesma celebração fixa';
