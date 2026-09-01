package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.Permissao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissaoRepository extends CustomJpaRepository<Permissao, Long> {
    Optional<Permissao> findByCodigo(UUID codigoPermissao);

    Optional<Permissao> findByChave(String chave);

    List<Permissao> findByAtivoTrueOrderByModuloAscOrdemAsc();

    List<Permissao> findByChaveIn(Collection<String> chaves);

    List<Permissao> findByCodigoIn(Collection<UUID> codigos);

    @Query(value = """
            SELECT DISTINCT p.chave
            FROM usuario u
            JOIN usuario_grupo ug ON ug.usuario_id = u.id
            JOIN grupo g ON g.id = ug.grupo_id AND g.ativo = TRUE
            JOIN grupo_permissao gp ON gp.grupo_id = g.id
            JOIN permissao p ON p.id = gp.permissao_id AND p.ativo = TRUE
            WHERE u.id = :usuarioId
              AND p.chave IS NOT NULL
              AND TRIM(p.chave) <> ''
              AND p.chave NOT LIKE 'ROLE\\_%'
            ORDER BY p.chave
            """, nativeQuery = true)
    List<String> findChavesAtivasPorUsuarioId(@Param("usuarioId") Long usuarioId);
}