package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.domain.model.enums.EscalaPublicacaoAlteracaoTipo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EscalaPublicacaoComparadorTest {

    @Test
    void detectaMusicoAdicionadoRemovidoFuncaoHorarioERepertorio() {
        UUID celebracao = UUID.randomUUID();
        UUID ana = UUID.randomUUID();
        UUID bruno = UUID.randomUUID();
        UUID vocal = UUID.randomUUID();
        UUID violao = UUID.randomUUID();
        UUID musicaA = UUID.randomUUID();
        UUID musicaB = UUID.randomUUID();

        EscalaPublicacaoSnapshot anterior = new EscalaPublicacaoSnapshot(
                celebracao,
                "Missa 19h",
                LocalDate.of(2026, 9, 6),
                LocalTime.of(19, 0),
                LocalTime.of(20, 30),
                "Matriz",
                List.of(new EscalaPublicacaoSnapshotParticipacao(ana, "Ana", vocal, "Vocal")),
                List.of(new EscalaPublicacaoSnapshotRepertorio(musicaA, "Entrada", "ENTRADA", 0, "G")));

        EscalaPublicacaoSnapshot atual = new EscalaPublicacaoSnapshot(
                celebracao,
                "Missa 19h",
                LocalDate.of(2026, 9, 6),
                LocalTime.of(18, 0),
                LocalTime.of(19, 30),
                "Matriz",
                List.of(
                        new EscalaPublicacaoSnapshotParticipacao(ana, "Ana", violao, "Violão"),
                        new EscalaPublicacaoSnapshotParticipacao(bruno, "Bruno", vocal, "Vocal")),
                List.of(new EscalaPublicacaoSnapshotRepertorio(musicaB, "Santo", "SANTO", 0, "A")));

        List<EscalaPublicacaoDiffItem> diffs = EscalaPublicacaoComparador.comparar(List.of(anterior), List.of(atual));

        assertThat(diffs)
                .extracting(EscalaPublicacaoDiffItem::tipo)
                .contains(
                        EscalaPublicacaoAlteracaoTipo.HORARIO_ALTERADO,
                        EscalaPublicacaoAlteracaoTipo.FUNCAO_ALTERADA,
                        EscalaPublicacaoAlteracaoTipo.MUSICO_ADICIONADO,
                        EscalaPublicacaoAlteracaoTipo.REPERTORIO_ALTERADO);
        assertThat(diffs)
                .extracting(EscalaPublicacaoDiffItem::tipo)
                .doesNotContain(EscalaPublicacaoAlteracaoTipo.MUSICO_REMOVIDO);
    }

    @Test
    void versaoInicialNaoGeraDiffQuandoNaoHaAnterior() {
        assertThat(EscalaPublicacaoComparador.comparar(List.of(), List.of())).isEmpty();
    }

    @Test
    void celebracaoAusenteCanceladaGeraDiffPorMusico() {
        UUID celebracao = UUID.randomUUID();
        UUID ana = UUID.randomUUID();
        UUID vocal = UUID.randomUUID();
        UUID musicaA = UUID.randomUUID();
        EscalaPublicacaoSnapshot anterior = new EscalaPublicacaoSnapshot(
                celebracao,
                "Missa 19h",
                LocalDate.of(2026, 9, 13),
                LocalTime.of(19, 0),
                LocalTime.of(20, 30),
                "Matriz",
                List.of(new EscalaPublicacaoSnapshotParticipacao(ana, "Ana", vocal, "Vocal")),
                List.of(new EscalaPublicacaoSnapshotRepertorio(musicaA, "Entrada", "ENTRADA", 0, "G")));

        List<EscalaPublicacaoDiffItem> diffs = EscalaPublicacaoComparador.comparar(
                List.of(anterior),
                List.of(),
                Set.of(celebracao));

        assertThat(diffs)
                .extracting(EscalaPublicacaoDiffItem::tipo)
                .containsExactly(EscalaPublicacaoAlteracaoTipo.CELEBRACAO_CANCELADA)
                .doesNotContain(EscalaPublicacaoAlteracaoTipo.REPERTORIO_ALTERADO);
    }

    @Test
    void celebracaoAusenteSemCancelamentoRemoveMusico() {
        UUID celebracao = UUID.randomUUID();
        UUID ana = UUID.randomUUID();
        UUID vocal = UUID.randomUUID();
        EscalaPublicacaoSnapshot anterior = new EscalaPublicacaoSnapshot(
                celebracao,
                "Missa 19h",
                LocalDate.of(2026, 9, 13),
                LocalTime.of(19, 0),
                LocalTime.of(20, 30),
                "Matriz",
                List.of(new EscalaPublicacaoSnapshotParticipacao(ana, "Ana", vocal, "Vocal")),
                List.of());

        List<EscalaPublicacaoDiffItem> diffs = EscalaPublicacaoComparador.comparar(List.of(anterior), List.of());

        assertThat(diffs)
                .extracting(EscalaPublicacaoDiffItem::tipo)
                .containsExactly(EscalaPublicacaoAlteracaoTipo.MUSICO_REMOVIDO);
    }
}
