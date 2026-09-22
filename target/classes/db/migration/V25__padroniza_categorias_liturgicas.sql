-- Padroniza categorias litúrgicas: Comunhão única, Outros e códigos novos.
-- Compatível com dados legados COMUNHAO_01/02 e OUTRO.

UPDATE musica
SET categoria_liturgica = 'COMUNHAO',
    data_atualizacao = NOW()
WHERE categoria_liturgica IN ('COMUNHAO_01', 'COMUNHAO_02');

UPDATE musica
SET categoria_liturgica = 'OUTROS',
    data_atualizacao = NOW()
WHERE categoria_liturgica = 'OUTRO';

UPDATE repertorio_item
SET momento_liturgico = 'COMUNHAO',
    data_atualizacao = NOW()
WHERE momento_liturgico IN ('COMUNHAO_01', 'COMUNHAO_02');

UPDATE repertorio_item
SET momento_liturgico = 'OUTROS',
    data_atualizacao = NOW()
WHERE momento_liturgico = 'OUTRO';

UPDATE escala_publicacao_repertorio
SET momento_liturgico = 'COMUNHAO',
    data_atualizacao = NOW()
WHERE momento_liturgico IN ('COMUNHAO_01', 'COMUNHAO_02');

UPDATE escala_publicacao_repertorio
SET momento_liturgico = 'OUTROS',
    data_atualizacao = NOW()
WHERE momento_liturgico = 'OUTRO';
