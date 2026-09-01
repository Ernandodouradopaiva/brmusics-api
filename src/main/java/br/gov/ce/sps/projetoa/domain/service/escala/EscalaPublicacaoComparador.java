package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.domain.model.enums.EscalaPublicacaoAlteracaoTipo;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

final class EscalaPublicacaoComparador {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private EscalaPublicacaoComparador() {
    }

    static List<EscalaPublicacaoDiffItem> comparar(
            List<EscalaPublicacaoSnapshot> anterior,
            List<EscalaPublicacaoSnapshot> atual) {
        return comparar(anterior, atual, Set.of());
    }

    static List<EscalaPublicacaoDiffItem> comparar(
            List<EscalaPublicacaoSnapshot> anterior,
            List<EscalaPublicacaoSnapshot> atual,
            Set<UUID> celebracoesCanceladas) {
        List<EscalaPublicacaoDiffItem> diffs = new ArrayList<>();
        Map<UUID, EscalaPublicacaoSnapshot> anteriores = indexar(anterior);
        Map<UUID, EscalaPublicacaoSnapshot> atuais = indexar(atual);
        Set<UUID> canceladas = celebracoesCanceladas == null ? Set.of() : celebracoesCanceladas;

        for (EscalaPublicacaoSnapshot novo : atuais.values()) {
            EscalaPublicacaoSnapshot velho = anteriores.get(novo.celebracaoCodigo());
            if (velho == null) {
                diffs.addAll(diffsDeInclusao(novo));
                continue;
            }
            diffs.addAll(diffsDeCelebracao(velho, novo));
        }
        for (EscalaPublicacaoSnapshot velho : anteriores.values()) {
            if (atuais.containsKey(velho.celebracaoCodigo())) {
                continue;
            }
            boolean cancelada = canceladas.contains(velho.celebracaoCodigo());
            for (EscalaPublicacaoSnapshotParticipacao p : velho.participacoes()) {
                diffs.add(new EscalaPublicacaoDiffItem(
                        velho.celebracaoCodigo(),
                        velho.titulo(),
                        cancelada
                                ? EscalaPublicacaoAlteracaoTipo.CELEBRACAO_CANCELADA
                                : EscalaPublicacaoAlteracaoTipo.MUSICO_REMOVIDO,
                        cancelada
                                ? p.musicoNome() + " — a celebração " + velho.titulo()
                                + " de " + DATA.format(velho.data()) + " foi cancelada."
                                : p.musicoNome() + " foi removido(a) da escala de " + velho.titulo() + "."));
            }
        }
        return List.copyOf(diffs);
    }

    private static List<EscalaPublicacaoDiffItem> diffsDeInclusao(EscalaPublicacaoSnapshot novo) {
        List<EscalaPublicacaoDiffItem> diffs = new ArrayList<>();
        for (EscalaPublicacaoSnapshotParticipacao p : novo.participacoes()) {
            diffs.add(new EscalaPublicacaoDiffItem(
                    novo.celebracaoCodigo(),
                    novo.titulo(),
                    EscalaPublicacaoAlteracaoTipo.MUSICO_ADICIONADO,
                    p.musicoNome() + " foi adicionado(a) em " + novo.titulo()
                            + " (" + p.instrumentoNome() + ")."));
        }
        if (!novo.repertorio().isEmpty()) {
            diffs.add(new EscalaPublicacaoDiffItem(
                    novo.celebracaoCodigo(),
                    novo.titulo(),
                    EscalaPublicacaoAlteracaoTipo.REPERTORIO_ALTERADO,
                    "O repertório de " + novo.titulo() + " foi incluído."));
        }
        return diffs;
    }

