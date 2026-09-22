-- Seed a partir de repertorio_catolico_enriquecido.json (idempotente por titulo).
INSERT INTO musica (
    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,
    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao
)
SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,
       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('VAMOS CELEBRAR', 'Carlos Eduardo Da Silva; Maria Eduarda Nascimento Vieira Da Cunha; Pitter Di Laura', 'Ministério Amor e Adoração', 'E', 'ENTRADA', 'https://www.letras.mus.br/ministerio-amor-e-adoracao/1882954/', 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/vamos-celebrar/', 'https://www.youtube.com/watch?v=ikE9eCKZshs', 'Canto de entrada/louvor'),
    ('PÃO E VINHO', 'Carlos Eduardo Da Silva; Thiago Tome', 'Ministério Amor e Adoração', 'A', 'OFERTORIO', 'https://www.letras.mus.br/ministerio-amor-e-adoracao/1866371/', 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/pao-e-vinho/', 'https://www.youtube.com/watch?v=9DajLjlxM1o', 'Apresentação das oferendas'),
    ('GLÓRIA A DEUS NAS ALTURAS', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'E', 'GLORIA', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/gloria-a-deus-nas-alturas/', NULL, 'Hino de louvor - Glória'),
    ('VERBUM PANIS', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'Em', 'COMUNHAO', 'https://www.letras.mus.br/ministerio-amor-e-adoracao/1943039/', 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/verbum-panis/', NULL, 'Comunhão / Eucaristia'),
    ('OFERTA DE LOUVOR', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'A', 'OFERTORIO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/oferta-de-louvor/', NULL, 'Apresentação das oferendas'),
    ('SANTO 2', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'G', 'SANTO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/santo-2/', NULL, 'Aclamação do Santo'),
    ('QUERO CONFESSAR', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'A', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/quero-confessar/', NULL, 'Canto penitencial'),
    ('CORDEIRO DE DEUS', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'G', 'CORDEIRO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/cordeiro-de-deus/', NULL, 'Fração do pão - Cordeiro de Deus'),
    ('ENTRE NÓS', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'F', 'COMUNHAO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/entre-nos/', NULL, 'Comunhão / presença de Cristo'),
    ('LUZ PARA O MEU CAMINHO', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'E', 'POS_COMUNHAO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/luz-para-o-meu-caminho/', NULL, 'Oração / ação de graças'),
    ('FONTE DO VIVER', 'Carlos Eduardo Da Silva; Ana Lucia; Luciana Sitta', 'Ministério Amor e Adoração', 'G', 'COMUNHAO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/fonte-do-viver/', NULL, 'Comunhão / Eucaristia'),
    ('SANTO', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'C', 'SANTO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/santo/', NULL, 'Aclamação do Santo'),
    ('IGREJA REUNIDA', 'Carlos Eduardo Da Silva', 'Ministério Amor e Adoração', 'E', 'ENTRADA', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/igreja-reunida/', NULL, 'Entrada / assembleia reunida'),
    ('Ó SENHOR, TENDE PIEDADE', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'D', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/o-senhor-tende-piedade/', NULL, 'Ato penitencial'),
    ('CORDEIRO', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'A', 'CORDEIRO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/cordeiro/', NULL, 'Fração do pão'),
    ('RECEBE ADORAÇÃO', 'Ministério Amor e Adoração', 'Ministério Amor e Adoração', 'C', 'ADORACAO', NULL, 'https://www.cifraclub.com.br/ministerio-amor-e-adoracao/recebe-adoracao/', NULL, 'Adoração / oração'),
    ('DEIXA DEUS SONHAR EM TI', 'Frei Gilson', 'Frei Gilson', 'Bm', 'POS_COMUNHAO', 'https://www.letras.mus.br/frei-gilson/3702207/', 'https://www.cifraclub.com.br/frei-gilson/deixa-deus-sonhar-em-ti/', NULL, 'Reflexão / pós-comunhão'),
    ('EU SEGUIREI', 'Frei Gilson', 'Frei Gilson', 'E', 'FINAL', 'https://www.letras.mus.br/frei-gilson/2757945/', 'https://www.cifraclub.com.br/frei-gilson/eu-seguirei/', 'https://www.youtube.com/watch?v=o2QQWcLE7ac', 'Envio / compromisso cristão'),
    ('DE TI PRECISO', 'Gilson da Silva Pupo Azevedo', 'Frei Gilson', 'F', 'ADORACAO', 'https://www.letras.mus.br/frei-gilson/3448547/', 'https://www.cifraclub.com.br/frei-gilson/de-ti-preciso/', NULL, 'Adoração / oração'),
    ('TU ÉS O CENTRO', 'Frei Gilson', 'Frei Gilson', 'F', 'ADORACAO', 'https://www.letras.mus.br/frei-gilson/3259766/', 'https://www.cifraclub.com.br/frei-gilson/tu-es-o-centro/', 'https://www.youtube.com/watch?v=FURNjcDNVd4', 'Adoração'),
    ('PESCADOR DE HOMENS', 'Frei Gilson', 'Frei Gilson', 'Am', 'FINAL', 'https://www.letras.mus.br/frei-gilson/2873627/', 'https://www.cifraclub.com.br/frei-gilson/pescador-de-homens/', NULL, 'Vocacional / envio'),
    ('MINHA FAMÍLIA É UMA BENÇÃO', 'Frei Gilson', 'Frei Gilson', 'G', 'OUTROS', 'https://www.letras.mus.br/frei-gilson/3852636/', 'https://www.cifraclub.com.br/frei-gilson/minha-familia-e-uma-bencao/', NULL, 'Família'),
    ('SANTO', 'Frei Gilson', 'Frei Gilson', 'E', 'SANTO', 'https://www.letras.mus.br/frei-gilson/2873630/', 'https://www.cifraclub.com.br/frei-gilson/santo/', NULL, 'Aclamação do Santo'),
    ('COLO DE MÃE', 'Frei Gilson', 'Frei Gilson', 'D', 'MARIANA', 'https://www.letras.mus.br/frei-gilson/3149676/', 'https://www.cifraclub.com.br/frei-gilson/colo-de-mae/', NULL, 'Mariana'),
    ('EU TE LEVANTAREI', 'Frei Gilson', 'Frei Gilson', 'F', 'POS_COMUNHAO', 'https://www.letras.mus.br/frei-gilson/3002060/', 'https://www.cifraclub.com.br/frei-gilson/eu-te-levantarei/', NULL, 'Oração / ação de graças'),
    ('ACALMA MINHA TEMPESTADE', 'Frei Gilson', 'Frei Gilson', 'G', 'POS_COMUNHAO', 'https://www.letras.mus.br/frei-gilson/3107418/', 'https://www.cifraclub.com.br/frei-gilson/acalma-minha-tempestade/', NULL, 'Oração / confiança'),
    ('CORDEIRO', 'Frei Gilson', 'Frei Gilson', 'F', 'CORDEIRO', NULL, 'https://www.cifraclub.com.br/frei-gilson/cordeiro/', NULL, 'Fração do pão'),
    ('VEM QUEIMAR O NOSSO CORAÇÃO', 'Frei Gilson', 'Frei Gilson', 'C', 'ESPIRITO_SANTO', 'https://www.letras.mus.br/frei-gilson/3143219/', 'https://www.cifraclub.com.br/frei-gilson/vem-queimar-o-nosso-coracao/', NULL, 'Invocação ao Espírito Santo'),
    ('LINDO CÉU', 'Adriana Arydes', 'Adriana Arydes', 'C', 'POS_COMUNHAO', NULL, 'https://www.cifraclub.com.br/adriana/lindo-ceu/', NULL, 'Esperança / céu / ação de graças'),
    ('ABRAÇO DE PAI', 'Adriana Arydes', 'Adriana Arydes', 'A', 'POS_COMUNHAO', NULL, 'https://www.cifraclub.com.br/adriana/abraco-de-pai/', NULL, 'Oração / misericórdia / ação de graças'),
    ('NOSSA MISSÃO', 'Adriana Arydes', 'Adriana Arydes', NULL, 'FINAL', NULL, 'https://www.cifraclub.com.br/adriana/nossa-missao/', NULL, 'Missão / envio'),
    ('DIÁRIO DE MARIA', 'Adriana Arydes', 'Adriana Arydes', NULL, 'MARIANA', NULL, 'https://www.cifraclub.com.br/adriana/diario-de-maria/', NULL, 'Mariana'),
    ('HUMANO AMOR DE DEUS', 'Adriana Arydes', 'Adriana Arydes', NULL, 'POS_COMUNHAO', NULL, 'https://www.cifraclub.com.br/adriana/humano-amor-de-deus/', NULL, 'Reflexão / ação de graças'),
    ('COROAÇÃO DE NOSSA SENHORA', 'Adriana Arydes', 'Adriana Arydes', NULL, 'MARIANA', NULL, 'https://www.cifraclub.com.br/adriana/coroacao-de-nossa-senhora/', NULL, 'Coroação / devoção mariana')
) AS v(titulo, autor, interprete, tom, categoria, letra, cifra, link, observacao)
WHERE NOT EXISTS (
    SELECT 1 FROM musica m WHERE LOWER(TRIM(m.titulo)) = LOWER(TRIM(v.titulo))
);
