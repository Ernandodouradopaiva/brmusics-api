package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.Musica;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MusicaRepository extends CustomJpaRepository<Musica, Long> {

    Optional<Musica> findByCodigo(UUID codigo);
}
