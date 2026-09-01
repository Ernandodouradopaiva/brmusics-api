package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.domain.service.whatsapp.WhatsAppWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/whatsapp/webhook")
@RequiredArgsConstructor
public class WhatsAppWebhookController {

    private final WhatsAppWebhookService whatsAppWebhookService;

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verificar(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String token,
            @RequestParam(name = "hub.challenge", required = false) String challenge) {
        return whatsAppWebhookService.verificar(mode, token, challenge)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(403).build());
    }

    @PostMapping
    public ResponseEntity<Void> receber(
            @RequestHeader(name = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody(required = false) String payload) {
        whatsAppWebhookService.processar(signature, payload);
        return ResponseEntity.ok().build();
    }
}
