package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record WhatsAppEscalaMensalBloco(
        LocalDate data,
        LocalTime horaInicio,
        String celebracaoTitulo,
        String localNome,
        List<String> funcoes,
        List<WhatsAppRepertorioItemResumo> repertorio) {

    public WhatsAppEscalaMensalBloco {
        funcoes = funcoes == null ? List.of() : List.copyOf(funcoes);
        repertorio = repertorio == null ? List.of() : List.copyOf(repertorio);
    }
}

