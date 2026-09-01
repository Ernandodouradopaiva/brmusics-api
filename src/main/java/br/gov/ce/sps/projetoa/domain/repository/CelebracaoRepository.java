package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CelebracaoRepository extends CustomJpaRepository<Celebracao, Long> {

    @Query("SELECT c FROM Celebracao c LEFT JOIN FETCH c.local WHERE c.codigo = :codigo")
    Optional<Celebracao> findByCodigo(@Param("codigo") UUID codigo);

    boolean existsByLocal_Id(Long localId);

    List<Celebracao> findByCodigoIn(Collection<UUID> codigos);

    @Query("""
            SELECT c FROM Celebracao c
            LEFT JOIN FETCH c.local
            WHERE c.data BETWEEN :inicio AND :fim
            ORDER BY c.data ASC, c.horaInicio ASC
            """)
    List<Celebracao> findByDataBetweenOrderByDataAscHoraInicioAsc(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);
}
