package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

record WhatsAppDestinatarioNotificacao(
        UUID publicacaoCodigo,
        UUID musicoCodigo,
        String musicoNome,
        WhatsAppTipoMensagem tipo,
        List<WhatsAppEscalaMensalBloco> blocos,
        UUID celebracaoCodigo,
        String resumo) {

    WhatsAppDestinatarioNotificacao {
        blocos = blocos == null ? List.of() : List.copyOf(blocos);
        resumo = resumo == null ? "" : resumo;
    }

    String chaveIdempotencia() {
        String sufixo = celebracaoCodigo != null ? celebracaoCodigo.toString() : "ALL";
        return tipo.name() + ":" + publicacaoCodigo + ":" + musicoCodigo + ":" + sufixo;
    }
}

record WhatsAppCelebracaoResumo(
        UUID celebracaoCodigo,
        String titulo,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim,
        String localNome,
        List<WhatsAppParticipacaoResumo> participacoes,
        List<WhatsAppRepertorioItemResumo> repertorio) {

    WhatsAppCelebracaoResumo {
        participacoes = participacoes == null ? List.of() : List.copyOf(participacoes);
        repertorio = repertorio == null ? List.of() : List.copyOf(repertorio);
    }
}

record WhatsAppParticipacaoResumo(
        UUID musicoCodigo,
        String musicoNome,
        UUID instrumentoCodigo,
        String instrumentoNome) {
}
