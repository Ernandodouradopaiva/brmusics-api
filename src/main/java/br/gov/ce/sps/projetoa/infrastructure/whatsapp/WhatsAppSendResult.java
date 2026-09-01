package br.gov.ce.sps.projetoa.infrastructure.whatsapp;

public record WhatsAppSendResult(boolean sucesso, String providerMessageId, String erro) {

    public static WhatsAppSendResult ok(String providerMessageId) {
        return new WhatsAppSendResult(true, providerMessageId, null);
    }

    public static WhatsAppSendResult falha(String erro) {
        return new WhatsAppSendResult(false, null, erro);
    }
}
