-- Seed a partir de repertorio_gloria_missa.json (idempotente por titulo).
INSERT INTO musica (
    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,
    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao
)
SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,
       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('GLÓRIA - TEXTO OFICIAL DA MISSA', 'Matheus Domingues', 'Músicas Católicas', 'G', 'GLORIA', NULL, 'https://www.cifraclub.com.br/catolicas/gloria-texto-oficial-da-missa/', NULL, 'Hino do Glória da Missa; texto litúrgico do Ordinário.'),
    ('GLÓRIA A DEUS NAS ALTURAS', 'Vanessa L. Piaz', 'Vanessa L. Piaz', 'A', 'GLORIA', NULL, NULL, 'https://www.youtube.com/watch?v=iaF3jVSVGSg', 'Hino do Glória; melodia sobre o texto do Missal Romano.'),
    ('GLÓRIA LITÚRGICO', 'Flavio Fonseca', 'Flavio Fonseca', 'E', 'GLORIA', NULL, 'https://www.cifraclub.com.br/flavio-fonseca/canto-liturgico-de-gloria/', NULL, 'Hino do Glória da Missa; texto litúrgico.'),
    ('GLÓRIA A DEUS NAS ALTURAS (GLÓRIA)', 'Missal Romano; Marcos R. N. Da Matta; Joel Postma', 'CNBB', NULL, 'GLORIA', NULL, 'https://www.cifraclub.com.br/cnbb/gloria-a-deus-nas-alturas-gloria/', NULL, 'Hino do Glória; texto do Missal Romano.'),
    ('GLÓRIA A DEUS (MISSA)', 'Obra de Maria', 'Obra de Maria', 'D', 'GLORIA', NULL, 'https://www.cifraclub.com.br/obra-de-maria/gloria-a-deus-missa/', NULL, 'Hino do Glória destinado à celebração da Missa.'),
    ('GLÓRIA - MISSA PASCAL', 'Pe. Sílvio Milanez', 'Pe. Sílvio Milanez', NULL, 'GLORIA', 'https://musicasparamissa.com.br/musica/gloria-pe-silvio-milanez/', NULL, NULL, 'Hino do Glória; Missa Pascal; texto do Missal Romano.'),
    ('GLÓRIA', 'Frei Wanderson Luiz Freitas, O.Carm.', 'Frei Wanderson Luiz Freitas, O.Carm.', NULL, 'GLORIA', 'https://musicasparamissa.com.br/musica/gloria-oficina-da-musica-liturgica/', NULL, NULL, 'Hino de louvor da Missa; texto do Missal Romano.')
) AS v(titulo, autor, interprete, tom, categoria, letra, cifra, link, observacao)
WHERE NOT EXISTS (
    SELECT 1 FROM musica m WHERE LOWER(TRIM(m.titulo)) = LOWER(TRIM(v.titulo))
);
