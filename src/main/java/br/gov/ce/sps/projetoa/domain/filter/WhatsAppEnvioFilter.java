package br.gov.ce.sps.projetoa.domain.filter;

import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppEnvioStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WhatsAppEnvioFilter {
    private WhatsAppEnvioStatus status;
    private WhatsAppTipoMensagem tipoMensagem;
    private String telefone;
    private String musico;
}
