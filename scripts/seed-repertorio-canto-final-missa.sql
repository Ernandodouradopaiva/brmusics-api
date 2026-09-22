-- Seed a partir de repertorio_canto_final_missa.json (idempotente por titulo).
INSERT INTO musica (
    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,
    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao
)
SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,
       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('A MISSA TERMINOU', 'Pe. José Fernandes', 'Músicas Católicas', 'A', 'FINAL', NULL, 'https://www.cifraclub.com.br/catolicas/a-missa-terminou/', 'https://www.youtube.com/results?search_query=M%C3%BAsicas+Cat%C3%B3licas+A+MISSA+TERMINOU', 'Canto final / envio missionário; a celebração termina e começa a missão'),
    ('CANTO FINAL - MISSA SERTANEJA', 'Maestro Marino Cafundó', 'Grupo Musical Dom Valioso', 'E', 'FINAL', NULL, 'https://www.cifraclub.com.br/grupo-musical-dom-valioso/canto-final-missa-sertaneja/', 'https://www.youtube.com/results?search_query=Grupo+Musical+Dom+Valioso+CANTO+FINAL+-+MISSA+SERTANEJA', 'Canto final / levar ao mundo o amor recebido na celebração'),
    ('NOVA MISSÃO', 'Dirceu Vicente De Paula', 'Músicas Católicas', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=M%C3%BAsicas+Cat%C3%B3licas+NOVA+MISS%C3%83O', 'Canto final / missão, evangelização e testemunho'),
    ('ENVIA, SENHOR, OPERÁRIOS PARA A MESSE', 'Carlos Bueno; Breno Stanley', 'Músicas Católicas', 'G', 'FINAL', NULL, 'https://www.cifraclub.com.br/catolicas/envia-senhor-operarios-para-messe/', 'https://www.youtube.com/results?search_query=M%C3%BAsicas+Cat%C3%B3licas+ENVIA%2C+SENHOR%2C+OPER%C3%81RIOS+PARA+A+MESSE', 'Canto final / envio e missão'),
    ('A BÍBLIA É COMUNICAÇÃO', 'Músicas Católicas', 'Músicas Católicas', 'Cm', 'FINAL', NULL, 'https://www.cifraclub.com.br/catolicas/a-biblia-comunicacao/', 'https://www.youtube.com/results?search_query=M%C3%BAsicas+Cat%C3%B3licas+A+B%C3%8DBLIA+%C3%89+COMUNICA%C3%87%C3%83O', 'Canto final / missão e anúncio da Palavra'),
    ('SEGURA NA MÃO DE DEUS', 'Nelson Monteiro da Mota', 'Padre Marcelo Rossi', 'E', 'FINAL', NULL, 'https://www.cifraclub.com.br/padre-marcelo-rossi/segura-na-mao-de-deus/', 'https://www.youtube.com/results?search_query=Padre+Marcelo+Rossi+SEGURA+NA+M%C3%83O+DE+DEUS', 'Canto final / envio, perseverança e caminhada'),
    ('OBRA NOVA', 'Vida Reluz', 'Vida Reluz', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=Vida+Reluz+OBRA+NOVA', 'Canto final / missão e vida nova; repertório classificado como FINAL'),
    ('NOVA UNÇÃO', 'Adriana Arydes', 'Adriana Arydes', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=Adriana+Arydes+NOVA+UN%C3%87%C3%83O', 'Canto final / envio e ação do Espírito; repertório classificado como FINAL'),
    ('AVE, RAINHA DO CÉU', 'Liturgia das Horas; música: Pe. José Weber', 'Pe. José Weber', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=Pe.+Jos%C3%A9+Weber+AVE%2C+RAINHA+DO+C%C3%89U', 'Canto final / antífona mariana; indicada pela CNBB'),
    ('AVE, RAINHA DO CÉU (VIRGEM MÃE, Ó MARIA)', 'Liturgia das Horas; música: D.R.', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=CNBB+%2F+M%C3%BAsica+Lit%C3%BArgica+AVE%2C+RAINHA+DO+C%C3%89U+%28VIRGEM+M%C3%83E%2C+%C3%93+MARIA%29', 'Canto final / antífona mariana / Tempo Comum'),
    ('IMACULADA, MARIA DO POVO', 'Frei Fabreti, OFM', 'Frei Fabreti, OFM', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=Frei+Fabreti%2C+OFM+IMACULADA%2C+MARIA+DO+POVO', 'Canto final / mariano / Assunção de Nossa Senhora'),
    ('HOMENAGEM A MARIA', 'Pe. José Bortolini; música: Ir. Míria T. Kolling', 'Ir. Míria T. Kolling', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=Ir.+M%C3%ADria+T.+Kolling+HOMENAGEM+A+MARIA', 'Canto final / mariano / Assunção de Nossa Senhora'),
    ('O ESPÍRITO DO SENHOR', 'Frei José Moacyr Cadenassi, OFMCap; música: Frei José Luiz Prim, OFM', 'Frei José Luiz Prim, OFM', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=Frei+Jos%C3%A9+Luiz+Prim%2C+OFM+O+ESP%C3%8DRITO+DO+SENHOR', 'Canto final / missão no Espírito / Assunção de Nossa Senhora'),
    ('ENVIAI, SENHOR!', 'Madre Tarcísia; música: José Alves', 'José Alves', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=Jos%C3%A9+Alves+ENVIAI%2C+SENHOR%21', 'Canto final / Pentecostes / envio'),
    ('O ESPÍRITO DO SENHOR REPOUSA SOBRE MIM', 'Pe. José Weber, SVD', 'Pe. José Weber, SVD', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=Pe.+Jos%C3%A9+Weber%2C+SVD+O+ESP%C3%8DRITO+DO+SENHOR+REPOUSA+SOBRE+MIM', 'Canto final / Pentecostes / missão'),
    ('VEM, ESPÍRITO DE LUZ', 'André Zamur; Pe. Lúcio Floro', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=QKUQ-XR4MsU', 'Canto final / Pentecostes'),
    ('O AMOR DE DEUS COBRIU', 'Ir. Míria T. Kolling; Pe. Lúcio Floro', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=BxvL5wsACYg', 'Canto final / Pentecostes'),
    ('VEM, ESPÍRITO SANTO, VEM!', 'D.R.', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=CNBB+%2F+M%C3%BAsica+Lit%C3%BArgica+VEM%2C+ESP%C3%8DRITO+SANTO%2C+VEM%21', 'Canto final / Pentecostes'),
    ('QUANDO O ESPÍRITO DE DEUS SOPROU', 'Zé Vicente', 'Zé Vicente', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=Iq9v14IztBI', 'Canto final / Pentecostes / envio no Espírito'),
    ('SENHOR, MEU DEUS, QUANDO EU MARAVILHADO', 'Adaptação de How Great Thou Art; melodia tradicional sueca', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/results?search_query=CNBB+%2F+M%C3%BAsica+Lit%C3%BArgica+SENHOR%2C+MEU+DEUS%2C+QUANDO+EU+MARAVILHADO', 'Canto final / Santíssima Trindade / louvor final'),
    ('HINO DE ADORAÇÃO', 'Anônimo séc. XV; adaptação: Pe. Jacques Zwaanenburg', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=Rm-c7ruowIc', 'Louvor final / Corpus Christi / adoração e procissão'),
    ('DEUS DE AMOR', 'Pe. Josmar Braga; música: José Alves', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=cwhkYfcVsW4', 'Louvor final / Corpus Christi / adoração'),
    ('VAMOS TODOS LOUVAR JUNTOS', 'São Tomás de Aquino; Missal Romano; música: Ir. Míria T. Kolling', 'Ir. Míria T. Kolling', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=2wXJeC2O9AQ', 'Louvor final / Corpus Christi / adoração e procissão'),
    ('VAMOS TODOS LOUVAR JUNTOS - FREI TELLES', 'São Tomás de Aquino; Missal Romano; música: Frei Telles Ramon, O. de M.', 'Frei Telles Ramon, O. de M.', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=VdWDDJ2MWzo', 'Louvor final / Corpus Christi / adoração e procissão'),
    ('DEUS INFINITO', 'CNBB / Música Litúrgica', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=z6Ta9Ca3haw', 'Louvor final / Corpus Christi'),
    ('VIVA CRISTO, NA HÓSTIA SAGRADA', 'CNBB / Música Litúrgica', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=CgYUN2Ju-1Q', 'Louvor final / Corpus Christi / adoração'),
    ('GLÓRIA A JESUS NA HÓSTIA SANTA', 'CNBB / Música Litúrgica', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=k9g4-oPL9M0', 'Louvor final / Corpus Christi / adoração'),
    ('PÃO EM TODAS AS MESAS', 'CNBB / Música Litúrgica', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=Fz5WoxIDK54', 'Louvor final / Corpus Christi'),
    ('DEUS DE AMOR NÓS TE ADORAMOS', 'CNBB / Música Litúrgica', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=hWusDIVCD4Y', 'Louvor final / Corpus Christi / adoração'),
    ('EU QUISERA', 'CNBB / Música Litúrgica', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=jpK0u7cCx60', 'Louvor final / Corpus Christi / adoração'),
    ('JESUS CRISTO ESTÁ REALMENTE', 'CNBB / Música Litúrgica', 'CNBB / Música Litúrgica', NULL, 'FINAL', NULL, NULL, 'https://www.youtube.com/watch?v=lHrHznnYcdM', 'Louvor final / Corpus Christi / adoração')
) AS v(titulo, autor, interprete, tom, categoria, letra, cifra, link, observacao)
WHERE NOT EXISTS (
    SELECT 1 FROM musica m WHERE LOWER(TRIM(m.titulo)) = LOWER(TRIM(v.titulo))
);
