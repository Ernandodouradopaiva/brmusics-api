-- Catálogo litúrgico informado pelo ministério (idempotente por UUID e por título).
-- Premissa: Entrada (Tempo Pascal) → ENTRADA; Ação de Graças → POS_COMUNHAO;
-- Final / Mariano → FINAL. Nuances ficam em observacao.

INSERT INTO musica (
    codigo, titulo, tom_padrao, categoria_liturgica, observacao,
    ativo, data_cadastro, data_atualizacao
)
SELECT v.codigo::uuid, v.titulo, v.tom, v.categoria, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-b000-000000000001', 'FESTA DO CORDEIRO',     'C',   'ENTRADA',         NULL),
    ('00000000-0000-4000-b000-000000000002', 'ÉS A NOSSA VIDA',       NULL,  'ENTRADA',         NULL),
    ('00000000-0000-4000-b000-000000000003', 'RESSUSCITOU',           NULL,  'ENTRADA',         'Tempo Pascal'),
    ('00000000-0000-4000-b000-000000000004', 'RENOVA O MUNDO',        'Eb',  'ATO_PENITENCIAL', NULL),
    ('00000000-0000-4000-b000-000000000005', 'MUITO PERDOAIS',        'D',   'ATO_PENITENCIAL', NULL),
    ('00000000-0000-4000-b000-000000000006', 'PERDOA-ME',             NULL,  'ATO_PENITENCIAL', NULL),
    ('00000000-0000-4000-b000-000000000007', 'REI DOS CÉUS',          'E',   'GLORIA',          NULL),
    ('00000000-0000-4000-b000-000000000008', 'PAZ NA TERRA',          'Em',  'GLORIA',          NULL),
    ('00000000-0000-4000-b000-000000000009', 'ETERNA VIDA',           'E',   'ACLAMACAO',       NULL),
    ('00000000-0000-4000-b000-00000000000a', 'AO OUVIR TUA VOZ',      'G',   'ACLAMACAO',       NULL),
    ('00000000-0000-4000-b000-00000000000b', 'RESPLANDECEU',          NULL,  'ACLAMACAO',       NULL),
    ('00000000-0000-4000-b000-00000000000c', 'TUA PALAVRA',           NULL,  'ACLAMACAO',       NULL),
    ('00000000-0000-4000-b000-00000000000d', 'CORAÇÕES ABRASADOS',    'F',   'OFERTORIO',       NULL),
    ('00000000-0000-4000-b000-00000000000e', 'NOSSA OFERTA DE AMOR',  'D',   'OFERTORIO',       NULL),
    ('00000000-0000-4000-b000-00000000000f', 'HOSANA NAS ALTURAS',    'A',   'SANTO',           NULL),
    ('00000000-0000-4000-b000-000000000010', 'SANTO É O SENHOR',      NULL,  'SANTO',           NULL),
    ('00000000-0000-4000-b000-000000000011', 'DAI-NOS A PAZ',         'F#m', 'CORDEIRO',        NULL),
    ('00000000-0000-4000-b000-000000000012', 'BANQUETE ETERNO',       'A',   'COMUNHAO',        NULL),
    ('00000000-0000-4000-b000-000000000013', 'ALMA DE CRISTO',        'C',   'COMUNHAO',        NULL),
    ('00000000-0000-4000-b000-000000000014', 'TU NOS ATRAÍSTE',       NULL,  'COMUNHAO',        NULL),
    ('00000000-0000-4000-b000-000000000015', 'SOPRA EM MEU JARDIM',   'C',   'POS_COMUNHAO',    'Ação de Graças'),
    ('00000000-0000-4000-b000-000000000016', 'VIDA NOVA',             NULL,  'POS_COMUNHAO',    NULL),
    ('00000000-0000-4000-b000-000000000017', 'RAINHA DOS CÉUS',       'D',   'FINAL',           'Mariano')
) AS v(codigo, titulo, tom, categoria, observacao)
WHERE NOT EXISTS (SELECT 1 FROM musica m WHERE m.codigo = v.codigo::uuid)
  AND NOT EXISTS (SELECT 1 FROM musica m WHERE LOWER(m.titulo) = LOWER(v.titulo));

