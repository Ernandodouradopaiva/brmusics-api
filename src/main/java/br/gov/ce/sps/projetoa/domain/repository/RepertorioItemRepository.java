package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepertorioItemRepository extends CustomJpaRepository<RepertorioItem, Long> {

    List<RepertorioItem> findByRepertorio_Id(Long repertorioId);

    Optional<RepertorioItem> findByCodigo(UUID codigo);

    boolean existsByMusica_Id(Long musicaId);

    void deleteByMusica_Id(Long musicaId);

    @Query("""
            SELECT DISTINCT ri FROM RepertorioItem ri
            JOIN FETCH ri.repertorio
            JOIN FETCH ri.musica
            WHERE ri.repertorio.id IN :repertorioIds AND ri.ativo = TRUE
            """)
    List<RepertorioItem> findAtivosComMusicaByRepertorioIdIn(@Param("repertorioIds") Collection<Long> repertorioIds);
}
