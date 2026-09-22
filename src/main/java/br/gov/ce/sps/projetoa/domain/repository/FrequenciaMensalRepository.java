package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.FrequenciaMensal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FrequenciaMensalRepository extends CustomJpaRepository<FrequenciaMensal, Long> {

    Optional<FrequenciaMensal> findByCodigo(UUID codigo);

    Optional<FrequenciaMensal> findByMusico_IdAndAnoAndMes(Long musicoId, Integer ano, Integer mes);

    @Query("""
            SELECT f FROM FrequenciaMensal f
            JOIN FETCH f.musico m
            WHERE f.ano = :ano AND f.mes = :mes
            ORDER BY m.nome ASC
            """)
    List<FrequenciaMensal> findByAnoAndMesComMusico(@Param("ano") Integer ano, @Param("mes") Integer mes);
}
