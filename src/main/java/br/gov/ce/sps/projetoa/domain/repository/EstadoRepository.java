package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.Estado;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadoRepository extends CustomJpaRepository<Estado, Long> {    
    Optional<Estado> findByUf(String uf);    
    Optional<Estado> findByIbge(String ibge);    
    List<Estado> findAllByOrderByNomeAsc();    
}
