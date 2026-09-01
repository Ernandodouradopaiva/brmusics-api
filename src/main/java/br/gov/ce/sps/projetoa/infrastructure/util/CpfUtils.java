package br.gov.ce.sps.projetoa.infrastructure.util;

public final class CpfUtils {

    private CpfUtils() {
    }

    public static String normalizar(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return cpf;
        }
        return cpf.replaceAll("\\D", "");
    }
}
