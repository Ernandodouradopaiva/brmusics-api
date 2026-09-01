package br.gov.ce.sps.projetoa.infrastructure.util;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TelefoneUtilsTest {

    @Test
    void normalizaRemoveNaoDigitos() {
        assertThat(TelefoneUtils.normalizar("(85) 99999-8888")).isEqualTo("85999998888");
        assertThat(TelefoneUtils.normalizar("  ")).isNull();
        assertThat(TelefoneUtils.normalizar(null)).isNull();
    }

    @Test
    void whatsappObrigatorioEValido() {
        TelefoneUtils.validarWhatsapp("85999998888");
        assertThatThrownBy(() -> TelefoneUtils.validarWhatsapp(null))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Informe o WhatsApp do músico.");
        assertThatThrownBy(() -> TelefoneUtils.validarWhatsapp("123"))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Informe um WhatsApp válido com DDD.");
    }
}
