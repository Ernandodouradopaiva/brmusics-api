package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstrumentoRepository extends CustomJpaRepository<Instrumento, Long> {

    Optional<Instrumento> findByCodigo(UUID codigo);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    @Query("SELECT COALESCE(MAX(i.ordem), 0) FROM Instrumento i")
    int maxOrdem();
}
