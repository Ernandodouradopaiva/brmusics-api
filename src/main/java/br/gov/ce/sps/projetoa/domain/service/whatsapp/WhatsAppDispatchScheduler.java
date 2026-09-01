package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.infrastructure.whatsapp.WhatsAppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WhatsAppDispatchScheduler {

    private final WhatsAppMessageService whatsAppMessageService;
    private final WhatsAppProperties properties;

    @Scheduled(fixedDelayString = "${WHATSAPP_POLL_INTERVAL_MS:5000}")
    public void processarFila() {
        if (!properties.isEnabled()) {
            return;
        }
        whatsAppMessageService.processarFila();
    }
}
