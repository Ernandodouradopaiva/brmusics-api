package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WhatsAppTemplateServiceTest {

    private final WhatsAppTemplateService service = new WhatsAppTemplateService();

    @Test
    void montaUmaMensagemMensalComTodasAsEscalasDoMusico() {
        WhatsAppMensagemContexto contexto = new WhatsAppMensagemContexto(
                "João",
                "Setembro/2026",
                1,
                List.of(
                        new WhatsAppEscalaMensalBloco(
                                LocalDate.of(2026, 9, 6),
                                LocalTime.of(19, 0),
                                "Missa Dominical",
                                "Matriz",
                                List.of("Violão"),
                                List.of(
                                        new WhatsAppRepertorioItemResumo("ENTRADA", "Eis-me Aqui Senhor", "E", 0),
                                        new WhatsAppRepertorioItemResumo("GLORIA", "Glória", "G", 1),
                                        new WhatsAppRepertorioItemResumo("COMUNHAO", "Pão da Vida", "D", 2))),
                        new WhatsAppEscalaMensalBloco(
                                LocalDate.of(2026, 9, 27),
                                LocalTime.of(19, 0),
                                "Missa Dominical",
                                null,
                                List.of("Vocal"),
                                List.of(new WhatsAppRepertorioItemResumo("OFERTORIO", "Minha Vida Tem Sentido", "G", 0)))));

        String texto = service.montar(WhatsAppTipoMensagem.PUBLICACAO_ESCALA, contexto);

        assertThat(texto)
                .contains("🎵 BRMusics — Escala de Setembro/2026")
                .contains("Olá, João!")
                .contains("06/09/2026")
                .contains("27/09/2026")
                .contains("Domingo")
                .contains("Função: Violão")
                .contains("Função: Vocal")
                .contains("Entrada: Eis-me Aqui Senhor — Tom E")
                .contains("Glória: Glória — Tom G")
                .contains("Comunhão: Pão da Vida — Tom D")
                .contains("Ofertório: Minha Vida Tem Sentido — Tom G")
                .contains("Acesse o BRMusics para consultar sua escala completa.");
        assertThat(texto.indexOf("06/09/2026")).isLessThan(texto.indexOf("27/09/2026"));
    }

    @Test
    void naoGeraMensagemPorCelebracaoQuandoHaVariosBlocos() {
        WhatsAppMensagemContexto contexto = new WhatsAppMensagemContexto(
                "João",
                "Setembro/2026",
                1,
                List.of(
                        new WhatsAppEscalaMensalBloco(
                                LocalDate.of(2026, 9, 6), LocalTime.of(19, 0), "Missa", null, List.of("Violão"), List.of()),
                        new WhatsAppEscalaMensalBloco(
                                LocalDate.of(2026, 9, 13), LocalTime.of(19, 0), "Missa", null, List.of("Violão"), List.of())));

        String texto = service.montar(WhatsAppTipoMensagem.PUBLICACAO_ESCALA, contexto);

        assertThat(texto.split("Olá, João!", -1)).hasSize(2);
    }

    @Test
    void montaMensagemPontualDeAlteracaoSemEscalaMensal() {
        WhatsAppMensagemContexto contexto = new WhatsAppMensagemContexto(
                "Maria",
                "Setembro/2026",
                2,
                List.of(new WhatsAppEscalaMensalBloco(
                        LocalDate.of(2026, 9, 13),
                        LocalTime.of(19, 0),
                        "Missa Dominical",
                        "Matriz",
                        List.of("Vocal"),
                        List.of(new WhatsAppRepertorioItemResumo("ENTRADA", "Eis-me Aqui Senhor", "E", 0)))));

        String nova = service.montar(WhatsAppTipoMensagem.NOVA_ESCALA, contexto);
        String remocao = service.montar(WhatsAppTipoMensagem.REMOCAO_ESCALA, contexto);
        String repertorio = service.montar(WhatsAppTipoMensagem.ALTERACAO_REPERTORIO, contexto);
        String cancelada = service.montar(WhatsAppTipoMensagem.CELEBRACAO_CANCELADA, contexto);

        assertThat(nova)
                .contains("Nova escala")
                .contains("Você foi adicionado(a)")
                .contains("Função: Vocal")
                .doesNotContain("Confira sua escala deste mês");
        assertThat(remocao)
                .contains("Você foi removido(a) da celebração de 13/09/2026")
                .doesNotContain("Função: Vocal");
        assertThat(repertorio)
                .contains("O repertório da celebração de 13/09/2026 foi alterado")
                .contains("Eis-me Aqui Senhor");
        assertThat(cancelada)
                .contains("A celebração Missa Dominical de 13/09/2026 foi cancelada")
                .doesNotContain("Função: Vocal");
    }
}
