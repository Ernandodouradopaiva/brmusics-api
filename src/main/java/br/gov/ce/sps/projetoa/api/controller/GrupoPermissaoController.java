package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.dto.PermissaoModelBasico;
import br.gov.ce.sps.projetoa.api.input.GrupoPermissoesChavesInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.model.Grupo;
import br.gov.ce.sps.projetoa.domain.service.grupo.GetGrupoService;
import br.gov.ce.sps.projetoa.domain.service.grupo.SyncGrupoPermissoesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(value = "/grupos/{codigoGrupo}/permissoes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GrupoPermissaoController {

    private final GetGrupoService getGrupoService;
    private final SyncGrupoPermissoesService syncGrupoPermissoesService;
    private final GenericAssembler genericAssembler;

    @PreAuthorize("hasAnyAuthority('" + Permissoes.Grupo.GERENCIAR_PERMISSOES + "', '" + Permissoes.Grupo.EDITAR + "')")
    @GetMapping
    public Set<PermissaoModelBasico> listar(@PathVariable UUID codigoGrupo) {
        Grupo grupo = getGrupoService.findByCode(codigoGrupo);
        return genericAssembler.toCollectionModelSet(grupo.getPermissoes(), PermissaoModelBasico.class);
    }

    @PreAuthorize("hasAnyAuthority('" + Permissoes.Grupo.GERENCIAR_PERMISSOES + "', '" + Permissoes.Grupo.EDITAR + "')")
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> substituirPorChaves(
            @PathVariable UUID codigoGrupo, @RequestBody @Valid GrupoPermissoesChavesInput input) {
        syncGrupoPermissoesService.syncPermissoesPorChaves(codigoGrupo, input.getPermissoes());
        return ResponseEntity.noContent().build();
    }
}
