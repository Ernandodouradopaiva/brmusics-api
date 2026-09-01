package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WhatsAppPublicacaoNotificacaoResolverTest {

    @Test
    void agrupaVariasCelebracoesEmUmaMensagemPorMusico() {
        UUID publicacao = UUID.randomUUID();
        UUID musico = UUID.randomUUID();
        UUID outro = UUID.randomUUID();
        WhatsAppCelebracaoResumo cel1 = celebracao(
                UUID.randomUUID(),
                LocalDate.of(2026, 9, 6),
                List.of(new WhatsAppParticipacaoResumo(musico, "JOAO", UUID.randomUUID(), "Violão")),
                List.of(new WhatsAppRepertorioItemResumo("ENTRADA", "Eis-me Aqui Senhor", "E", 0)));
        WhatsAppCelebracaoResumo cel2 = celebracao(
                UUID.randomUUID(),
                LocalDate.of(2026, 9, 27),
                List.of(
                        new WhatsAppParticipacaoResumo(musico, "JOAO", UUID.randomUUID(), "Vocal"),
                        new WhatsAppParticipacaoResumo(outro, "MARIA", UUID.randomUUID(), "Teclado")),
                List.of(new WhatsAppRepertorioItemResumo("COMUNHAO", "Pão da Vida", "D", 0)));

        List<WhatsAppDestinatarioNotificacao> destinos =
                WhatsAppPublicacaoNotificacaoResolver.resolver(publicacao, List.of(cel2, cel1));

        assertThat(destinos).hasSize(2);
        WhatsAppDestinatarioNotificacao joao = destinos.stream()
                .filter(d -> musico.equals(d.musicoCodigo()))
                .findFirst()
                .orElseThrow();
        assertThat(joao.tipo()).isEqualTo(WhatsAppTipoMensagem.PUBLICACAO_ESCALA);
        assertThat(joao.blocos()).hasSize(2);
        assertThat(joao.blocos().getFirst().data()).isEqualTo(LocalDate.of(2026, 9, 6));
        assertThat(joao.blocos().getLast().data()).isEqualTo(LocalDate.of(2026, 9, 27));
        assertThat(joao.chaveIdempotencia()).isEqualTo(
                "PUBLICACAO_ESCALA:" + publicacao + ":" + musico + ":ALL");
        assertThat(destinos.stream().filter(d -> outro.equals(d.musicoCodigo())).findFirst().orElseThrow().blocos())
                .hasSize(1);
    }

    @Test
    void publicacaoInicialUsaMensagemMensalAgrupada() {
        UUID publicacao = UUID.randomUUID();
        UUID musico = UUID.randomUUID();
        WhatsAppCelebracaoResumo cel = celebracao(
                UUID.randomUUID(),
                LocalDate.of(2026, 9, 6),
                List.of(new WhatsAppParticipacaoResumo(musico, "ANA", UUID.randomUUID(), "Violão")),
                List.of());

        List<WhatsAppDestinatarioNotificacao> destinos =
                WhatsAppPublicacaoNotificacaoResolver.resolver(publicacao, List.of(cel));

        assertThat(destinos).hasSize(1);
        assertThat(destinos.getFirst().tipo()).isEqualTo(WhatsAppTipoMensagem.PUBLICACAO_ESCALA);
        assertThat(destinos.getFirst().blocos()).hasSize(1);
        assertThat(destinos.getFirst().chaveIdempotencia()).endsWith(":ALL");
    }

    private static WhatsAppCelebracaoResumo celebracao(
            UUID codigo,
            LocalDate data,
            List<WhatsAppParticipacaoResumo> participacoes,
            List<WhatsAppRepertorioItemResumo> repertorio) {
        return new WhatsAppCelebracaoResumo(
                codigo,
                "Missa Dominical",
                data,
                LocalTime.of(19, 0),
                LocalTime.of(20, 30),
                "Matriz",
                participacoes,
                repertorio);
    }
}
