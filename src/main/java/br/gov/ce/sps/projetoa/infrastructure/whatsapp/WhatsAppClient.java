package br.gov.ce.sps.projetoa.infrastructure.whatsapp;

public interface WhatsAppClient {

    WhatsAppSendResult enviar(String telefoneE164, String mensagem);
}
