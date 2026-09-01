package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.dto.InstrumentoModelBasico;
import br.gov.ce.sps.projetoa.api.input.AtivoInput;
import br.gov.ce.sps.projetoa.api.input.InstrumentoInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.filter.InstrumentoFilter;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.service.instrumento.AtualizaInstrumentoService;
import br.gov.ce.sps.projetoa.domain.service.instrumento.CadastroInstrumentoService;
import br.gov.ce.sps.projetoa.domain.service.instrumento.DeletaInstrumentoService;
import br.gov.ce.sps.projetoa.domain.service.instrumento.GetInstrumentoService;
import br.gov.ce.sps.projetoa.domain.service.instrumento.ListInstrumentoService;
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
@RequestMapping("/instrumentos")
@RequiredArgsConstructor
public class InstrumentoController {

    private final ListInstrumentoService listInstrumentoService;
    private final GetInstrumentoService getInstrumentoService;
    private final CadastroInstrumentoService cadastroInstrumentoService;
    private final AtualizaInstrumentoService atualizaInstrumentoService;
    private final DeletaInstrumentoService deletaInstrumentoService;
    private final GenericAssembler genericAssembler;

    @PreAuthorize("hasAnyAuthority('" + Permissoes.Instrumento.LISTAR + "', '"
            + Permissoes.Musico.CRIAR + "', '" + Permissoes.Musico.EDITAR + "', '"
            + Permissoes.Escala.LISTAR + "', '"
            + Permissoes.Escala.CRIAR + "', '" + Permissoes.Escala.EDITAR + "')")
    @GetMapping
    public Page<InstrumentoModelBasico> listar(
            InstrumentoFilter filtro,
            @PageableDefault(size = 5, sort = {"ordem", "nome"}) Pageable pageable) {
        Page<Instrumento> page = listInstrumentoService.listar(filtro, pageable);
        List<InstrumentoModelBasico> dto = Objects.requireNonNull(
                genericAssembler.toCollectionModel(page.getContent(), InstrumentoModelBasico.class));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Instrumento.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public InstrumentoModelBasico buscar(@PathVariable UUID codigo) {
        return genericAssembler.toModel(getInstrumentoService.findByCode(codigo), InstrumentoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Instrumento.CRIAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstrumentoModelBasico adicionar(@Valid @RequestBody InstrumentoInput input) {
        Instrumento salvo = cadastroInstrumentoService.salvar(input);
        return genericAssembler.toModel(salvo, InstrumentoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Instrumento.EDITAR + "')")
    @PutMapping("/{codigo}")
    public InstrumentoModelBasico atualizar(@PathVariable UUID codigo, @Valid @RequestBody InstrumentoInput input) {
        Instrumento instrumento = getInstrumentoService.findByCode(codigo);
        Instrumento atualizado = atualizaInstrumentoService.atualiza(instrumento, input);
        return genericAssembler.toModel(atualizado, InstrumentoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Instrumento.EDITAR + "')")
    @PutMapping("/{codigo}/ativo")
    public InstrumentoModelBasico atualizarPropriedadeAtivo(
            @PathVariable UUID codigo,
            @Valid @RequestBody AtivoInput input) {
        Instrumento instrumento = getInstrumentoService.findByCode(codigo);
        Instrumento atualizado = atualizaInstrumentoService.atualizarAtivo(instrumento, input.ativo());
        return genericAssembler.toModel(atualizado, InstrumentoModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Instrumento.EXCLUIR + "')")
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID codigo) {
        deletaInstrumentoService.deletar(codigo);
    }
}
