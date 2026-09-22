package br.gov.ce.sps.projetoa.domain.model.enums;

public enum FrequenciaStatus {
    PRESENTE("Presente"),
    FALTOU("Faltou"),
    DISPENSADO("Foi dispensado"),
    NAO_MINISTERIO("Não é do Ministério");

    private final String rotulo;

    FrequenciaStatus(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
