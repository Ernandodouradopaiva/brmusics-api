package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.domain.event.EscalaPublicadaEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EscalaPublicadaWhatsAppListener {

    private static final Logger log = LoggerFactory.getLogger(EscalaPublicadaWhatsAppListener.class);

    private final WhatsAppService whatsAppService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEscalaPublicada(EscalaPublicadaEvent event) {
        if (event == null || event.publicacaoCodigo() == null) {
            return;
        }
        try {
            whatsAppService.enfileirarPublicacao(event.publicacaoCodigo());
        } catch (RuntimeException ex) {
            log.warn("Não foi possível enfileirar WhatsApp da publicação {}: {}",
                    event.publicacaoCodigo(), ex.getMessage());
        }
    }
}
