package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record WhatsAppMensagemContexto(
        String nomeMusico,
        String competencia,
        Integer versao,
        List<WhatsAppEscalaMensalBloco> escalas) {

    public WhatsAppMensagemContexto {
        escalas = escalas == null ? List.of() : List.copyOf(escalas);
    }

    public WhatsAppMensagemContexto(String nomeMusico, String competencia, Integer versao) {
        this(nomeMusico, competencia, versao, List.of());
    }

    public static WhatsAppEscalaMensalBloco bloco(
            LocalDate data,
            LocalTime horaInicio,
            String titulo,
            String localNome,
            List<String> funcoes,
            List<WhatsAppRepertorioItemResumo> repertorio) {
        return new WhatsAppEscalaMensalBloco(data, horaInicio, titulo, localNome, funcoes, repertorio);
    }
}
