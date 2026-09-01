package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

final class WhatsAppAlteracaoResolver {

    private static final DateTimeFormatter DATA_CURTA = DateTimeFormatter.ofPattern("dd/MM");

    private WhatsAppAlteracaoResolver() {
    }

    static List<WhatsAppDestinatarioNotificacao> resolver(
            UUID publicacaoCodigo,
            List<WhatsAppCelebracaoResumo> atuais,
            List<WhatsAppCelebracaoResumo> anteriores,
            Set<UUID> celebracoesCanceladas) {
        Map<UUID, WhatsAppCelebracaoResumo> mapaAtual = indexar(atuais);
        Map<UUID, WhatsAppCelebracaoResumo> mapaAnterior = indexar(anteriores);
        Set<UUID> canceladas = celebracoesCanceladas == null ? Set.of() : celebracoesCanceladas;
        List<WhatsAppDestinatarioNotificacao> destinos = new ArrayList<>();

        for (WhatsAppCelebracaoResumo atual : mapaAtual.values()) {
            WhatsAppCelebracaoResumo anterior = mapaAnterior.get(atual.celebracaoCodigo());
            if (anterior == null) {
                for (UUID musicoCodigo : musicos(atual)) {
                    destinos.add(destino(
                            publicacaoCodigo,
                            musicoCodigo,
                            nome(atual, musicoCodigo),
                            WhatsAppTipoMensagem.NOVA_ESCALA,
                            atual,
                            musicoCodigo,
                            "ADICIONADO à celebração de " + dataCurta(atual)));
                }
                continue;
            }

            Set<UUID> musicosAtuais = musicos(atual);
            Set<UUID> musicosAnteriores = musicos(anterior);
            boolean horarioAlterado = horarioAlterado(anterior, atual);
            boolean repertorioAlterado = !mesmoRepertorio(anterior.repertorio(), atual.repertorio());

            for (UUID musicoCodigo : musicosAtuais) {
                if (!musicosAnteriores.contains(musicoCodigo)) {
                    destinos.add(destino(
                            publicacaoCodigo,
                            musicoCodigo,
                            nome(atual, musicoCodigo),
                            WhatsAppTipoMensagem.NOVA_ESCALA,
                            atual,
                            musicoCodigo,
                            "ADICIONADO à celebração de " + dataCurta(atual)));
                    continue;
                }
                boolean funcaoAlterada = !mesmoConjuntoFuncoes(anterior, atual, musicoCodigo);
                if (funcaoAlterada || horarioAlterado) {
                    destinos.add(destino(
                            publicacaoCodigo,
                            musicoCodigo,
                            nome(atual, musicoCodigo),
                            WhatsAppTipoMensagem.ALTERACAO_ESCALA,
                            atual,
                            musicoCodigo,
                            resumoAlteracao(funcaoAlterada, horarioAlterado, atual)));
                }
                if (repertorioAlterado) {
                    destinos.add(destino(
                            publicacaoCodigo,
                            musicoCodigo,
                            nome(atual, musicoCodigo),
                            WhatsAppTipoMensagem.ALTERACAO_REPERTORIO,
                            atual,
                            musicoCodigo,
                            "REPERTÓRIO ALTERADO em " + dataCurta(atual)));
                }
            }
            for (UUID musicoCodigo : musicosAnteriores) {
                if (!musicosAtuais.contains(musicoCodigo)) {
                    destinos.add(destino(
                            publicacaoCodigo,
                            musicoCodigo,
                            nome(anterior, musicoCodigo),
                            WhatsAppTipoMensagem.REMOCAO_ESCALA,
                            anterior,
                            musicoCodigo,
                            "REMOVIDO da celebração de " + dataCurta(anterior)));
                }
            }
        }

        for (WhatsAppCelebracaoResumo anterior : mapaAnterior.values()) {
            if (mapaAtual.containsKey(anterior.celebracaoCodigo())) {
                continue;
            }
            boolean cancelada = canceladas.contains(anterior.celebracaoCodigo());
            WhatsAppTipoMensagem tipo = cancelada
                    ? WhatsAppTipoMensagem.CELEBRACAO_CANCELADA
                    : WhatsAppTipoMensagem.REMOCAO_ESCALA;
            String resumo = cancelada
                    ? "CELEBRAÇÃO CANCELADA em " + dataCurta(anterior)
                    : "REMOVIDO da celebração de " + dataCurta(anterior);
            for (UUID musicoCodigo : musicos(anterior)) {
                destinos.add(destino(
                        publicacaoCodigo,
                        musicoCodigo,
                        nome(anterior, musicoCodigo),
                        tipo,
                        anterior,
                        musicoCodigo,
                        resumo));
            }
        }

        destinos.sort(Comparator
                .comparing(WhatsAppDestinatarioNotificacao::musicoNome, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(d -> d.blocos().isEmpty() || d.blocos().getFirst().data() == null
                        ? null
                        : d.blocos().getFirst().data(), Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(d -> d.tipo().name()));
        return List.copyOf(destinos);
    }

    private static WhatsAppDestinatarioNotificacao destino(
            UUID publicacaoCodigo,
            UUID musicoCodigo,
            String musicoNome,
            WhatsAppTipoMensagem tipo,
            WhatsAppCelebracaoResumo celebracao,
            UUID musicoBloco,
            String resumo) {
        return new WhatsAppDestinatarioNotificacao(
                publicacaoCodigo,
                musicoCodigo,
                musicoNome,
                tipo,
                List.of(bloco(celebracao, musicoBloco)),
                celebracao.celebracaoCodigo(),
                resumo);
    }

    private static WhatsAppEscalaMensalBloco bloco(WhatsAppCelebracaoResumo cel, UUID musicoCodigo) {
        List<String> funcoes = cel.participacoes().stream()
                .filter(p -> musicoCodigo.equals(p.musicoCodigo()))
                .map(WhatsAppParticipacaoResumo::instrumentoNome)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
        return new WhatsAppEscalaMensalBloco(
                cel.data(),
                cel.horaInicio(),
                cel.titulo(),
                cel.localNome(),
                funcoes,
                cel.repertorio());
    }

    private static Map<UUID, WhatsAppCelebracaoResumo> indexar(List<WhatsAppCelebracaoResumo> itens) {
        Map<UUID, WhatsAppCelebracaoResumo> mapa = new LinkedHashMap<>();
        if (itens == null) {
            return mapa;
        }
        for (WhatsAppCelebracaoResumo item : itens) {
            if (item != null && item.celebracaoCodigo() != null) {
                mapa.put(item.celebracaoCodigo(), item);
            }
        }
        return mapa;
    }

    private static Set<UUID> musicos(WhatsAppCelebracaoResumo cel) {
        Set<UUID> ids = new LinkedHashSet<>();
        for (WhatsAppParticipacaoResumo p : cel.participacoes()) {
            if (p.musicoCodigo() != null) {
                ids.add(p.musicoCodigo());
            }
        }
        return ids;
    }

    private static String nome(WhatsAppCelebracaoResumo cel, UUID musicoCodigo) {
        return cel.participacoes().stream()
                .filter(p -> musicoCodigo.equals(p.musicoCodigo()))
                .map(WhatsAppParticipacaoResumo::musicoNome)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("músico");
    }

    private static boolean horarioAlterado(WhatsAppCelebracaoResumo anterior, WhatsAppCelebracaoResumo atual) {
        return !Objects.equals(anterior.data(), atual.data())
                || !Objects.equals(anterior.horaInicio(), atual.horaInicio())
                || !Objects.equals(anterior.horaFim(), atual.horaFim());
    }

    private static boolean mesmoConjuntoFuncoes(
            WhatsAppCelebracaoResumo anterior,
            WhatsAppCelebracaoResumo atual,
            UUID musicoCodigo) {
        Set<UUID> antigas = anterior.participacoes().stream()
                .filter(p -> musicoCodigo.equals(p.musicoCodigo()))
                .map(WhatsAppParticipacaoResumo::instrumentoCodigo)
                .collect(Collectors.toSet());
        Set<UUID> novas = atual.participacoes().stream()
                .filter(p -> musicoCodigo.equals(p.musicoCodigo()))
                .map(WhatsAppParticipacaoResumo::instrumentoCodigo)
                .collect(Collectors.toSet());
        return antigas.equals(novas);
    }

    private static boolean mesmoRepertorio(
            List<WhatsAppRepertorioItemResumo> a,
            List<WhatsAppRepertorioItemResumo> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            WhatsAppRepertorioItemResumo left = a.get(i);
            WhatsAppRepertorioItemResumo right = b.get(i);
            if (!Objects.equals(left.momentoLiturgico(), right.momentoLiturgico())
                    || !Objects.equals(blank(left.musicaTitulo()), blank(right.musicaTitulo()))
                    || !Objects.equals(blank(left.tom()), blank(right.tom()))
                    || !Objects.equals(left.ordem(), right.ordem())) {
                return false;
            }
        }
        return true;
    }

    private static String resumoAlteracao(boolean funcao, boolean horario, WhatsAppCelebracaoResumo cel) {
        String data = dataCurta(cel);
        if (funcao && horario) {
            return "FUNÇÃO E HORÁRIO ALTERADOS na celebração de " + data;
        }
        if (funcao) {
            return "FUNÇÃO ALTERADA na celebração de " + data;
        }
        return "HORÁRIO ALTERADO na celebração de " + data;
    }

    private static String dataCurta(WhatsAppCelebracaoResumo cel) {
        if (cel.data() == null) {
            return "—";
        }
        return DATA_CURTA.format(cel.data());
    }

    private static String blank(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
