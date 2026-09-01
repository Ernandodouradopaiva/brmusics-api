package br.gov.ce.sps.projetoa.api.dto;

import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppEnvioStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class WhatsAppComunicacaoItemModel {

    private UUID musicoCodigo;
    private String musicoNome;
    private WhatsAppTipoMensagem tipo;
    private String tipoRotulo;
    private String resumo;
    private LocalDate data;
    private String celebracaoTitulo;
    private WhatsAppEnvioStatus statusEnvio;
}
