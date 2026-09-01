package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacaoRepertorioItem;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface EscalaPublicacaoRepertorioItemRepository extends CustomJpaRepository<EscalaPublicacaoRepertorioItem, Long> {

    List<EscalaPublicacaoRepertorioItem> findByCelebracaoSnapshot_IdIn(Collection<Long> celebracaoSnapshotIds);
}
