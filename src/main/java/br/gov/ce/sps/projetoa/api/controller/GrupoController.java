package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.disassembler.GenericDisassembler;
import br.gov.ce.sps.projetoa.api.dto.GrupoModelBasico;
import br.gov.ce.sps.projetoa.api.input.GrupoInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.model.Grupo;
import br.gov.ce.sps.projetoa.domain.service.grupo.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final ListGrupoService listGrupoService;
    private final GetGrupoService getGrupoService;
    private final CadastroGrupoService1 cadastroGrupoService;
    private final AtualizaGrupoService atualizaGrupoService;
    private final DeletaGrupoService deletaGrupoService;
    private final SyncGrupoPermissoesService syncGrupoPermissoesService;

    private final GenericAssembler genericAssembler;
    private final GenericDisassembler genericDisassembler;

    @PreAuthorize("hasAuthority('" + Permissoes.Grupo.LISTAR + "')")
    @GetMapping
    public Page<GrupoModelBasico> listar(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 5, sort = "nome") Pageable pageable) {
        Page<Grupo> grupoPage = listGrupoService.listar(busca, pageable);
        List<GrupoModelBasico> grupoDTO = Objects.requireNonNull(
                genericAssembler.toCollectionModel(grupoPage.getContent(), GrupoModelBasico.class));
        return new PageImpl<>(grupoDTO, grupoPage.getPageable(), grupoPage.getTotalElements());
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Grupo.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public GrupoModelBasico buscar(@PathVariable UUID codigo) {
        Grupo grupo = getGrupoService.findByCode(codigo);
        return genericAssembler.toModel(grupo, GrupoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Grupo.CRIAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GrupoModelBasico adicionar(@RequestBody @Valid GrupoInput grupoInput) {
        Grupo grupo = genericDisassembler.toDomainObject(grupoInput, Grupo.class);
        Grupo grupoSalvo = cadastroGrupoService.salvar(grupo);
        if (grupoInput.getPermissoesCodigos() != null) {
            syncGrupoPermissoesService.syncPermissoes(grupoSalvo.getCodigo(), grupoInput.getPermissoesCodigos());
            grupoSalvo = getGrupoService.findByCode(grupoSalvo.getCodigo());
        }
        return genericAssembler.toModel(grupoSalvo, GrupoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Grupo.EDITAR + "')")
    @PutMapping("/{codigo}")
    public GrupoModelBasico atualizar(@PathVariable UUID codigo, @RequestBody @Valid GrupoInput grupoInput) {
        Grupo grupo = getGrupoService.findByCode(codigo);
        genericDisassembler.copyToDomainObject(grupoInput, grupo);
        Grupo grupoAtualizado = atualizaGrupoService.atualiza(grupo);
        if (grupoInput.getPermissoesCodigos() != null) {
            syncGrupoPermissoesService.syncPermissoes(codigo, grupoInput.getPermissoesCodigos());
            grupoAtualizado = getGrupoService.findByCode(codigo);
        }
        return genericAssembler.toModel(grupoAtualizado, GrupoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Grupo.EXCLUIR + "')")
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID codigo) {
        deletaGrupoService.deletar(codigo);
    }
}
