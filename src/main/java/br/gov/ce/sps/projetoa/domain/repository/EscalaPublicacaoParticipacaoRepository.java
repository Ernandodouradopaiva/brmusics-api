package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacaoParticipacao;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface EscalaPublicacaoParticipacaoRepository extends CustomJpaRepository<EscalaPublicacaoParticipacao, Long> {

    List<EscalaPublicacaoParticipacao> findByCelebracaoSnapshot_IdIn(Collection<Long> celebracaoSnapshotIds);
}
