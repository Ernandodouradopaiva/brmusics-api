-- Libera os relatórios BRMusics para todo grupo que já acessa o módulo Relatórios.
INSERT INTO grupo_permissao (grupo_id, permissao_id)
SELECT g.id, p.id
FROM grupo g
JOIN grupo_permissao gp_base ON gp_base.grupo_id = g.id
JOIN permissao p_base ON p_base.id = gp_base.permissao_id AND p_base.chave = 'relatorio.pagina'
CROSS JOIN permissao p
WHERE p.chave LIKE 'relatorio-%'
  AND NOT EXISTS (
      SELECT 1
      FROM grupo_permissao gp_existente
      WHERE gp_existente.grupo_id = g.id
        AND gp_existente.permissao_id = p.id
  );
