package br.gov.ce.sps.projetoa.infrastructure.whatsapp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.whatsapp")
public class WhatsAppProperties {

    private boolean enabled;
    private String provider = "fake";
    private String apiBaseUrl = "https://graph.facebook.com";
    private String apiVersion = "v21.0";
    private String phoneNumberId = "";
    private String accessToken = "";
    private String webhookVerifyToken = "";
    private String appSecret = "";
    private String defaultCountryCode = "55";
    private String templateName = "";
    private String templateLanguage = "pt_BR";
    private int rateLimitPerMinute = 20;
    private int maxTentativas = 3;
    private int retryIntervalSeconds = 60;
    private long pollIntervalMs = 5000;
    private int stuckProcessingSeconds = 300;
    private int batchSize = 20;

    public boolean isFake() {
        return "fake".equalsIgnoreCase(provider);
    }

    public boolean isCloud() {
        return "cloud".equalsIgnoreCase(provider);
    }
}
