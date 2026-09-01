package br.gov.ce.sps.projetoa.domain.model.enums;

public enum WhatsAppEnvioStatus {
    PENDENTE("Pendente"),
    PROCESSANDO("Processando"),
    ENVIADO("Enviado"),
    ERRO("Erro");

    private final String rotulo;

    WhatsAppEnvioStatus(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
