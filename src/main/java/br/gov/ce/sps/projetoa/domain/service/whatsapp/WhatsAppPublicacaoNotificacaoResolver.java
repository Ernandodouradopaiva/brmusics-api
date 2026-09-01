package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

final class WhatsAppPublicacaoNotificacaoResolver {

    private WhatsAppPublicacaoNotificacaoResolver() {
    }

    static List<WhatsAppDestinatarioNotificacao> resolver(
            UUID publicacaoCodigo,
            List<WhatsAppCelebracaoResumo> atuais) {
        return agruparPorMusico(publicacaoCodigo, WhatsAppTipoMensagem.PUBLICACAO_ESCALA, atuais);
    }

    static List<WhatsAppDestinatarioNotificacao> lembretes(
            UUID publicacaoCodigo,
            List<WhatsAppCelebracaoResumo> atuais) {
        return agruparPorMusico(publicacaoCodigo, WhatsAppTipoMensagem.LEMBRETE, atuais);
    }

    private static List<WhatsAppDestinatarioNotificacao> agruparPorMusico(
            UUID publicacaoCodigo,
            WhatsAppTipoMensagem tipo,
            List<WhatsAppCelebracaoResumo> atuais) {
        if (atuais == null || atuais.isEmpty()) {
            return List.of();
        }
        List<WhatsAppCelebracaoResumo> ordenadas = atuais.stream()
                .sorted(Comparator
                        .comparing(WhatsAppCelebracaoResumo::data, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(WhatsAppCelebracaoResumo::horaInicio, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        Map<UUID, String> nomes = new LinkedHashMap<>();
        Map<UUID, List<WhatsAppEscalaMensalBloco>> blocosPorMusico = new LinkedHashMap<>();
        for (WhatsAppCelebracaoResumo cel : ordenadas) {
            Map<UUID, List<String>> funcoesPorMusico = new LinkedHashMap<>();
            for (WhatsAppParticipacaoResumo p : cel.participacoes()) {
                if (p.musicoCodigo() == null) {
                    continue;
                }
                nomes.putIfAbsent(p.musicoCodigo(), p.musicoNome());
                funcoesPorMusico
                        .computeIfAbsent(p.musicoCodigo(), key -> new ArrayList<>())
                        .add(p.instrumentoNome());
            }
            for (Map.Entry<UUID, List<String>> entry : funcoesPorMusico.entrySet()) {
                List<String> funcoes = entry.getValue().stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .distinct()
                        .toList();
                blocosPorMusico
                        .computeIfAbsent(entry.getKey(), key -> new ArrayList<>())
                        .add(new WhatsAppEscalaMensalBloco(
                                cel.data(),
                                cel.horaInicio(),
                                cel.titulo(),
                                cel.localNome(),
                                funcoes,
                                cel.repertorio()));
            }
        }

        List<WhatsAppDestinatarioNotificacao> destinos = new ArrayList<>();
        for (Map.Entry<UUID, List<WhatsAppEscalaMensalBloco>> entry : blocosPorMusico.entrySet()) {
            destinos.add(new WhatsAppDestinatarioNotificacao(
                    publicacaoCodigo,
                    entry.getKey(),
                    nomes.get(entry.getKey()),
                    tipo,
                    entry.getValue(),
                    null,
                    ""));
        }
        return List.copyOf(destinos);
    }
}
