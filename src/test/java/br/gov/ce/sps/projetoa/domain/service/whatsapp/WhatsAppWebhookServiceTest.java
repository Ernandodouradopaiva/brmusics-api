package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.infrastructure.whatsapp.WhatsAppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WhatsAppWebhookServiceTest {

    @Mock
    private WhatsAppLogService whatsAppLogService;

    private WhatsAppProperties properties;
    private WhatsAppWebhookService service;

    @BeforeEach
    void setUp() {
        properties = new WhatsAppProperties();
        service = new WhatsAppWebhookService(properties, whatsAppLogService, new ObjectMapper());
    }

    @Test
    void providerCloudSemAppSecretRejeitaPayload() {
        properties.setProvider("cloud");
        properties.setEnabled(true);
        properties.setAppSecret("");

        service.processar("sha256=abc", "{\"entry\":[]}");

        verify(whatsAppLogService, never()).atualizarStatusProvedor(any(), any(), any());
    }

    @Test
    void assinaturaInvalidaNaoAtualizaEnvio() {
        properties.setProvider("cloud");
        properties.setAppSecret("segredo");

        service.processar("sha256=00", "{\"entry\":[]}");

        verify(whatsAppLogService, never()).atualizarStatusProvedor(any(), any(), any());
    }

    @Test
    void assinaturaValidaAceitaPayload() throws Exception {
        properties.setProvider("cloud");
        properties.setAppSecret("segredo");
        String payload = "{\"entry\":[]}";

        service.processar("sha256=" + hmac(payload, "segredo"), payload);

        verify(whatsAppLogService, never()).atualizarStatusProvedor(any(), any(), any());
    }

    @Test
    void verifyTokenIncorretoNaoDevolveChallenge() {
        properties.setWebhookVerifyToken("esperado");
        assertThat(service.verificar("subscribe", "errado", "desafio")).isEmpty();
    }

    @Test
    void verifyTokenCorretoDevolveChallenge() {
        properties.setWebhookVerifyToken("esperado");
        assertThat(service.verificar("subscribe", "esperado", "desafio")).contains("desafio");
    }

    private static String hmac(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    }
}