-- Complemento: títulos da lista ampliada que ainda não existiam.
INSERT INTO musica (
    codigo, titulo, tom_padrao, categoria_liturgica, observacao,
    ativo, data_cadastro, data_atualizacao
)
SELECT v.codigo::uuid, v.titulo, v.tom, v.categoria, v.observacao, TRUE, NOW(), NOW()
FROM (VALUES
    ('00000000-0000-4000-b000-000000000018', 'À CASA DO PAI',                                 NULL,  'ENTRADA',         NULL),
    ('00000000-0000-4000-b000-000000000019', 'UM SÓ CORPO',                                   NULL,  'ENTRADA',         NULL),
    ('00000000-0000-4000-b000-00000000001a', 'ETERNA UNIÃO',                                  NULL,  'ENTRADA',         NULL),
    ('00000000-0000-4000-b000-00000000001b', 'ALELUIA! DEUS É FIEL',                          NULL,  'ENTRADA',         NULL),
    ('00000000-0000-4000-b000-00000000001c', 'ESPOSO RESSUSCITADO',                           NULL,  'ENTRADA',         NULL),
    ('00000000-0000-4000-b000-00000000001d', 'CORDEIRO VENCEDOR',                             'G',   'ENTRADA',         NULL),
    ('00000000-0000-4000-b000-00000000001e', 'PERDOAI-ME',                                    NULL,  'ATO_PENITENCIAL', NULL),
    ('00000000-0000-4000-b000-00000000001f', 'KYRIE ELEISON',                                 NULL,  'ATO_PENITENCIAL', NULL),
    ('00000000-0000-4000-b000-000000000020', 'KYRIE ELEISON (MISSAL ROMANO)',                 NULL,  'ATO_PENITENCIAL', NULL),
    ('00000000-0000-4000-b000-000000000021', 'SENHOR QUE VIESTE SALVAR',                      NULL,  'ATO_PENITENCIAL', NULL),
    ('00000000-0000-4000-b000-000000000022', 'GLÓRIA A DEUS NAS ALTURAS – RESSUSCITOU',       'F',   'GLORIA',          NULL),
    ('00000000-0000-4000-b000-000000000023', 'AMADOS POR DEUS / GLÓRIA SHALOM',               'A',   'GLORIA',          NULL),
    ('00000000-0000-4000-b000-000000000024', 'GLÓRIA A DEUS NAS ALTURAS – NA DANÇA DA VIDA',  NULL,  'GLORIA',          NULL),
    ('00000000-0000-4000-b000-000000000025', 'ALELUIA ANTÍFONA',                              NULL,  'ACLAMACAO',       NULL),
    ('00000000-0000-4000-b000-000000000026', 'NOSSA OFERTA',                                  NULL,  'OFERTORIO',       NULL),
    ('00000000-0000-4000-b000-000000000027', 'OFERTA',                                        NULL,  'OFERTORIO',       NULL),
    ('00000000-0000-4000-b000-000000000028', 'SACRIFÍCIO DE AMOR',                            NULL,  'OFERTORIO',       NULL),
    ('00000000-0000-4000-b000-000000000029', 'HOLOCAUSTO DE AMOR',                            NULL,  'OFERTORIO',       NULL),
    ('00000000-0000-4000-b000-00000000002a', 'ESTAR EM TUAS MÃOS',                            NULL,  'OFERTORIO',       NULL),
    ('00000000-0000-4000-b000-00000000002b', 'TUA GLÓRIA CANTAR',                             'D',   'OFERTORIO',       NULL),
    ('00000000-0000-4000-b000-00000000002c', 'SANTO É O SENHOR – RESSUSCITOU',                NULL,  'SANTO',           NULL),
    ('00000000-0000-4000-b000-00000000002d', 'SANTO É O SENHOR – NA DANÇA DA VIDA',           NULL,  'SANTO',           NULL),
    ('00000000-0000-4000-b000-00000000002e', 'SANTO – 10 ANOS',                               NULL,  'SANTO',           NULL),
    ('00000000-0000-4000-b000-00000000002f', 'SANCTUS',                                       NULL,  'SANTO',           NULL),
    ('00000000-0000-4000-b000-000000000030', 'DAI-NOS A PAZ – 40 ANOS',                       NULL,  'CORDEIRO',        NULL),
    ('00000000-0000-4000-b000-000000000031', 'AGNUS DEI',                                     NULL,  'CORDEIRO',        NULL),
    ('00000000-0000-4000-b000-000000000032', 'EM TEU ABRAÇO',                                 NULL,  'COMUNHAO',        NULL),
    ('00000000-0000-4000-b000-000000000033', 'MESMO SENDO DEUS',                              NULL,  'COMUNHAO',        NULL),
    ('00000000-0000-4000-b000-000000000034', 'FONTE INESGOTÁVEL',                             NULL,  'COMUNHAO',        NULL),
    ('00000000-0000-4000-b000-000000000035', 'DIVINO CORAÇÃO',                                NULL,  'COMUNHAO',        NULL),
    ('00000000-0000-4000-b000-000000000036', 'A TUA TERNURA',                                 NULL,  'COMUNHAO',        NULL),
    ('00000000-0000-4000-b000-000000000037', 'O MEU CORAÇÃO É TEU',                           NULL,  'COMUNHAO',        NULL),
    ('00000000-0000-4000-b000-000000000038', 'PARAÍSO ABERTO',                                NULL,  'COMUNHAO',        NULL),
    ('00000000-0000-4000-b000-000000000039', 'SACIA O MEU CORAÇÃO',                           NULL,  'COMUNHAO',        NULL),
    ('00000000-0000-4000-b000-00000000003a', 'BELÍSSIMO ESPOSO',                              NULL,  'POS_COMUNHAO',    'Ação de Graças'),
    ('00000000-0000-4000-b000-00000000003b', 'SÓ ADORAR',                                     NULL,  'POS_COMUNHAO',    'Ação de Graças'),
    ('00000000-0000-4000-b000-00000000003c', 'TODA ADORAÇÃO',                                 NULL,  'POS_COMUNHAO',    'Ação de Graças'),
    ('00000000-0000-4000-b000-00000000003d', 'FACE ADORÁVEL',                                 NULL,  'POS_COMUNHAO',    'Ação de Graças'),
    ('00000000-0000-4000-b000-00000000003e', 'MEU TUDO',                                      NULL,  'POS_COMUNHAO',    'Ação de Graças'),
    ('00000000-0000-4000-b000-00000000003f', 'TENDO A DEUS TENHO TUDO',                       NULL,  'POS_COMUNHAO',    'Ação de Graças'),
    ('00000000-0000-4000-b000-000000000040', 'QUE CRESÇA A TUA GLÓRIA',                       'D',   'POS_COMUNHAO',    'Ação de Graças'),
    ('00000000-0000-4000-b000-000000000041', 'À VOSSA PROTEÇÃO',                              NULL,  'FINAL',           'Mariano'),
    ('00000000-0000-4000-b000-000000000042', 'EU SOU TODO TEU',                               NULL,  'FINAL',           'Mariano'),
    ('00000000-0000-4000-b000-000000000043', 'MÃE DE MISERICÓRDIA',                           NULL,  'FINAL',           'Mariano'),
    ('00000000-0000-4000-b000-000000000044', 'ATÉ OS CONFINS DA TERRA',                       NULL,  'FINAL',           'Missão'),
    ('00000000-0000-4000-b000-000000000045', 'GLORIOSO REI',                                  NULL,  'FINAL',           NULL)
) AS v(codigo, titulo, tom, categoria, observacao)
WHERE NOT EXISTS (SELECT 1 FROM musica m WHERE m.codigo = v.codigo::uuid)
  AND NOT EXISTS (SELECT 1 FROM musica m WHERE LOWER(m.titulo) = LOWER(v.titulo));
