-- Seed a partir de repertorio_cordeiro_de_deus_missa.json (idempotente por titulo).
INSERT INTO musica (
    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,
    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao
)
SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,
       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('CORDEIRO DE DEUS', 'Amanda Pinheiro; Leonardo Biondo', 'Comunidade Católica Shalom', 'G', 'CORDEIRO', NULL, 'https://www.cifraclub.com.br/comunidade-catolica-shalom/cordeiro-de-deus/', NULL, 'Cordeiro de Deus da Missa; preserva as invocações litúrgicas e conclui com Dai-nos a paz.'),
    ('CORDEIRO', 'Emanuel Stenio', 'Ministério Amor e Adoração', 'A', 'CORDEIRO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/cordeiro/', NULL, 'Cordeiro de Deus da Missa; texto estruturado segundo o Agnus Dei.'),
    ('CORDEIRO DE DEUS I', 'Músicas Católicas', 'Músicas Católicas', 'Dm', 'CORDEIRO', NULL, 'https://www.cifraclub.com.br/catolicas/cordeiro-de-deus-i/', NULL, 'Cordeiro de Deus da Missa; fórmula concisa com duas invocações de piedade e uma de paz.'),
    ('CORDEIRO DE DEUS - VERSÃO EM C', 'Marcelo Oliveira; Thiago Garcia; Missal Romano; Músicas Católicas', 'Músicas Católicas', 'C', 'CORDEIRO', NULL, 'https://www.cifraclub.com.br/catolicas/cordeiro-de-deus-7/', NULL, 'Cordeiro de Deus da Missa; versão em C preservando a fórmula litúrgica.'),
    ('CORDEIRO DE DEUS - VERSÃO EM C (THIAGO GARCIA)', 'Thiago Garcia; Missal Romano', 'Músicas Católicas', 'C', 'CORDEIRO', NULL, 'https://www.cifraclub.com.br/catolicas/cordeiro-de-deus-9/', NULL, 'Cordeiro de Deus da Missa; texto do Missal Romano.'),
    ('CORDEIRO DE DEUS - VERSÃO EM G', 'Músicas Católicas', 'Católicas', 'G', 'CORDEIRO', NULL, 'https://www.cifraclub.com.br/catolicas-2/cordeiro-de-deus/', NULL, 'Cordeiro de Deus da Missa; duas invocações de piedade e conclusão com Dai-nos a paz.'),
    ('CORDEIRO DE DEUS CLÁSSICO', 'Músicas Católicas', 'Músicas Católicas', 'G', 'CORDEIRO', NULL, 'https://www.cifraclub.com.br/catolicas/cordeiro-de-deus-classico/', NULL, 'Cordeiro de Deus da Missa; versão tradicional em G.'),
    ('CORDEIRO DE DEUS - REPETE', 'Thiago Garcia', 'Músicas Católicas', 'E', 'CORDEIRO', NULL, 'https://www.cifraclub.com.br/catolicas/cordeiro-de-deus/cordeiro-repete.html', NULL, 'Cordeiro de Deus da Missa; versão responsorial/repetida.')
) AS v(titulo, autor, interprete, tom, categoria, letra, cifra, link, observacao)
WHERE NOT EXISTS (
    SELECT 1 FROM musica m WHERE LOWER(TRIM(m.titulo)) = LOWER(TRIM(v.titulo))
);
