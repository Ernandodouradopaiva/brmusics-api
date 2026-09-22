-- Seed a partir de repertorio_musicas_entrada_missa.json (idempotente por titulo).
INSERT INTO musica (
    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,
    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao
)
SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,
       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('ALEGRES VAMOS À CASA DO PAI', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / reunião da assembleia'),
    ('BENDITO SEJA DEUS PAI', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / louvor trinitário'),
    ('ATÉ OS CONFINS DA TERRA', 'Marcelo Oliveira', 'Marcelo Oliveira', NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / missão'),
    ('BEM AVENTURADOS', 'Padre Zezinho', 'Padre Zezinho', NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / bem-aventuranças'),
    ('CELEBRAR', 'Salette Ferreira', 'Salette Ferreira', NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / celebração'),
    ('COM ALEGRIA CANTAR', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / alegria da assembleia'),
    ('COM ALEGRIA E GRATIDÃO', 'Ricardo Sabino', 'Ricardo Sabino', NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / ação de graças'),
    ('COM GRANDE ALEGRIA', 'Ir. Míria T. Kolling', 'Ir. Míria T. Kolling', NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / alegria'),
    ('COM PEDRO E COM PAULO', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / São Pedro e São Paulo'),
    ('DE TODOS CANTOS VIEMOS', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / assembleia'),
    ('REUNIDOS AQUI', 'Padre Marcelo Rossi', 'Padre Marcelo Rossi', NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / assembleia reunida'),
    ('FICO FELIZ EM VIR EM TUA CASA', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / casa do Senhor'),
    ('HOJE É TEMPO DE LOUVAR A DEUS', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / louvor'),
    ('PELAS ESTRADAS DA VIDA', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / caminhada'),
    ('COMO SÃO BELOS', 'Padre Zezinho', 'Padre Zezinho', NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / missão e anúncio'),
    ('NOVO AMANHECER', 'Comunidade Católica Shalom', 'Comunidade Católica Shalom', NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / louvor'),
    ('DEUS HABITA', 'Canção Nova', 'Canção Nova', NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / assembleia'),
    ('A PALAVRA DE DEUS NOS CONVIDA', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Palavra de Deus'),
    ('A BÍBLIA É A PALAVRA DE DEUS', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Palavra de Deus'),
    ('QUERO LEVAR ESTA BÍBLIA', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Palavra e missão'),
    ('TUA PALAVRA', 'Comunidade Católica Shalom', 'Comunidade Católica Shalom', 'D', 'ENTRADA', NULL, 'https://www.cifraclub.com.br/comunidade-catolica-shalom/tua-palavra/', 'https://www.cifraclub.com.br/comunidade-catolica-shalom/tua-palavra/', 'Entrada / Palavra de Deus'),
    ('É COMO A CHUVA QUE LAVA', 'Padre Zezinho', 'Padre Zezinho', NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Palavra de Deus'),
    ('DE PAZ SÃO MEUS PENSAMENTOS', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / antífona'),
    ('DO SEU POVO ELE É A FORÇA', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / confiança no Senhor'),
    ('CONTEMPLAREI JUSTIFICADO', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / antífona'),
    ('RECEBEMOS, Ó DEUS', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / misericórdia'),
    ('EIS OS PENSAMENTOS DO SEU CORAÇÃO', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Sagrado Coração'),
    ('QUANDO A PLENITUDE DOS TEMPOS CHEGOU', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Natal'),
    ('EU VIM AO MUNDO DAR TESTEMUNHO', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Cristo Rei'),
    ('NOITE EXCELSA', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Natal - Missa da Noite'),
    ('UM MENINO NASCEU PARA NÓS', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Natal - Missa do Dia'),
    ('SANTO MODELO DE LAR', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Sagrada Família'),
    ('SANTA MÃE DE DEUS', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Santa Maria, Mãe de Deus'),
    ('SANTA MANIFESTAÇÃO', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Epifania'),
    ('OS CÉUS SE ABRIRAM', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Batismo do Senhor'),
    ('VÓS SOIS DIGNO, SENHOR NOSSO DEUS', NULL, NULL, NULL, 'ENTRADA', NULL, NULL, 'https://www.youtube.com/watch?v=CUkN5uEQVo0', 'Entrada / Corpus Christi'),
    ('CRISTO, PÃO DOS POBRES', 'Pe. José Freitas Campos', 'Pe. José Freitas Campos', NULL, 'ENTRADA', NULL, NULL, 'https://www.youtube.com/watch?v=xfCTs4rXrFU', 'Entrada / Corpus Christi'),
    ('VENHAM TODOS PARA A CEIA DO SENHOR', 'Dom Carlos A.', 'Dom Carlos A.', NULL, 'ENTRADA', NULL, NULL, NULL, 'Entrada / Corpus Christi'),
    ('BENDITO SEJAS TU', 'José Alves', 'José Alves', NULL, 'ENTRADA', NULL, NULL, 'https://www.youtube.com/watch?v=abwQk_33zJA', 'Entrada / Santíssima Trindade'),
    ('BENDIZEI NOSSO DEUS', 'Reginaldo Veloso', 'Reginaldo Veloso', NULL, 'ENTRADA', NULL, NULL, 'https://www.youtube.com/watch?v=BzRnFAp599Y', 'Entrada / Santíssima Trindade'),
    ('GLÓRIA A DEUS PAI', 'Reginaldo Veloso', 'Reginaldo Veloso', NULL, 'ENTRADA', NULL, NULL, 'https://www.youtube.com/watch?v=-8tZqNI_8Q8', 'Entrada / Santíssima Trindade'),
    ('GLÓRIA AO PAI, DOS HOMENS, DOS ANJOS', 'José Alves', 'José Alves', NULL, 'ENTRADA', NULL, NULL, 'https://www.youtube.com/watch?v=rFSQJjk4ilw', 'Entrada / Santíssima Trindade'),
    ('Ó DEUS VIVO', 'Pe. José Weber', 'Pe. José Weber', NULL, 'ENTRADA', NULL, NULL, 'https://www.youtube.com/watch?v=fHWfyVu-pdo', 'Entrada / Santíssima Trindade'),
    ('GLÓRIA, AO PAI CRIADOR', 'Gustavo Balbinot', 'Gustavo Balbinot', NULL, 'ENTRADA', NULL, NULL, 'https://www.youtube.com/watch?v=uzaLHnvcaH4', 'Entrada / Santíssima Trindade'),
    ('ENTREMOS NA CASA DO SENHOR', 'José Acácio', 'José Acácio', NULL, 'ENTRADA', NULL, NULL, 'https://www.youtube.com/watch?v=mLw6i8Bn4uk', 'Entrada / Santíssima Trindade'),
    ('VEM SANTÍSSIMA TRINDADE', 'José Acácio', 'José Acácio', NULL, 'ENTRADA', NULL, NULL, 'https://www.youtube.com/watch?v=B9JYvqMVnC8', 'Entrada / Santíssima Trindade'),
    ('BENDITO SEJA DEUS', 'D.R.', 'D.R.', NULL, 'ENTRADA', NULL, NULL, 'https://www.youtube.com/watch?v=KhD5qAJlX54', 'Entrada / Santíssima Trindade')
) AS v(titulo, autor, interprete, tom, categoria, letra, cifra, link, observacao)
WHERE NOT EXISTS (
    SELECT 1 FROM musica m WHERE LOWER(TRIM(m.titulo)) = LOWER(TRIM(v.titulo))
);
