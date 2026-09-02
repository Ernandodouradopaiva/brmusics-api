package br.gov.ce.sps.projetoa.domain.service.relatorio;

import br.gov.ce.sps.projetoa.domain.filter.UsuarioFilter;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.service.usuario.ListUsuarioService;
import br.gov.ce.sps.projetoa.infrastructure.report.RelatorioUsuariosPdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioUsuariosService {

    private final ListUsuarioService listUsuarioService;
    private final RelatorioUsuariosPdfGenerator pdfGenerator;

    public byte[] gerarPdf(UsuarioFilter filtro) {
        UsuarioFilter f = filtro != null ? filtro : new UsuarioFilter();
        List<Usuario> usuarios = listUsuarioService.listarTodos(f);
        long ativos = usuarios.stream().filter(u -> Boolean.TRUE.equals(u.getAtivo())).count();
        long inativos = usuarios.size() - ativos;
        List<RelatorioUsuariosPdfGenerator.Linha> linhas = usuarios.stream()
                .map(u -> new RelatorioUsuariosPdfGenerator.Linha(
                        u.getCpf(),
                        u.getNome(),
                        u.getCargo(),
                        Boolean.TRUE.equals(u.getAtivo()) ? "Ativo" : "Inativo"))
                .toList();
        return pdfGenerator.gerar(new RelatorioUsuariosPdfGenerator.Resumo(
                "Relatório de Usuários",
                "Listagem cadastral — BRMusics",
                usuarios.size(),
                ativos,
                inativos,
                linhas));
    }
}
