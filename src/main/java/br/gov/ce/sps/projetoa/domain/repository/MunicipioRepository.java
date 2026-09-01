package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.Municipio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipioRepository extends CustomJpaRepository<Municipio, Long> {    
    Optional<Municipio> findByCodigoIbge(Long codigoIbge);    
    List<Municipio> findByEstadoIdOrderByNomeAsc(Long estadoId);    
    List<Municipio> findByEstadoUfOrderByNomeAsc(String ufEstado);    
    boolean existsByCodigoIbgeAndEstadoId(Long codigoIbge, Long estadoId);    
}