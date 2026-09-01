package br.gov.ce.sps.projetoa.domain.repository;

import br.gov.ce.sps.projetoa.domain.model.WhatsAppEnvio;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppEnvioStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WhatsAppEnvioRepository extends CustomJpaRepository<WhatsAppEnvio, Long> {

    @Query("SELECT e FROM WhatsAppEnvio e LEFT JOIN FETCH e.musico WHERE e.codigo = :codigo")
    Optional<WhatsAppEnvio> findByCodigo(@Param("codigo") UUID codigo);

    Optional<WhatsAppEnvio> findByChaveIdempotencia(String chaveIdempotencia);

    Optional<WhatsAppEnvio> findByProviderMessageId(String providerMessageId);

    List<WhatsAppEnvio> findByStatusAndDataAtualizacaoBefore(WhatsAppEnvioStatus status, OffsetDateTime limite);

    List<WhatsAppEnvio> findByStatusInOrderByDataSolicitacaoAsc(Collection<WhatsAppEnvioStatus> statuses);

    long countByStatus(WhatsAppEnvioStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE WhatsAppEnvio e
            SET e.status = :destino
            WHERE e.id = :id AND e.status = :origem
            """)
    int transicionarStatus(
            @Param("id") Long id,
            @Param("origem") WhatsAppEnvioStatus origem,
            @Param("destino") WhatsAppEnvioStatus destino);
}
