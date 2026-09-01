package br.gov.ce.sps.projetoa.infrastructure.whatsapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "app.whatsapp.provider", havingValue = "cloud")
public class CloudWhatsAppClient implements WhatsAppClient {

    private static final Logger log = LoggerFactory.getLogger(CloudWhatsAppClient.class);

    private final WhatsAppProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public CloudWhatsAppClient(
            WhatsAppProperties properties,
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        String base = trimSlash(properties.getApiBaseUrl()) + "/" + trimSlash(properties.getApiVersion());
        this.restClient = restClientBuilder.baseUrl(base).build();
    }

    @Override
    public WhatsAppSendResult enviar(String telefoneE164, String mensagem) {
        if (!StringUtils.hasText(properties.getAccessToken()) || !StringUtils.hasText(properties.getPhoneNumberId())) {
            return WhatsAppSendResult.falha("Integração WhatsApp Cloud API não configurada.");
        }
        if (!StringUtils.hasText(telefoneE164)) {
            return WhatsAppSendResult.falha("Telefone de destino inválido.");
        }
        try {
            Map<String, Object> payload = montarPayload(telefoneE164, mensagem);
            String body = restClient.post()
                    .uri("/{phoneNumberId}/messages", properties.getPhoneNumberId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);
            return parseSucesso(body);
        } catch (RestClientResponseException ex) {
            String erro = extrairErro(ex.getResponseBodyAsString(), ex.getStatusCode().value());
            log.warn("WhatsApp Cloud API recusou o envio (HTTP {}).", ex.getStatusCode().value());
            return WhatsAppSendResult.falha(erro);
        } catch (RuntimeException ex) {
            log.warn("Falha ao chamar WhatsApp Cloud API: {}", ex.getClass().getSimpleName());
            return WhatsAppSendResult.falha("Falha de comunicação com a API do WhatsApp.");
        }
    }

    private Map<String, Object> montarPayload(String telefoneE164, String mensagem) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("recipient_type", "individual");
        payload.put("to", telefoneE164);
        if (StringUtils.hasText(properties.getTemplateName())) {
            payload.put("type", "template");
            Map<String, Object> parameter = new LinkedHashMap<>();
            parameter.put("type", "text");
            parameter.put("text", truncar(mensagem, 1024));
            Map<String, Object> component = new LinkedHashMap<>();
            component.put("type", "body");
            component.put("parameters", List.of(parameter));
            Map<String, Object> template = new LinkedHashMap<>();
            template.put("name", properties.getTemplateName());
            template.put("language", Map.of("code", properties.getTemplateLanguage()));
            template.put("components", List.of(component));
            payload.put("template", template);
        } else {
            payload.put("type", "text");
            payload.put("text", Map.of("preview_url", false, "body", truncar(mensagem, 4096)));
        }
        return payload;
    }

    private WhatsAppSendResult parseSucesso(String body) {
        if (!StringUtils.hasText(body)) {
            return WhatsAppSendResult.falha("Resposta vazia da API do WhatsApp.");
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode messages = root.path("messages");
            if (messages.isArray() && messages.size() > 0) {
                String id = messages.get(0).path("id").asText(null);
                if (StringUtils.hasText(id)) {
                    return WhatsAppSendResult.ok(id);
                }
            }
            return WhatsAppSendResult.falha("Resposta da API do WhatsApp sem identificador da mensagem.");
        } catch (Exception ex) {
            return WhatsAppSendResult.falha("Não foi possível interpretar a resposta da API do WhatsApp.");
        }
    }

    private String extrairErro(String body, int status) {
        if (StringUtils.hasText(body)) {
            try {
                JsonNode error = objectMapper.readTree(body).path("error");
                String message = error.path("message").asText(null);
                if (StringUtils.hasText(message)) {
                    return truncar(message, 500);
                }
            } catch (Exception ignored) {
                // corpo não JSON — usa status HTTP
            }
        }
        return "API do WhatsApp retornou HTTP " + status + ".";
    }

    private static String trimSlash(String valor) {
        if (valor == null) {
            return "";
        }
        String v = valor.trim();
        while (v.endsWith("/")) {
            v = v.substring(0, v.length() - 1);
        }
        return v;
    }

    private static String truncar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= max ? texto : texto.substring(0, max);
    }
}
