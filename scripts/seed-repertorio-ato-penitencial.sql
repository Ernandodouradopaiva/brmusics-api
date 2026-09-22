-- Seed a partir de repertorio_ato_penitencial.json (idempotente por titulo).
INSERT INTO musica (
    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,
    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao
)
SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,
       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('PERDOA-ME', 'Nicodemos Costa; Gabriela De Sa; Evacy; Cristiano Pinheiro; Leonardo Biondo; Francisco Aristides De Sousa Ramos; Wilde Fabio; Leozany; Rafael Morel; Fabio Lima; Fernando Martins; Davidson Silva; Pedro Veiga; Kito Moura; Debora Pires; Gustavo Osterno; Erlison Galvao; Elkenson Silva; Joao Paulo Do Arocha; Samara Marques; Augusto Cesar', 'Comunidade Católica Shalom', 'E', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/comunidade-catolica-shalom/perdoa-me/', NULL, 'Ato penitencial / misericórdia'),
    ('PERDOA-ME - VERSÃO LITÚRGICA', 'Comunidade Católica Shalom', 'Comunidade Católica Shalom', 'D', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/comunidade-catolica-shalom/perdoa-me-versao-liturgica/', NULL, 'Ato penitencial / fórmula litúrgica'),
    ('KYRIE ELEISON', 'Comunidade Católica Shalom', 'Comunidade Católica Shalom', 'G', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/comunidade-catolica-shalom/kyrie-eleison/', NULL, 'Ato penitencial / Kyrie'),
    ('PIEDADE DE NÓS', 'Comunidade Católica Shalom', 'Comunidade Católica Shalom', NULL, 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Ato penitencial / perdão'),
    ('SENHOR, TENDE PIEDADE DE NÓS', 'Léo Mantovani', 'Léo Mantovani', 'D', 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Ato penitencial / reconciliação'),
    ('NA ÁGUA E NO ESPÍRITO', 'Léo Mantovani', 'Léo Mantovani', 'B', 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Ato penitencial / Batismo'),
    ('KYRIE ELEISON (JMJ 2013 - LITÚRGICO)', 'Samara Marques; Rodrigo Lima de Carvalho', 'JMJ RIO 2013', 'Bm', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/jmj-rio-2013/kyrie-eleison/', NULL, 'Ato penitencial / Kyrie'),
    ('ATO PENITENCIAL | KYRIE ELEISON', 'Canção Nova', 'Canção Nova', 'G', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/cancao-nova/ato-penitencial-kyrie-eleison/', NULL, 'Ato penitencial / fórmula Senhor que viestes salvar'),
    ('SENHOR QUE VIESTES SALVAR', 'Músicas Católicas', 'Músicas Católicas', 'Dm', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/catolicas/ato-penitencial-senhor-que-viestes-salvar/', NULL, 'Ato penitencial / fórmula litúrgica'),
    ('ATO PENITENCIAL - SENHOR BOM PASTOR', 'Músicas Católicas', 'Músicas Católicas', 'Ab', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/catolicas/ato-penitencial-senhor-bom-pastor/', 'https://www.youtube.com/watch?v=x4MKV8j-1DA', 'Ato penitencial / Bom Pastor'),
    ('SENHOR QUE VINDES VISITAR VOSSO POVO NA PAZ', 'Músicas Católicas', 'Músicas Católicas', 'D', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/catolicas/ato-penitencial-senhor-que-vindes-visitar-vosso-povo-na-paz/', NULL, 'Ato penitencial / Advento e reconciliação'),
    ('CONFESSO A DEUS - ATO PENITENCIAL', 'Músicas Católicas', 'Músicas Católicas', 'A', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/catolicas/confesso-a-deus-ato-penitencial/', NULL, 'Ato penitencial / Confiteor'),
    ('EU CONFESSO A DEUS E A VÓS IRMÃOS', 'Thaylla', 'Músicas Católicas', 'Bm', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/catolicas/ato-penitencial-eu-confesso-a-deus-e-vos-irmaos/', NULL, 'Ato penitencial / Confiteor'),
    ('PELOS PECADOS - ATO PENITENCIAL', 'Músicas Católicas', 'Músicas Católicas', 'G', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/catolicas/pelos-pecados-ato-penitencial/', NULL, 'Ato penitencial / perdão'),
    ('ATO PENITENCIAL - SENHOR QUE SOIS O CAMINHO', 'Missa', 'Missa', 'Eb', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/missa/ato-penitencial/', NULL, 'Ato penitencial / Tempo Comum'),
    ('KYRIE - ATO PENITENCIAL TEMPO COMUM', 'Marcelo Oliveira', 'Marcelo Oliveira Católico', 'Em', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/marcelo-oliveira-catolico/kyrie-ato-penitencial-tempo-comum/', NULL, 'Ato penitencial / Tempo Comum'),
    ('SENHOR QUE VIESTES NÃO PARA CONDENAR', 'Andre Zamur', 'David Melo', 'D', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/david-melo/ato-penitencial-senhor-que-viestes-nao-para-condenar/', NULL, 'Ato penitencial / misericórdia'),
    ('KYRIE ELEISON - ATO PENITENCIAL', 'Géo Maia', 'Géo Maia', 'D', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/geo-maia/kyrie-eleison-ato-penitencial/', NULL, 'Ato penitencial / Batismo e Espírito Santo'),
    ('ATO PENITENCIAL DO ADVENTO', 'Cláudio Santos; Jonas Rodrigues', 'Músicas Católicas', 'E', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/catolicas/ato-penitencial-do-advento/sjwhthm.html', NULL, 'Ato penitencial / Advento'),
    ('ATO PENITENCIAL - 3ª FÓRMULA DO ADVENTO', 'Missal Romano', 'Missal Romano', 'Dm', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/missal-romano/ato-penitencial-2-formula-do-advento/versao-2.html', NULL, 'Ato penitencial / Advento / reconciliação'),
    ('ATO PENITENCIAL - TEMPO PASCAL - SENHOR QUE PELO ESPÍRITO SANTO', 'Músicas Católicas', 'Músicas Católicas', 'G', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/catolicas/melodia-ato-penitencial-pascoa-formula-3-senhor-que-pelo-espirito-santo/', NULL, 'Ato penitencial / Tempo Pascal'),
    ('ATO PENITENCIAL - TEMPO PASCAL - SENHOR QUE SUBINDO AO CÉU', 'Marcus Vinícius Lima', 'Marcus Vinícius Lima', 'E', 'ATO_PENITENCIAL', NULL, 'https://www.cifraclub.com.br/marcus-vinicius-lima/ato-penitencial-tempo-pascal-3-opcao-senhor-que-subindo-ao-ceu/', NULL, 'Ato penitencial / Tempo Pascal'),
    ('MISERICÓRDIA SENHOR', 'Padre Marcelo Rossi', 'Padre Marcelo Rossi', 'G', 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Ato penitencial / misericórdia'),
    ('SENHOR TENDE PIEDADE E PERDOAI A NOSSA CULPA', 'Padre Marcelo Rossi', 'Padre Marcelo Rossi', NULL, 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Ato penitencial / reconciliação'),
    ('SENHOR PIEDADE', 'Padre Marcelo Rossi', 'Padre Marcelo Rossi', NULL, 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Ato penitencial / perdão'),
    ('SENHOR TENDE PIEDADE', 'Campanha da Fraternidade 2015', 'Campanha da Fraternidade 2015', 'D', 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Ato penitencial / Quaresma'),
    ('CONFESSO A DEUS', 'Comunidade Recado', 'Comunidade Recado', NULL, 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Ato penitencial / perdão'),
    ('TENDE PIEDADE', 'Padre Zezinho', 'Padre Zezinho', NULL, 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Ato penitencial / reconciliação'),
    ('CORAÇÃO CONTRITO', 'Flavinho', 'Flavinho', NULL, 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Ato penitencial / reconciliação'),
    ('A OVELHA SOU EU', 'Toca de Assis', 'Toca de Assis', NULL, 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Reconciliação / ato penitencial'),
    ('TENDE COMPAIXÃO DE NÓS, SENHOR', 'Missal Romano; Frei José Luiz Prim OFM', 'CNBB - Setor Música Litúrgica', 'C', 'ATO_PENITENCIAL', NULL, NULL, NULL, 'Ato penitencial / 2ª fórmula / Quaresma')
) AS v(titulo, autor, interprete, tom, categoria, letra, cifra, link, observacao)
WHERE NOT EXISTS (
    SELECT 1 FROM musica m WHERE LOWER(TRIM(m.titulo)) = LOWER(TRIM(v.titulo))
);
