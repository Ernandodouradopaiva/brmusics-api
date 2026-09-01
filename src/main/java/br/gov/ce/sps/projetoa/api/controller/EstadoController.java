package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.disassembler.GenericDisassembler;
import br.gov.ce.sps.projetoa.api.dto.EstadoModelBasico;
import br.gov.ce.sps.projetoa.api.input.EstadoInput;
import br.gov.ce.sps.projetoa.domain.model.Estado;
import br.gov.ce.sps.projetoa.domain.service.estado.CadastroEstadoService;
import br.gov.ce.sps.projetoa.domain.service.estado.GetEstadoService;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estados")
@RequiredArgsConstructor
public class EstadoController {
    
    private final GetEstadoService getEstadoService;
    private final CadastroEstadoService cadastroEstadoService;
    private final GenericAssembler genericAssembler;
    private final GenericDisassembler genericDisassembler;
    
    @GetMapping
    public List<EstadoModelBasico> listar() {
        return genericAssembler.toCollectionModel(
            getEstadoService.listarTodos(), EstadoModelBasico.class);
    }
    
    @GetMapping("/{id}")
    public EstadoModelBasico buscar(@PathVariable Long id) {
        return genericAssembler.toModel(
            getEstadoService.buscarPorId(id), EstadoModelBasico.class);
    }
    
    @PreAuthorize("hasAuthority('" + Permissoes.Inicio.LISTAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstadoModelBasico cadastrar(@Valid @RequestBody EstadoInput estadoInput) {
        Estado estado = genericDisassembler.toDomainObject(estadoInput, Estado.class);
        Estado estadoSalvo = cadastroEstadoService.cadastrar(estado);
        return genericAssembler.toModel(estadoSalvo, EstadoModelBasico.class);
    }
}

