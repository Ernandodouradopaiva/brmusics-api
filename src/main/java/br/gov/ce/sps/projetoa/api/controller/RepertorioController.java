package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.dto.CategoriaLiturgicaModel;
import br.gov.ce.sps.projetoa.api.dto.RepertorioMensalItemModel;
import br.gov.ce.sps.projetoa.api.dto.RepertorioModel;
import br.gov.ce.sps.projetoa.api.input.RepertorioInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.model.CategoriasLiturgicas;
import br.gov.ce.sps.projetoa.domain.service.repertorio.AtualizaRepertorioService;
import br.gov.ce.sps.projetoa.domain.service.repertorio.CadastroRepertorioService;
import br.gov.ce.sps.projetoa.domain.service.repertorio.DeletaRepertorioService;
import br.gov.ce.sps.projetoa.domain.service.repertorio.GetRepertorioService;
import br.gov.ce.sps.projetoa.domain.service.repertorio.ListRepertorioMensalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/repertorios")
@RequiredArgsConstructor
public class RepertorioController {

    private final ListRepertorioMensalService listRepertorioMensalService;
    private final GetRepertorioService getRepertorioService;
    private final CadastroRepertorioService cadastroRepertorioService;
    private final AtualizaRepertorioService atualizaRepertorioService;
    private final DeletaRepertorioService deletaRepertorioService;

    @PreAuthorize("hasAuthority('" + Permissoes.Repertorio.LISTAR + "')")
    @GetMapping("/mensal")
    public List<RepertorioMensalItemModel> listarMensal(@RequestParam int ano, @RequestParam int mes) {
        return listRepertorioMensalService.listar(ano, mes);
    }

    @PreAuthorize("hasAnyAuthority('"
            + Permissoes.Repertorio.LISTAR + "', '"
            + Permissoes.Repertorio.VISUALIZAR + "', '"
            + Permissoes.Repertorio.CRIAR + "', '"
            + Permissoes.Repertorio.EDITAR + "')")
    @GetMapping("/momentos")
    public List<CategoriaLiturgicaModel> momentos() {
        return CategoriasLiturgicas.SUGERIDAS.stream().map(codigo -> {
            CategoriaLiturgicaModel item = new CategoriaLiturgicaModel();
            item.setCodigo(codigo);
            item.setRotulo(CategoriasLiturgicas.rotulo(codigo));
            return item;
        }).toList();
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Repertorio.VISUALIZAR + "')")
    @GetMapping("/por-celebracao/{celebracaoCodigo}")
    public RepertorioModel buscarPorCelebracao(@PathVariable UUID celebracaoCodigo) {
        return getRepertorioService.buscarPorCelebracao(celebracaoCodigo);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Repertorio.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public RepertorioModel buscar(@PathVariable UUID codigo) {
        return getRepertorioService.buscar(codigo);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Repertorio.CRIAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RepertorioModel adicionar(@Valid @RequestBody RepertorioInput input) {
        return cadastroRepertorioService.salvar(input);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Repertorio.EDITAR + "')")
    @PutMapping("/{codigo}")
    public RepertorioModel atualizar(@PathVariable UUID codigo, @Valid @RequestBody RepertorioInput input) {
        return atualizaRepertorioService.atualizar(codigo, input);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Repertorio.EXCLUIR + "')")
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID codigo) {
        deletaRepertorioService.deletar(codigo);
    }
}
