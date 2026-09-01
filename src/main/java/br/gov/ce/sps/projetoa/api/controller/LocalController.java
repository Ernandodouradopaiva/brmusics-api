package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.dto.LocalModelBasico;
import br.gov.ce.sps.projetoa.api.input.AtivoInput;
import br.gov.ce.sps.projetoa.api.input.LocalInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.filter.LocalFilter;
import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.service.local.AtualizaLocalService;
import br.gov.ce.sps.projetoa.domain.service.local.CadastroLocalService;
import br.gov.ce.sps.projetoa.domain.service.local.DeletaLocalService;
import br.gov.ce.sps.projetoa.domain.service.local.GetLocalService;
import br.gov.ce.sps.projetoa.domain.service.local.ListLocalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/locais")
@RequiredArgsConstructor
public class LocalController {

    private final ListLocalService listLocalService;
    private final GetLocalService getLocalService;
    private final CadastroLocalService cadastroLocalService;
    private final AtualizaLocalService atualizaLocalService;
    private final DeletaLocalService deletaLocalService;
    private final GenericAssembler genericAssembler;

    @PreAuthorize("hasAnyAuthority('" + Permissoes.Local.LISTAR + "', '"
            + Permissoes.Celebracao.LISTAR + "', '"
            + Permissoes.Celebracao.CRIAR + "', '" + Permissoes.Celebracao.EDITAR + "')")
    @GetMapping
    public Page<LocalModelBasico> listar(
            LocalFilter filtro,
            @PageableDefault(size = 5, sort = "nome") Pageable pageable) {
        Page<Local> page = listLocalService.listar(filtro, pageable);
        List<LocalModelBasico> dto = Objects.requireNonNull(
                genericAssembler.toCollectionModel(page.getContent(), LocalModelBasico.class));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Local.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public LocalModelBasico buscar(@PathVariable UUID codigo) {
        return genericAssembler.toModel(getLocalService.findByCode(codigo), LocalModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Local.CRIAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocalModelBasico adicionar(@Valid @RequestBody LocalInput input) {
        return genericAssembler.toModel(cadastroLocalService.salvar(input), LocalModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Local.EDITAR + "')")
    @PutMapping("/{codigo}")
    public LocalModelBasico atualizar(@PathVariable UUID codigo, @Valid @RequestBody LocalInput input) {
        Local local = getLocalService.findByCode(codigo);
        return genericAssembler.toModel(atualizaLocalService.atualiza(local, input), LocalModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Local.EDITAR + "')")
    @PutMapping("/{codigo}/ativo")
    public LocalModelBasico atualizarPropriedadeAtivo(
            @PathVariable UUID codigo,
            @Valid @RequestBody AtivoInput input) {
        Local local = getLocalService.findByCode(codigo);
        return genericAssembler.toModel(atualizaLocalService.atualizarAtivo(local, input.ativo()), LocalModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Local.EXCLUIR + "')")
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID codigo) {
        deletaLocalService.deletar(codigo);
    }
}
