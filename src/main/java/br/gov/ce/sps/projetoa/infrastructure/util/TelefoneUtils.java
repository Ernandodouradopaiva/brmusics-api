package br.gov.ce.sps.projetoa.infrastructure.util;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;

public final class TelefoneUtils {

    private static final int MIN_DIGITOS = 10;
    private static final int MAX_DIGITOS = 13;

    private TelefoneUtils() {
    }

    public static String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String digits = valor.replaceAll("\\D", "");
        return digits.isEmpty() ? null : digits;
    }

    public static void validarWhatsapp(String whatsappNormalizado) {
        if (whatsappNormalizado == null) {
            throw new NegocioException("Informe o WhatsApp do músico.");
        }
        validarQuantidadeDigitos(whatsappNormalizado, "WhatsApp");
    }

    public static void validarTelefoneOpcional(String telefoneNormalizado) {
        if (telefoneNormalizado == null) {
            return;
        }
        validarQuantidadeDigitos(telefoneNormalizado, "telefone");
    }

    private static void validarQuantidadeDigitos(String digits, String campo) {
        if (digits.length() < MIN_DIGITOS || digits.length() > MAX_DIGITOS) {
            throw new NegocioException("Informe um " + campo + " válido com DDD.");
        }
    }
}
