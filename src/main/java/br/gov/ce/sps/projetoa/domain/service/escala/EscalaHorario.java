package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.domain.model.Celebracao;

import java.time.LocalTime;

final class EscalaHorario {

    private static final int DURACAO_PADRAO_HORAS = 2;

    private EscalaHorario() {
    }

    static boolean horariosConflitam(Celebracao a, Celebracao b) {
        if (a == null || b == null || a.getHoraInicio() == null || b.getHoraInicio() == null) {
            return false;
        }
        LocalTime inicioA = a.getHoraInicio();
        LocalTime fimA = fimEfetivo(a);
        LocalTime inicioB = b.getHoraInicio();
        LocalTime fimB = fimEfetivo(b);
        return inicioA.isBefore(fimB) && inicioB.isBefore(fimA);
    }

    static LocalTime fimEfetivo(Celebracao celebracao) {
        if (celebracao.getHoraFim() != null && celebracao.getHoraFim().isAfter(celebracao.getHoraInicio())) {
            return celebracao.getHoraFim();
        }
        return celebracao.getHoraInicio().plusHours(DURACAO_PADRAO_HORAS);
    }
}
