package br.gov.ce.sps.projetoa.api.assembler;

import br.gov.ce.sps.projetoa.api.dto.RepertorioItemModel;
import br.gov.ce.sps.projetoa.api.dto.RepertorioMensalItemModel;
import br.gov.ce.sps.projetoa.api.dto.RepertorioModel;
import br.gov.ce.sps.projetoa.domain.model.CategoriasLiturgicas;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class RepertorioAssembler {

    public RepertorioModel toModel(Repertorio repertorio, List<RepertorioItem> itensAtivos) {
        RepertorioModel model = new RepertorioModel();
        if (repertorio != null) {
            model.setCodigo(repertorio.getCodigo());
            model.setStatus(repertorio.getStatus());
            model.setObservacao(repertorio.getObservacao());
            preencherCelebracao(model, repertorio.getCelebracao());
        }
        model.setItens(ordenar(itensAtivos).stream().map(this::toItem).toList());
        return model;
    }

    public RepertorioModel toModelSemRepertorio(Celebracao celebracao) {
        RepertorioModel model = new RepertorioModel();
        preencherCelebracao(model, celebracao);
        return model;
    }

    public RepertorioMensalItemModel toMensalItem(
            Celebracao celebracao,
            Repertorio repertorio,
            List<RepertorioItem> itensAtivos) {
        RepertorioMensalItemModel item = new RepertorioMensalItemModel();
        item.setCelebracaoCodigo(celebracao.getCodigo());
        item.setTitulo(celebracao.getTitulo());
        item.setData(celebracao.getData());
        item.setHoraInicio(celebracao.getHoraInicio());
        item.setHoraFim(celebracao.getHoraFim());
        item.setLocalNome(celebracao.getLocal() != null ? celebracao.getLocal().getNome() : null);
        item.setDiaSemana(EscalaAssembler.diaSemana(celebracao));
        item.setCelebracaoStatus(celebracao.getStatus());
        if (repertorio != null) {
            item.setRepertorioCodigo(repertorio.getCodigo());
            item.setRepertorioStatus(repertorio.getStatus());
            item.setObservacao(repertorio.getObservacao());
        }
        List<RepertorioItemModel> itens = ordenar(itensAtivos).stream().map(this::toItem).toList();
        item.setItens(itens);
        item.setQuantidadeItens(itens.size());
        return item;
    }

    public RepertorioItemModel toItem(RepertorioItem item) {
        RepertorioItemModel model = new RepertorioItemModel();
        model.setCodigo(item.getCodigo());
        Musica musica = item.getMusica();
        if (musica != null) {
            model.setMusicaCodigo(musica.getCodigo());
            model.setMusicaTitulo(musica.getTitulo());
            model.setMusicaAutor(musica.getAutor());
            model.setTomPadraoMusica(musica.getTomPadrao());
        }
        model.setMomentoLiturgico(item.getMomentoLiturgico());
        model.setMomentoLiturgicoRotulo(CategoriasLiturgicas.rotulo(item.getMomentoLiturgico()));
        model.setOrdem(item.getOrdem());
        model.setTom(item.getTom());
        model.setObservacao(item.getObservacao());
        return model;
    }

    static List<RepertorioItem> ordenar(List<RepertorioItem> itens) {
        return itens.stream()
                .sorted(Comparator
                        .comparingInt((RepertorioItem ri) -> indiceMomento(ri.getMomentoLiturgico()))
                        .thenComparing(ri -> ri.getOrdem() == null ? 0 : ri.getOrdem())
                        .thenComparing(ri -> ri.getId() == null ? 0L : ri.getId()))
                .toList();
    }

    private static int indiceMomento(String momento) {
        if (momento == null) {
            return CategoriasLiturgicas.SUGERIDAS.size();
        }
        int indice = CategoriasLiturgicas.SUGERIDAS.indexOf(momento);
        return indice < 0 ? CategoriasLiturgicas.SUGERIDAS.size() : indice;
    }

    private void preencherCelebracao(RepertorioModel model, Celebracao celebracao) {
        if (celebracao == null) {
            return;
        }
        model.setCelebracaoCodigo(celebracao.getCodigo());
        model.setCelebracaoTitulo(celebracao.getTitulo());
        model.setData(celebracao.getData());
        model.setHoraInicio(celebracao.getHoraInicio());
        model.setHoraFim(celebracao.getHoraFim());
        model.setLocalNome(celebracao.getLocal() != null ? celebracao.getLocal().getNome() : null);
        model.setDiaSemana(EscalaAssembler.diaSemana(celebracao));
        model.setCelebracaoStatus(celebracao.getStatus());
    }
}
