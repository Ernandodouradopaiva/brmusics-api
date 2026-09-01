package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.Escala;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EscalaRepository extends CustomJpaRepository<Escala, Long> {

    Optional<Escala> findByCodigo(UUID codigo);

    Optional<Escala> findByCelebracao_Id(Long celebracaoId);

    Optional<Escala> findByCelebracao_Codigo(UUID celebracaoCodigo);

    boolean existsByCelebracao_Id(Long celebracaoId);

    boolean existsByCelebracao_Codigo(UUID celebracaoCodigo);

    @Query("SELECT e FROM Escala e JOIN FETCH e.celebracao c LEFT JOIN FETCH c.local WHERE e.codigo = :codigo")
    Optional<Escala> findComCelebracaoByCodigo(@Param("codigo") UUID codigo);

    @Query("""
            SELECT e FROM Escala e
            JOIN FETCH e.celebracao c
            LEFT JOIN FETCH c.local
            WHERE c.id IN :ids
            """)
    List<Escala> findComCelebracaoByCelebracaoIdIn(@Param("ids") Collection<Long> ids);

    @Query("""
            SELECT e FROM Escala e
            JOIN e.celebracao c
            WHERE (c.data < :data OR (c.data = :data AND c.horaInicio < :horaInicio))
              AND EXISTS (
                  SELECT 1 FROM EscalaMusico em
                  WHERE em.escala = e AND em.ativo = TRUE
              )
            ORDER BY c.data DESC, c.horaInicio DESC
            """)
    List<Escala> findAnterioresComEquipe(
            @Param("data") LocalDate data,
            @Param("horaInicio") LocalTime horaInicio,
            Pageable pageable);
}
