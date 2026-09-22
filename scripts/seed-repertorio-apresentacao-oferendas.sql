-- Seed a partir de repertorio_apresentacao_oferendas.json (idempotente por titulo).
INSERT INTO musica (
    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,
    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao
)
SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,
       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('NOSSA OFERTA DE AMOR', 'Augusto Cesar; Fernando Martins; Leonardo Biondo; Kito Moura; João Paulo Coelho; Gustavo Osterno; Fábio Lima; Erlison Galvão; Debora Pires; Davidson Silva; Cristiano Pinheiro; Ana Gabriela; Francisco Aristides; Elkenson Silva; Evacy Assunção; Leozany Oli', 'Comunidade Católica Shalom', 'D', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/comunidade-catolica-shalom/nossa-oferta-de-amor/', NULL, 'Apresentação das oferendas / Oferta da vida, pão e vinho'),
    ('PÃO E VINHO', 'Carlos Eduardo Da Silva; Thiago Tome', 'Ministério Amor e Adoração', 'A', 'OFERTORIO', 'https://www.letras.mus.br/ministerio-amor-e-adoracao/1866371/', 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/pao-e-vinho/', NULL, 'Apresentação das oferendas / Pão e vinho'),
    ('OFERTA DE LOUVOR', 'Gil Duarte; Carlos Tocco; Ana Lucia; Karina Tonoli', 'Ministério Amor e Adoração', 'A', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/oferta-de-louvor/', NULL, 'Apresentação das oferendas / Oferta e ação de graças'),
    ('A VÓS, SENHOR, APRESENTAMOS', 'Elvira Dordlom', 'CNBB', 'Ab', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/cnbb/a-vos-senhor-apresentamos/', NULL, 'Apresentação das oferendas / Pão, vinho e Salmo 115'),
    ('QUE PODEREI RETRIBUIR AO SENHOR', 'Pe. José Weber; Arr. Delphim Rezende Porto', 'Pe. José Weber', 'F', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/pe-jose-weber/que-poderei-retribuir-ao-senhor/', NULL, 'Apresentação das oferendas / Salmo 115 / ação de graças'),
    ('MUITOS GRÃOS DE TRIGO', 'José Acácio Santana', 'Músicas Católicas', 'C', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/catolicas/muitos-graos-de-trigo/', 'https://www.youtube.com/watch?v=Ny5rgue22f0', 'Apresentação das oferendas / Pão, vinho e oferta da vida'),
    ('DE MÃOS ESTENDIDAS', 'Irmã Salete', 'Músicas Católicas', 'Ebm', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/catolicas/de-maos-estendida/', 'https://www.youtube.com/watch?v=z8v0Au78g-4', 'Apresentação das oferendas / Criação, pão, vinho e vida'),
    ('A MESA SANTA', 'Almir Dos Reis', 'CNBB', 'B', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/cnbb/a-mesa-santa/', NULL, 'Apresentação das oferendas / Pão, vinho, trabalho e vida'),
    ('SOBE A JERUSALÉM', 'Valdeci Farias; Dom Carlos Alberto Navarro', 'Músicas Católicas', 'D', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/catolicas/sobe-jerusalem/', NULL, 'Apresentação das oferendas / Oferta e oblação; celebrações marianas'),
    ('UM CORAÇÃO PARA AMAR', 'Pe. Zezinho, SCJ', 'Padre Zezinho', 'G', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/padre-zezinho/um-coracao-para-amar/', NULL, 'Apresentação das oferendas / Oferta do coração e da vida'),
    ('MINHA VIDA TEM SENTIDO', 'Padre Zezinho', 'Padre Zezinho', 'D', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/padre-zezinho/minha-vida-tem-sentido/', NULL, 'Apresentação das oferendas / Pão, vinho e oferta do amor'),
    ('BENDITO SEJAS, SENHOR', 'Frei Fabretti', 'Frei Fabretti', 'C', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/frei-fabretti/bendito-sejas-senhor/', NULL, 'Apresentação das oferendas / Trigo, vinho e oferta da vida'),
    ('BENDITO SEJAS', 'CNBB / Música Litúrgica', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Missa do Crisma'),
    ('COMO IREI RETRIBUIR AO MEU SENHOR', 'CNBB / Música Litúrgica', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Missa do Crisma / Salmo 115'),
    ('QUE MARAVILHA, SENHOR, ESTAR AQUI', 'CNBB / Música Litúrgica', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Missa do Crisma / oferta comunitária'),
    ('SUSCITAI, Ó SENHOR DEUS', 'Fr. Joel Postma', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, 'https://www.youtube.com/watch?v=S6MSn1NN0po', 'Apresentação das oferendas / Pentecostes'),
    ('CONFIRMAI, Ó DEUS', 'Frei Wanderson Freitas', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, 'https://www.youtube.com/watch?v=TEeY0Zuytc0', 'Apresentação das oferendas / Pentecostes'),
    ('EIS A PROCISSÃO DO REI, NOSSO DEUS', 'Reginaldo Veloso', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, 'https://www.youtube.com/watch?v=g_ijkjxh0HA', 'Apresentação das oferendas / Pentecostes / Ascensão'),
    ('Ó PAI QUE PELO ESPÍRITO', 'Frei Fabreti, OFM', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, 'https://www.youtube.com/watch?v=gOLwiuzlwoc', 'Apresentação das oferendas / Pentecostes'),
    ('ESPÍRITO CRIADOR', 'André Zamur', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, 'https://www.youtube.com/watch?v=nyLQaQQvtMk', 'Apresentação das oferendas / Pentecostes'),
    ('PÃO E VINHO, PAI POREMOS', 'Ir. Míria T. Kolling', 'Ir. Míria T. Kolling', NULL, 'OFERTORIO', NULL, NULL, 'https://www.youtube.com/watch?v=5-l_LHdj9cY', 'Apresentação das oferendas / Pentecostes'),
    ('FOI ELEVADA MARIA AO CÉU', 'Gradual Romano; música: Gílson Celerino', 'Gílson Celerino', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Assunção de Nossa Senhora'),
    ('BEM AVENTURADA ÉS', 'Gradual Romano; música e adaptação: Gílson Celerino', 'Gílson Celerino', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Assunção / Imaculada Conceição'),
    ('MARIA FOI ASSUNTA AO CÉU', 'Gradual Romano; música: Frei Wanderson Luiz, O.Carm.', 'Frei Wanderson Luiz, O.Carm.', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Assunção de Nossa Senhora'),
    ('É GRANDE O SENHOR', 'José Thomaz Filho; música: Frei Fabreti, OFM', 'Frei Fabreti, OFM', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Assunção de Nossa Senhora'),
    ('A TERRA AMIGA', 'Frei José Moacyr Cadenassi, OFMCap; música: Frei José Luiz Prim, OFM', 'Frei José Luiz Prim, OFM', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Celebrações marianas'),
    ('GLORIFICA O SENHOR, JERUSALÉM', 'Joseph Gelineau, SJ', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Ascensão do Senhor'),
    ('CANTAI AO SENHOR UM NOVO CANTO', 'Geraldo Leite Bastos; música: Reginaldo Veloso', 'Reginaldo Veloso', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Ascensão do Senhor'),
    ('BENDITO SEJA O NOME DO SENHOR', 'Missal Romano; música: Pe. José Weber, SVD', 'Pe. José Weber, SVD', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Ascensão; indicada quando cantada pelo presidente'),
    ('O SANTO NOME DO SENHOR', 'Gradual Triplex; Liturgia das Horas; música: Frei Wanderson Luiz, O.Carm.', 'Frei Wanderson Luiz, O.Carm.', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Exaltação da Santa Cruz'),
    ('NOSSA GLÓRIA É A CRUZ', 'Pe. José Weber, SVD', 'Pe. José Weber, SVD', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Exaltação da Santa Cruz'),
    ('A VIDA DOS JUSTOS', 'Graduale Romanum; Liturgia das Horas; música: Fr. Joel Postma, OFM', 'Fr. Joel Postma, OFM', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Todos os Santos / Fiéis Defuntos'),
    ('A MORTE JÁ NÃO MATA MAIS', 'Waldeci Farias', 'Waldeci Farias', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Todos os Santos'),
    ('OS OLHOS JAMAIS CONTEMPLARAM', 'Ir. Míria T. Kolling', 'Ir. Míria T. Kolling', NULL, 'OFERTORIO', NULL, NULL, 'https://www.youtube.com/watch?v=QlhKjDlxwRw', 'Apresentação das oferendas / Fiéis Defuntos'),
    ('QUEM HABITARÁ NA TUA CASA?', 'Jocy Rodrigues', 'Jocy Rodrigues', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Fiéis Defuntos'),
    ('MISERICÓRDIA, Ó SENHOR', 'Graduale Romanum; Liturgia das Horas; música: Frei Wanderson Luiz Freitas; Pe. J. Gelineau', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Fiéis Defuntos'),
    ('NO SENHOR SE ENCONTRA A MISERICÓRDIA', 'Missal Romano; Liturgia das Horas; música: Pe. Sílvio Milanez', 'Pe. Sílvio Milanez', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Missa pelos Fiéis Defuntos'),
    ('HÓSTIAS E PRECES DE LOUVOR', 'Fr. Joel Postma, OFM', 'Fr. Joel Postma, OFM', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Fiéis Defuntos'),
    ('PERANTE OS VOSSOS ANJOS', 'Graduale Simplex; Liturgia das Horas; música: Angelo La Serra', 'Angelo La Serra', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Fiéis Defuntos'),
    ('NEM A VIDA, NEM A MORTE', 'Ir. Míria T. Kolling', 'Ir. Míria T. Kolling', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Fiéis Defuntos'),
    ('CONFIA, MINH''ALMA, NO SENHOR', 'Pe. Joseph Gelineau', 'Pe. Joseph Gelineau', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Fiéis Defuntos / Salmo 129'),
    ('ESCUTA, SENHOR, A VOZ DO POVO TEU', 'Frei Telles Ramon, O. de M.; música: Wanderson Luiz Freitas, O.Carm.', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Quaresma'),
    ('ACEITA, SENHOR, COM PRAZER', 'Reginaldo Veloso; música: Daniel De Angeles', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Quaresma'),
    ('RECEBE, DEUS AMIGO', 'Juracy B. A. Junior; música: Juliano Lima Lucas', 'CNBB / Música Litúrgica', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Quaresma'),
    ('RETORNA, ISRAEL', 'Frei Telles Ramon, O. de M.', 'Frei Telles Ramon, O. de M.', NULL, 'OFERTORIO', NULL, NULL, NULL, 'Apresentação das oferendas / Quaresma')
) AS v(titulo, autor, interprete, tom, categoria, letra, cifra, link, observacao)
WHERE NOT EXISTS (
    SELECT 1 FROM musica m WHERE LOWER(TRIM(m.titulo)) = LOWER(TRIM(v.titulo))
);
