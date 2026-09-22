-- Seed a partir de repertorio_aclamacao_evangelho.json (idempotente por titulo).
INSERT INTO musica (
    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,
    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao
)
SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,
       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('ALELUIA - ACLAMAÇÃO AO EVANGELHO', 'Músicas Católicas', 'Músicas Católicas', 'C', 'ACLAMACAO', NULL, 'https://www.cifraclub.com.br/catolicas/aleluia-aclamacao-evangelho/', NULL, 'Aclamação ao Evangelho / uso geral fora da Quaresma'),
    ('ALELUIA, ALELUIA! VAMOS ACLAMAR O EVANGELHO', 'Músicas Católicas', 'Músicas Católicas', 'A', 'ACLAMACAO', NULL, 'https://www.cifraclub.com.br/catolicas/aleluia-aleluia-vamos-aclamar-o-evangelho-a-aleluia/', NULL, 'Aclamação ao Evangelho / uso geral fora da Quaresma'),
    ('ALELUIA, ACLAMEMOS A PALAVRA DO SENHOR', 'Músicas Católicas', 'Músicas Católicas', 'Cm', 'ACLAMACAO', NULL, 'https://www.cifraclub.com.br/catolicas/aleluia-aclamemos-a-palavra-do-senhor/', NULL, 'Aclamação ao Evangelho / uso geral fora da Quaresma'),
    ('ALGUÉM DO POVO EXCLAMA', 'Músicas Católicas', 'Músicas Católicas', 'G', 'ACLAMACAO', NULL, 'https://www.cifraclub.com.br/catolicas/alguem-do-povo-exclama-aclamacao-do-evangelho/', NULL, 'Aclamação ao Evangelho / uso geral; conferir versículo com a celebração'),
    ('ACLAMAÇÃO AO EVANGELHO - ADVENTO', 'Católica', 'Músicas Católicas', 'Gm', 'ACLAMACAO', NULL, 'https://www.cifraclub.com.br/catolicas/aclamacao-ao-evangelho-advento/', NULL, 'Aclamação ao Evangelho própria para o Advento'),
    ('ALELUIA, GLÓRIA AO PAI', 'Ir. Míria T. Kolling', 'Ir. Míria T. Kolling', NULL, 'ACLAMACAO', NULL, NULL, NULL, 'Aclamação ao Evangelho / Santíssima Trindade'),
    ('ALELUIA, MARIA É ELEVADA', 'Valdeci Farias; José Acácio Santana', 'CNBB / Música Litúrgica', NULL, 'ACLAMACAO', NULL, NULL, NULL, 'Aclamação ao Evangelho / Assunção de Nossa Senhora'),
    ('ALELUIA, NÓS VOS ADORAMOS', 'Frei Fabreti, OFM', 'Frei Fabreti, OFM', NULL, 'ACLAMACAO', NULL, NULL, NULL, 'Aclamação ao Evangelho / Exaltação da Santa Cruz'),
    ('ALELUIA, VINDE ESPÍRITO', 'Fr. Joel Postma', 'Fr. Joel Postma', NULL, 'ACLAMACAO', NULL, NULL, 'https://www.youtube.com/watch?v=7HdHVsBdE0w', 'Aclamação ao Evangelho / Pentecostes'),
    ('ALELUIA, VINDE, ESPÍRITO SANTO', 'Frei Fabreti, OFM', 'Frei Fabreti, OFM', NULL, 'ACLAMACAO', NULL, NULL, 'https://www.youtube.com/watch?v=SlJDn4Tz-3c', 'Aclamação ao Evangelho / Pentecostes'),
    ('ALELUIA, EU SOU O PÃO VIVO', 'Ir. Janete Stürmer', 'Ir. Janete Stürmer', NULL, 'ACLAMACAO', NULL, NULL, 'https://www.youtube.com/watch?v=WWsl5DciSLs', 'Aclamação ao Evangelho / Corpus Christi'),
    ('ALELUIA, EU SOU O PÃO DA VIDA', 'Pe. José Weber, SVD', 'Pe. José Weber, SVD', NULL, 'ACLAMACAO', NULL, NULL, NULL, 'Aclamação ao Evangelho / Corpus Christi; referência no Hinário Litúrgico da CNBB'),
    ('ALELUIA, VINDE A MIM', 'Reginaldo Veloso', 'Reginaldo Veloso', NULL, 'ACLAMACAO', NULL, NULL, NULL, 'Aclamação ao Evangelho / Todos os Santos'),
    ('ALELUIA, É ESTA A VONTADE DE QUEM ME ENVIOU', 'D.R.', 'D.R.', NULL, 'ACLAMACAO', NULL, NULL, NULL, 'Aclamação ao Evangelho / Comemoração dos Fiéis Defuntos'),
    ('ALELUIA, EU TE LOUVO, Ó PAI SANTO', 'Reginaldo Veloso', 'Reginaldo Veloso', NULL, 'ACLAMACAO', NULL, NULL, NULL, 'Aclamação ao Evangelho / Comemoração dos Fiéis Defuntos')
) AS v(titulo, autor, interprete, tom, categoria, letra, cifra, link, observacao)
WHERE NOT EXISTS (
    SELECT 1 FROM musica m WHERE LOWER(TRIM(m.titulo)) = LOWER(TRIM(v.titulo))
);
