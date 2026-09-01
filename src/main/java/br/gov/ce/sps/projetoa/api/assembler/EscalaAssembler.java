package br.gov.ce.sps.projetoa.api.assembler;

import br.gov.ce.sps.projetoa.api.dto.EscalaMensalItemModel;
import br.gov.ce.sps.projetoa.api.dto.EscalaModel;
import br.gov.ce.sps.projetoa.api.dto.EscalaMusicoModel;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.model.EscalaMusico;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Component
public class EscalaAssembler {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    public EscalaModel toModel(Escala escala, List<EscalaMusico> ativas, List<String> alertas) {
        EscalaModel model = new EscalaModel();
        if (escala != null) {
            model.setCodigo(escala.getCodigo());
            model.setStatus(escala.getStatus());
        }
        Celebracao celebracao = escala != null ? escala.getCelebracao() : null;
        if (celebracao != null) {
            preencherCelebracao(model, celebracao);
        }
        model.setParticipacoes(ativas.stream().map(this::toParticipacao).toList());
        model.setAlertas(alertas == null ? List.of() : List.copyOf(alertas));
        return model;
    }

    public EscalaMensalItemModel toMensalItem(
            Celebracao celebracao,
            Escala escala,
            List<EscalaMusico> ativas,
            List<String> alertas) {
        EscalaMensalItemModel item = new EscalaMensalItemModel();
        item.setCelebracaoCodigo(celebracao.getCodigo());
        item.setTitulo(celebracao.getTitulo());
        item.setData(celebracao.getData());
        item.setHoraInicio(celebracao.getHoraInicio());
        item.setHoraFim(celebracao.getHoraFim());
        item.setLocalNome(celebracao.getLocal() != null ? celebracao.getLocal().getNome() : null);
        item.setDiaSemana(diaSemana(celebracao));
        item.setCelebracaoStatus(celebracao.getStatus());
        if (escala != null) {
            item.setEscalaCodigo(escala.getCodigo());
            item.setEscalaStatus(escala.getStatus());
        }
        List<EscalaMusicoModel> participacoes = ativas.stream().map(this::toParticipacao).toList();
        item.setParticipacoes(participacoes);
        item.setQuantidadeMusicos(participacoes.size());
        item.setAlertas(alertas == null ? List.of() : List.copyOf(alertas));
        return item;
    }

    public EscalaMusicoModel toParticipacao(EscalaMusico em) {
        EscalaMusicoModel model = new EscalaMusicoModel();
        model.setCodigo(em.getCodigo());
        Musico musico = em.getMusico();
        if (musico != null) {
            model.setMusicoCodigo(musico.getCodigo());
            model.setMusicoNome(nomeExibicao(musico));
            model.setInstrumentoDoMusico(possuiInstrumento(musico, em.getInstrumento()));
        }
        Instrumento instrumento = em.getInstrumento();
        if (instrumento != null) {
            model.setInstrumentoCodigo(instrumento.getCodigo());
            model.setInstrumentoNome(instrumento.getNome());
        }
        model.setObservacao(em.getObservacao());
        model.setStatusConfirmacao(em.getStatusConfirmacao());
        model.setOrdem(em.getOrdem());
        return model;
    }

    private void preencherCelebracao(EscalaModel model, Celebracao celebracao) {
        model.setCelebracaoCodigo(celebracao.getCodigo());
        model.setCelebracaoTitulo(celebracao.getTitulo());
        model.setData(celebracao.getData());
        model.setHoraInicio(celebracao.getHoraInicio());
        model.setHoraFim(celebracao.getHoraFim());
        model.setLocalNome(celebracao.getLocal() != null ? celebracao.getLocal().getNome() : null);
        model.setDiaSemana(diaSemana(celebracao));
        model.setCelebracaoStatus(celebracao.getStatus());
    }

    public static String diaSemana(Celebracao celebracao) {
        if (celebracao == null || celebracao.getData() == null) {
            return null;
        }
        String nome = celebracao.getData().getDayOfWeek().getDisplayName(TextStyle.FULL, PT_BR);
        if (!StringUtils.hasText(nome)) {
            return null;
        }
        return nome.substring(0, 1).toUpperCase(PT_BR) + nome.substring(1);
    }

    public static String nomeExibicao(Musico musico) {
        if (musico == null) {
            return null;
        }
        if (StringUtils.hasText(musico.getNomeArtistico())) {
            return musico.getNomeArtistico();
        }
        return musico.getNome();
    }

    public static boolean possuiInstrumento(Musico musico, Instrumento instrumento) {
        if (musico == null || instrumento == null || musico.getInstrumentos() == null) {
            return false;
        }
        return musico.getInstrumentos().stream()
                .anyMatch(item -> item.getId() != null && item.getId().equals(instrumento.getId()));
    }
}
