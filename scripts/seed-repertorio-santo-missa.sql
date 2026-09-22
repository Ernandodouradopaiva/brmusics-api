-- Seed a partir de repertorio_santo_missa.json (idempotente por titulo).
INSERT INTO musica (
    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,
    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao
)
SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,
       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('SANTO III', 'Adolfo Temme', 'CNBB', 'Bm', 'SANTO', NULL, 'https://www.cifraclub.com.br/cnbb/santo-iii/', NULL, 'Santo da Missa / Ordinário; texto litúrgico do Sanctus'),
    ('SANTO IV', 'CNBB', 'CNBB', 'Eb', 'SANTO', NULL, 'https://www.cifraclub.com.br/cnbb/santo-iv/', NULL, 'Santo da Missa / Ordinário; texto litúrgico do Sanctus'),
    ('SANTO V', 'Pe. Jose Geraldo de Souza', 'CNBB', 'A', 'SANTO', NULL, 'https://www.cifraclub.com.br/cnbb/santo-v/', NULL, 'Santo da Missa / Ordinário; texto litúrgico do Sanctus'),
    ('SANTO É O SENHOR (CD NA DANÇA DA VIDA)', 'Fernando Martins; Nicodemos Costa; Wilde Fábio; Leozany Oliveira; Debora Morais; Cristiano Pinheiro', 'Comunidade Católica Shalom', 'G', 'SANTO', NULL, 'https://www.cifraclub.com.br/comunidade-catolica-shalom/santo-e-o-senhor/', NULL, 'Santo da Missa; contém Santo, proclamação da glória, Hosana e Bendito'),
    ('SANTO É O SENHOR (CD RESSUSCITOU)', 'Fernando Martins; Nicodemos Costa; Wilde Fábio; Leozany Oliveira; Debora Morais; Cristiano Pinheiro', 'Comunidade Católica Shalom', 'G', 'SANTO', NULL, 'https://www.cifraclub.com.br/comunidade-catolica-shalom/santo-e-o-senhor-cd-ressuscitou/', NULL, 'Santo da Missa; versão do álbum Ressuscitou'),
    ('HOSANA NAS ALTURAS', 'Nicodemos Costa; Evacy; Cristiano Pinheiro; Leonardo Biondo; Wilde Fabio; Fabio Lima; Sibelle Veiga; Gustavo Osterno; Susi Castro; Dudu Cardoso; Justine Lafferriere; Laura Salvador; Mylene Otou; Pedro Veiga', 'Comunidade Católica Shalom', 'A', 'SANTO', NULL, 'https://www.cifraclub.com.br/comunidade-catolica-shalom/hosana-nas-alturas/', NULL, 'Santo da Missa; texto do Sanctus com Hosana e Bendito'),
    ('SANTO', 'Carlos Eduardo Da Silva; Ana Lucia; Emanuel Stenio; Luciana Sitta', 'Ministério Amor e Adoração', 'C', 'SANTO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/santo/', NULL, 'Santo da Missa; texto litúrgico do Sanctus'),
    ('SANTO, SANTO, SANTO, SENHOR DEUS DO UNIVERSO', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'A', 'SANTO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/santo-santo-santo-senhor-deus-do-universo/', NULL, 'Santo da Missa; texto litúrgico do Sanctus'),
    ('SANTO II', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'C', 'SANTO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/santo-ii/', NULL, 'Santo da Missa; texto litúrgico do Sanctus'),
    ('SANTO, SANTO, SANTO SENHOR DEUS DO UNIVERSO', 'Músicas Católicas', 'Músicas Católicas', 'A', 'SANTO', NULL, 'https://www.cifraclub.com.br/catolicas/santo-santo-santo-senhor-deus-do-universo/', NULL, 'Santo da Missa; versão em A'),
    ('SANTO, SANTO, SANTO É O SENHOR - SENHOR DEUS DO UNIVERSO', 'Músicas Católicas', 'Músicas Católicas', 'Bm', 'SANTO', NULL, 'https://www.cifraclub.com.br/catolicas/santo-santo-santo-e-o-senhor-senhor-deus-do-universo/', NULL, 'Santo da Missa; versão em Bm'),
    ('SANTO, SENHOR, DEUS DO UNIVERSO', 'Padre Pedro Brito Guimarães', 'Músicas Católicas', 'C', 'SANTO', NULL, 'https://www.cifraclub.com.br/catolicas/santo-senhor-deus-do-universo/', NULL, 'Santo da Missa; texto do Sanctus'),
    ('SANTO (SANTO, SANTO, SANTO É O SENHOR, NOSSO DEUS)', 'Músicas Católicas', 'Músicas Católicas', 'D', 'SANTO', NULL, 'https://www.cifraclub.com.br/catolicas/santo-santo-santo-santo-e-o-senhor-nosso-deus/', NULL, 'Santo da Missa; versão em D')
) AS v(titulo, autor, interprete, tom, categoria, letra, cifra, link, observacao)
WHERE NOT EXISTS (
    SELECT 1 FROM musica m WHERE LOWER(TRIM(m.titulo)) = LOWER(TRIM(v.titulo))
);
