package br.gov.ce.sps.projetoa.api.dto;

import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppEnvioStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class WhatsAppEnvioModel {
    private UUID codigo;
    private UUID musicoCodigo;
    private String musicoNome;
    private String telefone;
    private WhatsAppTipoMensagem tipoMensagem;
    private String tipoMensagemRotulo;
    private String mensagem;
    private WhatsAppEnvioStatus status;
    private String statusRotulo;
    private String providerMessageId;
    private Integer tentativas;
    private String erro;
    private OffsetDateTime dataSolicitacao;
    private OffsetDateTime dataEnvio;
}
