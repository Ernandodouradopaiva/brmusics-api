package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.Bairro;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BairroRepository extends CustomJpaRepository<Bairro, Long> {
    List<Bairro> findByMunicipioIdOrderByNomeAsc(Long municipioId);    
    boolean existsByNomeAndMunicipioId(String nome, Long municipioId);    
    Optional<Bairro> findByNomeAndMunicipioId(String nome, Long municipioId);
}