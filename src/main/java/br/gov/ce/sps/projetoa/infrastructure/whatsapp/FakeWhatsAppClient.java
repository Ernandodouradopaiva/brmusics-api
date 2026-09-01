package br.gov.ce.sps.projetoa.infrastructure.whatsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.whatsapp.provider", havingValue = "fake", matchIfMissing = true)
public class FakeWhatsAppClient implements WhatsAppClient {

    private static final Logger log = LoggerFactory.getLogger(FakeWhatsAppClient.class);

    @Override
    public WhatsAppSendResult enviar(String telefoneE164, String mensagem) {
        String id = "fake-" + UUID.randomUUID();
        log.info("WhatsApp fake: mensagem simulada para {} (id={})", mascarar(telefoneE164), id);
        return WhatsAppSendResult.ok(id);
    }

    private static String mascarar(String telefone) {
        if (telefone == null || telefone.length() < 4) {
            return "****";
        }
        return "****" + telefone.substring(telefone.length() - 4);
    }
}
