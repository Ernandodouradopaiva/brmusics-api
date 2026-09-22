package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepertorioRepository extends CustomJpaRepository<Repertorio, Long> {

    Optional<Repertorio> findByCodigo(UUID codigo);

    Optional<Repertorio> findByCelebracao_Id(Long celebracaoId);

    Optional<Repertorio> findByCelebracao_Codigo(UUID celebracaoCodigo);

    boolean existsByCelebracao_Id(Long celebracaoId);

    @Query("SELECT r FROM Repertorio r JOIN FETCH r.celebracao c LEFT JOIN FETCH c.local WHERE r.codigo = :codigo")
    Optional<Repertorio> findComCelebracaoByCodigo(@Param("codigo") UUID codigo);

    @Query("""
            SELECT r FROM Repertorio r
            JOIN FETCH r.celebracao c
            LEFT JOIN FETCH c.local
            WHERE c.codigo = :celebracaoCodigo
            """)
    Optional<Repertorio> findComCelebracaoByCelebracaoCodigo(@Param("celebracaoCodigo") UUID celebracaoCodigo);

    @Query("""
            SELECT r FROM Repertorio r
            JOIN FETCH r.celebracao c
            LEFT JOIN FETCH c.local
            WHERE c.id IN :ids
            """)
    List<Repertorio> findComCelebracaoByCelebracaoIdIn(@Param("ids") Collection<Long> ids);

    @Query("""
            SELECT r FROM Repertorio r
            JOIN FETCH r.celebracao c
            LEFT JOIN FETCH c.local
            WHERE c.data BETWEEN :inicio AND :fim
            ORDER BY c.data ASC, c.horaInicio ASC
            """)
    List<Repertorio> findComCelebracaoNoPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);
}
