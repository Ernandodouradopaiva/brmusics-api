package br.gov.ce.sps.projetoa.infrastructure.whatsapp;

import br.gov.ce.sps.projetoa.infrastructure.util.TelefoneUtils;
import org.springframework.util.StringUtils;

public final class WhatsAppPhoneFormatter {

    private WhatsAppPhoneFormatter() {
    }

    public static String paraE164(String telefone, String codigoPais) {
        String digits = TelefoneUtils.normalizar(telefone);
        if (digits == null) {
            return null;
        }
        String ddi = StringUtils.hasText(codigoPais) ? codigoPais.replaceAll("\\D", "") : "55";
        if (!StringUtils.hasText(ddi)) {
            ddi = "55";
        }
        if (digits.startsWith(ddi)) {
            return digits;
        }
        return ddi + digits;
    }
}
