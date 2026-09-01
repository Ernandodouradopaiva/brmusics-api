package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppEnvioStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;
import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "whatsapp_envio")
@Getter
@Setter
@SequenceGenerator(name = "seq", sequenceName = "whatsapp_envio_id_seq", allocationSize = 1)
public class WhatsAppEnvio extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "musico_id")
    private Musico musico;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mensagem", nullable = false, length = 40)
    private WhatsAppTipoMensagem tipoMensagem;

    @Column(name = "mensagem", nullable = false, length = 4000)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WhatsAppEnvioStatus status;

    @Column(name = "provider_message_id", length = 120)
    private String providerMessageId;

    @Column(name = "tentativas", nullable = false)
    private Integer tentativas = 0;

    @Column(name = "erro", length = 2000)
    private String erro;

    @Column(name = "data_solicitacao", nullable = false)
    private OffsetDateTime dataSolicitacao;

    @Column(name = "data_envio")
    private OffsetDateTime dataEnvio;

    @Column(name = "chave_idempotencia", nullable = false, unique = true, length = 160)
    private String chaveIdempotencia;
}
