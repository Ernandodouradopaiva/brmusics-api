package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacaoCelebracao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscalaPublicacaoCelebracaoRepository extends CustomJpaRepository<EscalaPublicacaoCelebracao, Long> {

    @Query("""
            SELECT c FROM EscalaPublicacaoCelebracao c
            WHERE c.publicacao.id = :publicacaoId
            ORDER BY c.data ASC, c.horaInicio ASC, c.id ASC
            """)
    List<EscalaPublicacaoCelebracao> findByPublicacaoId(@Param("publicacaoId") Long publicacaoId);
}
