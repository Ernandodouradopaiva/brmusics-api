package br.gov.ce.sps.projetoa.infrastructure.whatsapp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WhatsAppPhoneFormatterTest {

    @Test
    void prefixaDdiQuandoAusente() {
        assertThat(WhatsAppPhoneFormatter.paraE164("8599998888", "55")).isEqualTo("558599998888");
    }

    @Test
    void preservaNumeroQueJaTemDdi() {
        assertThat(WhatsAppPhoneFormatter.paraE164("558599998888", "55")).isEqualTo("558599998888");
    }
}
