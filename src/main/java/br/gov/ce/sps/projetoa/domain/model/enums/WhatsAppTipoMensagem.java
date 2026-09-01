package br.gov.ce.sps.projetoa.domain.model.enums;

public enum WhatsAppTipoMensagem {
    PUBLICACAO_ESCALA("Publicação da escala"),
    NOVA_ESCALA("Nova escala"),
    ALTERACAO_ESCALA("Alteração da escala"),
    REMOCAO_ESCALA("Remoção da escala"),
    ALTERACAO_REPERTORIO("Alteração de repertório"),
    CELEBRACAO_CANCELADA("Celebração cancelada"),
    LEMBRETE("Lembrete");

    private final String rotulo;

    WhatsAppTipoMensagem(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