    private static List<EscalaPublicacaoDiffItem> diffsDeCelebracao(
            EscalaPublicacaoSnapshot velho,
            EscalaPublicacaoSnapshot novo) {
        List<EscalaPublicacaoDiffItem> diffs = new ArrayList<>();
        if (!Objects.equals(velho.data(), novo.data())
                || !Objects.equals(velho.horaInicio(), novo.horaInicio())
                || !Objects.equals(velho.horaFim(), novo.horaFim())) {
            diffs.add(new EscalaPublicacaoDiffItem(
                    novo.celebracaoCodigo(),
                    novo.titulo(),
                    EscalaPublicacaoAlteracaoTipo.HORARIO_ALTERADO,
                    "Horário de " + novo.titulo() + " alterado de "
                            + formatarHorario(velho) + " para " + formatarHorario(novo) + "."));
        }

        Map<UUID, List<EscalaPublicacaoSnapshotParticipacao>> antigosPorMusico = agruparPorMusico(velho.participacoes());
        Map<UUID, List<EscalaPublicacaoSnapshotParticipacao>> novosPorMusico = agruparPorMusico(novo.participacoes());

        for (UUID musicoCodigo : novosPorMusico.keySet()) {
            List<EscalaPublicacaoSnapshotParticipacao> novos = novosPorMusico.get(musicoCodigo);
            List<EscalaPublicacaoSnapshotParticipacao> antigos = antigosPorMusico.get(musicoCodigo);
            String nome = novos.getFirst().musicoNome();
            if (antigos == null) {
                diffs.add(new EscalaPublicacaoDiffItem(
                        novo.celebracaoCodigo(),
                        novo.titulo(),
                        EscalaPublicacaoAlteracaoTipo.MUSICO_ADICIONADO,
                        nome + " foi adicionado(a) em " + novo.titulo()
                                + " (" + nomesFuncoes(novos) + ")."));
            } else if (!mesmoConjuntoFuncoes(antigos, novos)) {
                diffs.add(new EscalaPublicacaoDiffItem(
                        novo.celebracaoCodigo(),
                        novo.titulo(),
                        EscalaPublicacaoAlteracaoTipo.FUNCAO_ALTERADA,
                        "Função de " + nome + " em " + novo.titulo()
                                + " alterada de " + nomesFuncoes(antigos)
                                + " para " + nomesFuncoes(novos) + "."));
            }
        }
        for (UUID musicoCodigo : antigosPorMusico.keySet()) {
            if (!novosPorMusico.containsKey(musicoCodigo)) {
                EscalaPublicacaoSnapshotParticipacao p = antigosPorMusico.get(musicoCodigo).getFirst();
                diffs.add(new EscalaPublicacaoDiffItem(
                        novo.celebracaoCodigo(),
                        novo.titulo(),
                        EscalaPublicacaoAlteracaoTipo.MUSICO_REMOVIDO,
                        p.musicoNome() + " foi removido(a) da escala de " + novo.titulo() + "."));
            }
        }

        if (!mesmoRepertorio(velho.repertorio(), novo.repertorio())) {
            diffs.add(new EscalaPublicacaoDiffItem(
                    novo.celebracaoCodigo(),
                    novo.titulo(),
                    EscalaPublicacaoAlteracaoTipo.REPERTORIO_ALTERADO,
                    "O repertório de " + novo.titulo() + " foi alterado."));
        }
        return diffs;
    }

    private static Map<UUID, EscalaPublicacaoSnapshot> indexar(List<EscalaPublicacaoSnapshot> itens) {
        Map<UUID, EscalaPublicacaoSnapshot> mapa = new LinkedHashMap<>();
        if (itens == null) {
            return mapa;
        }
        for (EscalaPublicacaoSnapshot item : itens) {
            mapa.put(item.celebracaoCodigo(), item);
        }
        return mapa;
    }

    private static Map<UUID, List<EscalaPublicacaoSnapshotParticipacao>> agruparPorMusico(
            List<EscalaPublicacaoSnapshotParticipacao> participacoes) {
        Map<UUID, List<EscalaPublicacaoSnapshotParticipacao>> mapa = new LinkedHashMap<>();
        for (EscalaPublicacaoSnapshotParticipacao p : participacoes) {
            mapa.computeIfAbsent(p.musicoCodigo(), key -> new ArrayList<>()).add(p);
        }
        return mapa;
    }

    private static boolean mesmoConjuntoFuncoes(
            List<EscalaPublicacaoSnapshotParticipacao> a,
            List<EscalaPublicacaoSnapshotParticipacao> b) {
        Set<UUID> funcoesA = a.stream().map(EscalaPublicacaoSnapshotParticipacao::instrumentoCodigo).collect(Collectors.toSet());
        Set<UUID> funcoesB = b.stream().map(EscalaPublicacaoSnapshotParticipacao::instrumentoCodigo).collect(Collectors.toSet());
        return funcoesA.equals(funcoesB);
    }

    private static String nomesFuncoes(List<EscalaPublicacaoSnapshotParticipacao> participacoes) {
        return participacoes.stream()
                .map(EscalaPublicacaoSnapshotParticipacao::instrumentoNome)
                .distinct()
                .collect(Collectors.joining(", "));
    }

    private static boolean mesmoRepertorio(
            List<EscalaPublicacaoSnapshotRepertorio> a,
            List<EscalaPublicacaoSnapshotRepertorio> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            EscalaPublicacaoSnapshotRepertorio left = a.get(i);
            EscalaPublicacaoSnapshotRepertorio right = b.get(i);
            if (!Objects.equals(left.musicaCodigo(), right.musicaCodigo())
                    || !Objects.equals(left.momentoLiturgico(), right.momentoLiturgico())
                    || !Objects.equals(left.ordem(), right.ordem())
                    || !Objects.equals(blankToNull(left.tom()), blankToNull(right.tom()))) {
                return false;
            }
        }
        return true;
    }

    private static String formatarHorario(EscalaPublicacaoSnapshot snap) {
        StringBuilder sb = new StringBuilder();
        if (snap.data() != null) {
            sb.append(DATA.format(snap.data()));
        }
        if (snap.horaInicio() != null) {
            if (!sb.isEmpty()) {
                sb.append(" às ");
            }
            sb.append(HORA.format(snap.horaInicio()));
        }
        if (snap.horaFim() != null) {
            sb.append("–").append(HORA.format(snap.horaFim()));
        }
        return sb.isEmpty() ? "—" : sb.toString();
    }

    private static String blankToNull(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
