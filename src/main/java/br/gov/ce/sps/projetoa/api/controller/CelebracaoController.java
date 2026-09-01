package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.dto.CelebracaoModelBasico;
import br.gov.ce.sps.projetoa.api.input.CelebracaoInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.filter.CelebracaoFilter;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.service.celebracao.AtualizaCelebracaoService;
import br.gov.ce.sps.projetoa.domain.service.celebracao.CadastroCelebracaoService;
import br.gov.ce.sps.projetoa.domain.service.celebracao.DeletaCelebracaoService;
import br.gov.ce.sps.projetoa.domain.service.celebracao.GetCelebracaoService;
import br.gov.ce.sps.projetoa.domain.service.celebracao.ListCelebracaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/celebracoes")
@RequiredArgsConstructor
public class CelebracaoController {

    private final ListCelebracaoService listCelebracaoService;
    private final GetCelebracaoService getCelebracaoService;
    private final CadastroCelebracaoService cadastroCelebracaoService;
    private final AtualizaCelebracaoService atualizaCelebracaoService;
    private final DeletaCelebracaoService deletaCelebracaoService;
    private final GenericAssembler genericAssembler;

    @PreAuthorize("hasAnyAuthority('" + Permissoes.Celebracao.LISTAR + "', '"
            + Permissoes.Escala.LISTAR + "', '"
            + Permissoes.Escala.CRIAR + "', '" + Permissoes.Escala.EDITAR + "')")
    @GetMapping
    public Page<CelebracaoModelBasico> listar(
            CelebracaoFilter filtro,
            @PageableDefault(size = 5, sort = {"data", "horaInicio"}, direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<Celebracao> page = listCelebracaoService.listar(filtro, pageable);
        List<CelebracaoModelBasico> dto = Objects.requireNonNull(
                genericAssembler.toCollectionModel(page.getContent(), CelebracaoModelBasico.class));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Celebracao.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public CelebracaoModelBasico buscar(@PathVariable UUID codigo) {
        return genericAssembler.toModel(getCelebracaoService.findByCode(codigo), CelebracaoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Celebracao.CRIAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CelebracaoModelBasico adicionar(@Valid @RequestBody CelebracaoInput input) {
        Celebracao salvo = cadastroCelebracaoService.salvar(input);
        return genericAssembler.toModel(getCelebracaoService.findByCode(salvo.getCodigo()), CelebracaoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Celebracao.EDITAR + "')")
    @PutMapping("/{codigo}")
    public CelebracaoModelBasico atualizar(@PathVariable UUID codigo, @Valid @RequestBody CelebracaoInput input) {
        Celebracao celebracao = getCelebracaoService.findByCode(codigo);
        atualizaCelebracaoService.atualiza(celebracao, input);
        return genericAssembler.toModel(getCelebracaoService.findByCode(codigo), CelebracaoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Celebracao.EXCLUIR + "')")
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID codigo) {
        deletaCelebracaoService.deletar(codigo);
    }
}
