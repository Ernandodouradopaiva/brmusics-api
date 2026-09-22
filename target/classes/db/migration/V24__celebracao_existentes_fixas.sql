-- Converte celebrações já cadastradas em FIXA, agrupando em série
-- quando compartilham local, título, horário, dia da semana e ano.

WITH chaves AS (
    SELECT DISTINCT
        local_id,
        upper(trim(titulo)) AS titulo_norm,
        hora_inicio,
        EXTRACT(ISODOW FROM data)::integer AS dia_semana,
        EXTRACT(YEAR FROM data)::integer AS ano
    FROM celebracao
    WHERE serie_codigo IS NULL
),
series AS (
    SELECT
        local_id,
        titulo_norm,
        hora_inicio,
        dia_semana,
        ano,
        gen_random_uuid() AS serie_codigo
    FROM chaves
)
UPDATE celebracao c
SET tipo = 'FIXA',
    serie_codigo = s.serie_codigo,
    data_atualizacao = NOW()
FROM series s
WHERE c.serie_codigo IS NULL
  AND c.local_id = s.local_id
  AND upper(trim(c.titulo)) = s.titulo_norm
  AND c.hora_inicio = s.hora_inicio
  AND EXTRACT(ISODOW FROM c.data)::integer = s.dia_semana
  AND EXTRACT(YEAR FROM c.data)::integer = s.ano;
