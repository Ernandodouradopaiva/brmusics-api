package br.gov.ce.sps.projetoa.api.assembler;

import br.gov.ce.sps.projetoa.api.dto.EscalaPublicacaoAlteracaoModel;
import br.gov.ce.sps.projetoa.api.dto.EscalaPublicacaoModel;
import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacao;
import br.gov.ce.sps.projetoa.domain.model.EscalaPublicacaoAlteracao;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import org.springframework.stereotype.Component;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Component
public class EscalaPublicacaoAssembler {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    public EscalaPublicacaoModel toModel(EscalaPublicacao publicacao) {
        EscalaPublicacaoModel model = new EscalaPublicacaoModel();
        model.setCodigo(publicacao.getCodigo());
        model.setAno(publicacao.getAno());
        model.setMes(publicacao.getMes());
        model.setVersao(publicacao.getVersao());
        model.setPublicadoEm(publicacao.getPublicadoEm());
        model.setPublicadoPorNome(publicacao.getPublicadoPorNome());
        Usuario usuario = publicacao.getPublicadoPor();
        if (usuario != null) {
            model.setPublicadoPorCodigo(usuario.getCodigo());
        }
        model.setQuantidadeCelebracoes(publicacao.getQuantidadeCelebracoes());
        model.setQuantidadeMusicos(publicacao.getQuantidadeMusicos());
        model.setQuantidadeEscalas(publicacao.getQuantidadeEscalas());
        model.setAlteracoes(publicacao.getAlteracoes() == null
                ? List.of()
                : publicacao.getAlteracoes().stream().map(this::toAlteracao).toList());
        return model;
    }

    public EscalaPublicacaoAlteracaoModel toAlteracao(EscalaPublicacaoAlteracao alteracao) {
        EscalaPublicacaoAlteracaoModel model = new EscalaPublicacaoAlteracaoModel();
        model.setCelebracaoCodigo(alteracao.getCelebracaoCodigo());
        model.setCelebracaoTitulo(alteracao.getCelebracaoTitulo());
        model.setTipo(alteracao.getTipo());
        model.setDescricao(alteracao.getDescricao());
        return model;
    }

    public static String competencia(int ano, int mes) {
        String nome = Month.of(mes).getDisplayName(TextStyle.FULL, PT_BR);
        if (nome == null || nome.isBlank()) {
            return mes + "/" + ano;
        }
        return nome.substring(0, 1).toUpperCase(PT_BR) + nome.substring(1) + "/" + ano;
    }
}
