package br.gov.ce.sps.projetoa.infrastructure.whatsapp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FakeWhatsAppClientTest {

    private final FakeWhatsAppClient client = new FakeWhatsAppClient();

    @Test
    void naoChamaRedeEMarcaSucesso() {
        WhatsAppSendResult resultado = client.enviar("558599998888", "Olá, escala publicada.");

        assertThat(resultado.sucesso()).isTrue();
        assertThat(resultado.providerMessageId()).startsWith("fake-");
        assertThat(resultado.erro()).isNull();
    }
}
