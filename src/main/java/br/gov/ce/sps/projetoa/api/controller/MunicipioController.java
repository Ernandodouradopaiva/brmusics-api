package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.disassembler.GenericDisassembler;
import br.gov.ce.sps.projetoa.api.dto.MunicipioModelBasico;
import br.gov.ce.sps.projetoa.api.input.MunicipioInput;
import br.gov.ce.sps.projetoa.domain.model.Municipio;
import br.gov.ce.sps.projetoa.domain.service.municipio.CadastroMunicipioService;
import br.gov.ce.sps.projetoa.domain.service.municipio.GetMunicipioService;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estados/{estadoId}/municipios")
@RequiredArgsConstructor
public class MunicipioController {
    
    private final GetMunicipioService getMunicipioService;
    private final CadastroMunicipioService cadastroMunicipioService;
    private final GenericAssembler genericAssembler;
    private final GenericDisassembler genericDisassembler;
    
    @GetMapping
    public List<MunicipioModelBasico> listar(@PathVariable Long estadoId) {
        return genericAssembler.toCollectionModel(
            getMunicipioService.listarPorEstado(estadoId), MunicipioModelBasico.class);
    }
    
    @GetMapping("/{id}")
    public MunicipioModelBasico buscar(@PathVariable Long id) {
        return genericAssembler.toModel(
            getMunicipioService.buscarPorId(id), MunicipioModelBasico.class);
    }
    
    @PreAuthorize("hasAuthority('" + Permissoes.Inicio.LISTAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MunicipioModelBasico cadastrar(
            @PathVariable Long estadoId,
            @Valid @RequestBody MunicipioInput municipioInput) {
        Municipio municipio = genericDisassembler.toDomainObject(municipioInput, Municipio.class);
        Municipio municipioSalvo = cadastroMunicipioService.cadastrar(municipio, estadoId);
        return genericAssembler.toModel(municipioSalvo, MunicipioModelBasico.class);
    }
}

