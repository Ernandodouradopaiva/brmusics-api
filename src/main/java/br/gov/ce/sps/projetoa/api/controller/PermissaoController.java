package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.dto.PermissaoArvoreNodeModel;
import br.gov.ce.sps.projetoa.api.dto.PermissaoModelBasico;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.model.Permissao;
import br.gov.ce.sps.projetoa.domain.service.permissao.GetPermissaoService;
import br.gov.ce.sps.projetoa.domain.service.permissao.ListPermissaoArvoreService;
import br.gov.ce.sps.projetoa.domain.service.permissao.ListPermissaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(value = "/permissoes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PermissaoController {

    private final GenericAssembler genericAssembler;
    private final ListPermissaoService listPermissaoService;
    private final GetPermissaoService getPermissaoService;
    private final ListPermissaoArvoreService listPermissaoArvoreService;

    @PreAuthorize("hasAnyAuthority('" + Permissoes.Grupo.GERENCIAR_PERMISSOES + "', '" + Permissoes.Grupo.EDITAR + "', '" + Permissoes.Grupo.CRIAR + "')")
    @GetMapping("/arvore")
    public List<PermissaoArvoreNodeModel> arvore() {
        return listPermissaoArvoreService.montarArvore();
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Permissao.LISTAR + "')")
    @GetMapping
    public Page<PermissaoModelBasico> listarAll(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 5, sort = "ordem") Pageable pageable) {
        Page<Permissao> permissaoPage = listPermissaoService.listar(busca, pageable);
        List<PermissaoModelBasico> dto = Objects.requireNonNull(
                genericAssembler.toCollectionModel(permissaoPage.getContent(), PermissaoModelBasico.class));
        return new PageImpl<>(dto, permissaoPage.getPageable(), permissaoPage.getTotalElements());
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Permissao.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public PermissaoModelBasico buscar(@PathVariable UUID codigo) {
        Permissao permissao = getPermissaoService.findByCode(codigo);
        return genericAssembler.toModel(permissao, PermissaoModelBasico.class);
    }
}
