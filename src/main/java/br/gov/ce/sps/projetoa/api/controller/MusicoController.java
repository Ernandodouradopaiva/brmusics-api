package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.dto.MusicoModelBasico;
import br.gov.ce.sps.projetoa.api.input.AtivoInput;
import br.gov.ce.sps.projetoa.api.input.MusicoInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.filter.MusicoFilter;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.service.musico.AtualizaMusicoService;
import br.gov.ce.sps.projetoa.domain.service.musico.CadastroMusicoService;
import br.gov.ce.sps.projetoa.domain.service.musico.DeletaMusicoService;
import br.gov.ce.sps.projetoa.domain.service.musico.GetMusicoService;
import br.gov.ce.sps.projetoa.domain.service.musico.ListMusicoService;
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
@RequestMapping("/musicos")
@RequiredArgsConstructor
public class MusicoController {

    private final ListMusicoService listMusicoService;
    private final GetMusicoService getMusicoService;
    private final CadastroMusicoService cadastroMusicoService;
    private final AtualizaMusicoService atualizaMusicoService;
    private final DeletaMusicoService deletaMusicoService;
    private final GenericAssembler genericAssembler;

    @PreAuthorize("hasAnyAuthority('" + Permissoes.Musico.LISTAR + "', '"
            + Permissoes.Escala.LISTAR + "', '"
            + Permissoes.Escala.CRIAR + "', '" + Permissoes.Escala.EDITAR + "')")
    @GetMapping
    public Page<MusicoModelBasico> listar(MusicoFilter filtro, @PageableDefault(size = 5, sort = "nome") Pageable pageable) {
        Page<Musico> musicoPage = listMusicoService.listar(filtro, pageable);
        List<MusicoModelBasico> dto = Objects.requireNonNull(
                genericAssembler.toCollectionModel(musicoPage.getContent(), MusicoModelBasico.class));
        return new PageImpl<>(dto, musicoPage.getPageable(), musicoPage.getTotalElements());
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Musico.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public MusicoModelBasico buscar(@PathVariable UUID codigo) {
        Musico musico = getMusicoService.findByCode(codigo);
        return genericAssembler.toModel(musico, MusicoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Musico.CRIAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MusicoModelBasico adicionar(@Valid @RequestBody MusicoInput input) {
        Musico salvo = cadastroMusicoService.salvar(input);
        return genericAssembler.toModel(getMusicoService.findByCode(salvo.getCodigo()), MusicoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Musico.EDITAR + "')")
    @PutMapping("/{codigo}")
    public MusicoModelBasico atualizar(@PathVariable UUID codigo, @Valid @RequestBody MusicoInput input) {
        Musico musico = getMusicoService.findByCode(codigo);
        atualizaMusicoService.atualiza(musico, input);
        return genericAssembler.toModel(getMusicoService.findByCode(codigo), MusicoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Musico.EDITAR + "')")
    @PutMapping("/{codigo}/ativo")
    public MusicoModelBasico atualizarPropriedadeAtivo(
            @PathVariable UUID codigo,
            @Valid @RequestBody AtivoInput input) {
        Musico musico = getMusicoService.findByCode(codigo);
        Musico atualizado = atualizaMusicoService.atualizarAtivo(musico, input.ativo());
        return genericAssembler.toModel(atualizado, MusicoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Musico.EXCLUIR + "')")
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID codigo) {
        deletaMusicoService.deletar(codigo);
    }
}
