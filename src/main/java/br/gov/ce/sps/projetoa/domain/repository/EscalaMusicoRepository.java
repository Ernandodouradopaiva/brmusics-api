package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface EscalaMusicoRepository extends CustomJpaRepository<EscalaMusico, Long> {

    List<EscalaMusico> findByEscala_Id(Long escalaId);

    @Query("""
            SELECT DISTINCT em FROM EscalaMusico em
            JOIN FETCH em.escala
            JOIN FETCH em.musico
            JOIN FETCH em.instrumento
            WHERE em.escala.id IN :escalaIds AND em.ativo = TRUE
            """)
    List<EscalaMusico> findAtivasComMusicoEInstrumentoByEscalaIdIn(@Param("escalaIds") Collection<Long> escalaIds);

    @Query("""
            SELECT em FROM EscalaMusico em
            JOIN FETCH em.escala e
            JOIN FETCH e.celebracao c
            LEFT JOIN FETCH c.local
            JOIN FETCH em.musico m
            WHERE em.ativo = TRUE
              AND m.id IN :musicoIds
              AND c.data IN :datas
            """)
    List<EscalaMusico> findAtivasPorMusicosEDatas(
            @Param("musicoIds") Collection<Long> musicoIds,
            @Param("datas") Collection<LocalDate> datas);

    boolean existsByMusico_Id(Long musicoId);

    boolean existsByInstrumento_Id(Long instrumentoId);

    @Query("""
            SELECT em FROM EscalaMusico em
            JOIN FETCH em.escala e
            JOIN FETCH e.celebracao c
            LEFT JOIN FETCH c.local
            JOIN FETCH em.instrumento
            JOIN FETCH em.musico
            WHERE em.ativo = TRUE
              AND em.musico.id = :musicoId
              AND e.status = br.gov.ce.sps.projetoa.domain.model.enums.EscalaStatus.PUBLICADA
              AND c.status <> br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus.CANCELADA
            ORDER BY c.data ASC, c.horaInicio ASC
            """)
    List<EscalaMusico> findPublicadasDoMusico(@Param("musicoId") Long musicoId);

    boolean existsByEscala_IdAndMusico_IdAndAtivoTrue(Long escalaId, Long musicoId);
}
