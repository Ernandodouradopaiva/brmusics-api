-- Garante escala.publicar em grupos que já montam escala (têm escala.editar).
INSERT INTO grupo_permissao (grupo_id, permissao_id)
SELECT DISTINCT g.id, pub.id
FROM grupo g
JOIN grupo_permissao gp_editar ON gp_editar.grupo_id = g.id
JOIN permissao editar ON editar.id = gp_editar.permissao_id AND editar.chave = 'escala.editar'
JOIN permissao pub ON pub.chave = 'escala.publicar'
WHERE NOT EXISTS (
    SELECT 1
    FROM grupo_permissao gp_pub
    WHERE gp_pub.grupo_id = g.id
      AND gp_pub.permissao_id = pub.id
)
ON CONFLICT DO NOTHING;
