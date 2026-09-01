package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.Grupo;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GrupoRepository extends CustomJpaRepository<Grupo, Long> {
    Optional<Grupo> findByCodigo(UUID codigoGrupo);
}
