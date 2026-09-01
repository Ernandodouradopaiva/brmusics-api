package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.infrastructure.whatsapp.WhatsAppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WhatsAppWebhookService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppWebhookService.class);

    private final WhatsAppProperties properties;
    private final WhatsAppLogService whatsAppLogService;
    private final ObjectMapper objectMapper;

    public Optional<String> verificar(String mode, String token, String challenge) {
        if (!"subscribe".equals(mode) || !StringUtils.hasText(properties.getWebhookVerifyToken())) {
            return Optional.empty();
        }
        if (!properties.getWebhookVerifyToken().equals(token) || !StringUtils.hasText(challenge)) {
            return Optional.empty();
        }
        return Optional.of(challenge);
    }

    public void processar(String signature, String payload) {
        if (!assinaturaValida(signature, payload)) {
            log.warn("Webhook WhatsApp rejeitado: assinatura inválida.");
            return;
        }
        if (!StringUtils.hasText(payload)) {
            return;
        }
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode entries = root.path("entry");
            if (!entries.isArray()) {
                return;
            }
            for (JsonNode entry : entries) {
                JsonNode changes = entry.path("changes");
                if (!changes.isArray()) {
                    continue;
                }
                for (JsonNode change : changes) {
                    JsonNode statuses = change.path("value").path("statuses");
                    if (!statuses.isArray()) {
                        continue;
                    }
                    for (JsonNode statusNode : statuses) {
                        String providerId = statusNode.path("id").asText(null);
                        String status = statusNode.path("status").asText(null);
                        String erro = extrairErro(statusNode);
                        whatsAppLogService.atualizarStatusProvedor(providerId, status, erro);
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Webhook WhatsApp ignorado: payload inválido.");
        }
    }

    private boolean assinaturaValida(String signature, String payload) {
        if (properties.isFake()) {
            return true;
        }
        if (!StringUtils.hasText(properties.getAppSecret())) {
            log.warn("Webhook WhatsApp rejeitado: app secret não configurado.");
            return false;
        }
        if (!StringUtils.hasText(signature) || !signature.startsWith("sha256=") || payload == null) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(properties.getAppSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] esperado = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            byte[] informado = HexFormat.of().parseHex(signature.substring("sha256=".length()).trim());
            return java.security.MessageDigest.isEqual(esperado, informado);
        } catch (Exception ex) {
            return false;
        }
    }

    private static String extrairErro(JsonNode statusNode) {
        JsonNode errors = statusNode.path("errors");
        if (errors.isArray() && errors.size() > 0) {
            String title = errors.get(0).path("title").asText(null);
            String message = errors.get(0).path("message").asText(null);
            if (StringUtils.hasText(title) && StringUtils.hasText(message)) {
                return title + ": " + message;
            }
            if (StringUtils.hasText(message)) {
                return message;
            }
            return title;
        }
        return null;
    }
}
