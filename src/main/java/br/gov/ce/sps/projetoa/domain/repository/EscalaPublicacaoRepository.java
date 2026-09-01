package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EscalaPublicacaoRepository extends CustomJpaRepository<EscalaPublicacao, Long> {

    Optional<EscalaPublicacao> findByCodigo(UUID codigo);

    Optional<EscalaPublicacao> findFirstByAnoAndMesOrderByVersaoDesc(int ano, int mes);

    Optional<EscalaPublicacao> findByAnoAndMesAndVersao(int ano, int mes, int versao);

    List<EscalaPublicacao> findByAnoAndMesOrderByVersaoAsc(int ano, int mes);
}
