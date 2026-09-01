package br.gov.ce.sps.projetoa.domain.service.escala;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

record EscalaPublicacaoSnapshot(
        UUID celebracaoCodigo,
        String titulo,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim,
        String localNome,
        List<EscalaPublicacaoSnapshotParticipacao> participacoes,
        List<EscalaPublicacaoSnapshotRepertorio> repertorio) {

    EscalaPublicacaoSnapshot {
        participacoes = participacoes == null ? List.of() : List.copyOf(participacoes);
        repertorio = repertorio == null ? List.of() : List.copyOf(repertorio);
    }
}

record EscalaPublicacaoSnapshotParticipacao(
        UUID musicoCodigo,
        String musicoNome,
        UUID instrumentoCodigo,
        String instrumentoNome) {
}

record EscalaPublicacaoSnapshotRepertorio(
        UUID musicaCodigo,
        String musicaTitulo,
        String momentoLiturgico,
        Integer ordem,
        String tom) {
}

record EscalaPublicacaoDiffItem(
        UUID celebracaoCodigo,
        String celebracaoTitulo,
        br.gov.ce.sps.projetoa.domain.model.enums.EscalaPublicacaoAlteracaoTipo tipo,
        String descricao) {
}
