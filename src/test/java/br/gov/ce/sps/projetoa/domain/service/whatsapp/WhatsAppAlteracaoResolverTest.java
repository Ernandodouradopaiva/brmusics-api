package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WhatsAppAlteracaoResolverTest {

    private final UUID publicacao = UUID.randomUUID();
    private final UUID celebracao = UUID.randomUUID();
    private final UUID joao = UUID.randomUUID();
    private final UUID maria = UUID.randomUUID();
    private final UUID pedro = UUID.randomUUID();
    private final UUID violao = UUID.randomUUID();
    private final UUID vocal = UUID.randomUUID();
    private final UUID teclado = UUID.randomUUID();

    @Test
    void detectaMusicoAdicionado() {
        WhatsAppCelebracaoResumo anterior = missa(
                List.of(part(joao, "João Silva", violao, "Violão")),
                repertorio("ENTRADA", "A"));
        WhatsAppCelebracaoResumo atual = missa(
                List.of(
                        part(joao, "João Silva", violao, "Violão"),
                        part(maria, "Maria Souza", vocal, "Vocal")),
                repertorio("ENTRADA", "A"));

        List<WhatsAppDestinatarioNotificacao> destinos = resolver(anterior, atual, Set.of());

        assertThat(destinos).hasSize(1);
        WhatsAppDestinatarioNotificacao item = destinos.getFirst();
        assertThat(item.musicoCodigo()).isEqualTo(maria);
        assertThat(item.tipo()).isEqualTo(WhatsAppTipoMensagem.NOVA_ESCALA);
        assertThat(item.resumo()).isEqualTo("ADICIONADO à celebração de 13/09");
        assertThat(item.chaveIdempotencia()).isEqualTo(
                "NOVA_ESCALA:" + publicacao + ":" + maria + ":" + celebracao);
    }

    @Test
    void detectaMusicoRemovido() {
        WhatsAppCelebracaoResumo anterior = missa(
                List.of(
                        part(joao, "João Silva", violao, "Violão"),
                        part(maria, "Maria Souza", vocal, "Vocal")),
                repertorio("ENTRADA", "A"));
        WhatsAppCelebracaoResumo atual = missa(
                List.of(part(maria, "Maria Souza", vocal, "Vocal")),
                repertorio("ENTRADA", "A"));

        List<WhatsAppDestinatarioNotificacao> destinos = resolver(anterior, atual, Set.of());

        assertThat(destinos).hasSize(1);
        assertThat(destinos.getFirst().musicoCodigo()).isEqualTo(joao);
        assertThat(destinos.getFirst().tipo()).isEqualTo(WhatsAppTipoMensagem.REMOCAO_ESCALA);
        assertThat(destinos.getFirst().resumo()).isEqualTo("REMOVIDO da celebração de 13/09");
    }

    @Test
    void detectaFuncaoAlteradaSomenteDoMusicoAfetado() {
        WhatsAppCelebracaoResumo anterior = missa(
                List.of(
                        part(joao, "João Silva", violao, "Violão"),
                        part(maria, "Maria Souza", vocal, "Vocal")),
                repertorio("ENTRADA", "A"));
        WhatsAppCelebracaoResumo atual = missa(
                List.of(
                        part(joao, "João Silva", teclado, "Teclado"),
                        part(maria, "Maria Souza", vocal, "Vocal")),
                repertorio("ENTRADA", "A"));

        List<WhatsAppDestinatarioNotificacao> destinos = resolver(anterior, atual, Set.of());

        assertThat(destinos).hasSize(1);
        assertThat(destinos.getFirst().musicoCodigo()).isEqualTo(joao);
        assertThat(destinos.getFirst().tipo()).isEqualTo(WhatsAppTipoMensagem.ALTERACAO_ESCALA);
        assertThat(destinos.getFirst().resumo()).isEqualTo("FUNÇÃO ALTERADA na celebração de 13/09");
    }

    @Test
    void detectaHorarioAlteradoParaMusicosAtuais() {
        WhatsAppCelebracaoResumo anterior = missa(
                List.of(
                        part(joao, "João Silva", violao, "Violão"),
                        part(maria, "Maria Souza", vocal, "Vocal")),
                repertorio("ENTRADA", "A"));
        WhatsAppCelebracaoResumo atual = new WhatsAppCelebracaoResumo(
                celebracao,
                "Missa Dominical",
                LocalDate.of(2026, 9, 13),
                LocalTime.of(18, 0),
                LocalTime.of(19, 30),
                "Matriz",
                List.of(
                        part(joao, "João Silva", violao, "Violão"),
                        part(maria, "Maria Souza", vocal, "Vocal")),
                repertorio("ENTRADA", "A"));

        List<WhatsAppDestinatarioNotificacao> destinos = resolver(anterior, atual, Set.of());

        assertThat(destinos).hasSize(2);
        assertThat(destinos).allMatch(d -> d.tipo() == WhatsAppTipoMensagem.ALTERACAO_ESCALA);
        assertThat(destinos).extracting(WhatsAppDestinatarioNotificacao::musicoCodigo)
                .containsExactlyInAnyOrder(joao, maria);
        assertThat(destinos.getFirst().resumo()).isEqualTo("HORÁRIO ALTERADO na celebração de 13/09");
    }

    @Test
    void detectaCelebracaoCanceladaParaMusicosAnteriores() {
        WhatsAppCelebracaoResumo anterior = missa(
                List.of(
                        part(joao, "João Silva", violao, "Violão"),
                        part(maria, "Maria Souza", vocal, "Vocal")),
                repertorio("ENTRADA", "A"));

        List<WhatsAppDestinatarioNotificacao> destinos = WhatsAppAlteracaoResolver.resolver(
                publicacao,
                List.of(),
                List.of(anterior),
                Set.of(celebracao));

        assertThat(destinos).hasSize(2);
        assertThat(destinos).allMatch(d -> d.tipo() == WhatsAppTipoMensagem.CELEBRACAO_CANCELADA);
        assertThat(destinos).extracting(WhatsAppDestinatarioNotificacao::resumo)
                .containsOnly("CELEBRAÇÃO CANCELADA em 13/09");
        assertThat(destinos).noneMatch(d -> d.tipo() == WhatsAppTipoMensagem.ALTERACAO_REPERTORIO);
    }

    @Test
    void repertorioAlteradoComunicaSomenteMusicosDaCelebracao() {
        UUID outraCelebracao = UUID.randomUUID();
        WhatsAppCelebracaoResumo anteriorAlvo = missa(
                List.of(
                        part(pedro, "Pedro Silva", violao, "Violão"),
                        part(maria, "Maria Souza", vocal, "Vocal")),
                repertorio("ENTRADA", "A"));
        WhatsAppCelebracaoResumo atualAlvo = missa(
                List.of(
                        part(pedro, "Pedro Silva", violao, "Violão"),
                        part(maria, "Maria Souza", vocal, "Vocal")),
                repertorio("COMUNHAO", "B"));
        WhatsAppCelebracaoResumo outra = new WhatsAppCelebracaoResumo(
                outraCelebracao,
                "Missa 20/09",
                LocalDate.of(2026, 9, 20),
                LocalTime.of(19, 0),
                LocalTime.of(20, 30),
                "Matriz",
                List.of(part(joao, "João Silva", teclado, "Teclado")),
                repertorio("ENTRADA", "C"));

        List<WhatsAppDestinatarioNotificacao> destinos = WhatsAppAlteracaoResolver.resolver(
                publicacao,
                List.of(atualAlvo, outra),
                List.of(anteriorAlvo, outra),
                Set.of());

        assertThat(destinos).hasSize(2);
        assertThat(destinos).allMatch(d -> d.tipo() == WhatsAppTipoMensagem.ALTERACAO_REPERTORIO);
        assertThat(destinos).extracting(WhatsAppDestinatarioNotificacao::musicoCodigo)
                .containsExactlyInAnyOrder(pedro, maria)
                .doesNotContain(joao);
        assertThat(destinos).extracting(WhatsAppDestinatarioNotificacao::resumo)
                .containsOnly("REPERTÓRIO ALTERADO em 13/09");
    }

    @Test
    void musicoAdicionadoNaoRecebeRepertorioNemHorarioExtra() {
        WhatsAppCelebracaoResumo anterior = missa(
                List.of(part(joao, "João Silva", violao, "Violão")),
                repertorio("ENTRADA", "A"));
        WhatsAppCelebracaoResumo atual = new WhatsAppCelebracaoResumo(
                celebracao,
                "Missa Dominical",
                LocalDate.of(2026, 9, 13),
                LocalTime.of(18, 0),
                LocalTime.of(19, 30),
                "Matriz",
                List.of(
                        part(joao, "João Silva", violao, "Violão"),
                        part(maria, "Maria Souza", vocal, "Vocal")),
                repertorio("COMUNHAO", "B"));

        List<WhatsAppDestinatarioNotificacao> destinos = resolver(anterior, atual, Set.of());

        assertThat(destinos.stream().filter(d -> maria.equals(d.musicoCodigo())))
                .extracting(WhatsAppDestinatarioNotificacao::tipo)
                .containsExactly(WhatsAppTipoMensagem.NOVA_ESCALA);
        assertThat(destinos.stream().filter(d -> joao.equals(d.musicoCodigo())))
                .extracting(WhatsAppDestinatarioNotificacao::tipo)
                .containsExactlyInAnyOrder(
                        WhatsAppTipoMensagem.ALTERACAO_ESCALA,
                        WhatsAppTipoMensagem.ALTERACAO_REPERTORIO);
    }

    private List<WhatsAppDestinatarioNotificacao> resolver(
            WhatsAppCelebracaoResumo anterior,
            WhatsAppCelebracaoResumo atual,
            Set<UUID> canceladas) {
        return WhatsAppAlteracaoResolver.resolver(
                publicacao,
                List.of(atual),
                List.of(anterior),
                canceladas);
    }

    private WhatsAppCelebracaoResumo missa(
            List<WhatsAppParticipacaoResumo> participacoes,
            List<WhatsAppRepertorioItemResumo> repertorio) {
        return new WhatsAppCelebracaoResumo(
                celebracao,
                "Missa Dominical",
                LocalDate.of(2026, 9, 13),
                LocalTime.of(19, 0),
                LocalTime.of(20, 30),
                "Matriz",
                participacoes,
                repertorio);
    }

    private static WhatsAppParticipacaoResumo part(UUID musico, String nome, UUID instrumento, String funcao) {
        return new WhatsAppParticipacaoResumo(musico, nome, instrumento, funcao);
    }

    private static List<WhatsAppRepertorioItemResumo> repertorio(String momento, String titulo) {
        return List.of(new WhatsAppRepertorioItemResumo(momento, titulo, "E", 0));
    }
}
