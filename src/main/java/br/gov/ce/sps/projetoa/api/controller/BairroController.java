package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.disassembler.GenericDisassembler;
import br.gov.ce.sps.projetoa.api.dto.BairroModelBasico;
import br.gov.ce.sps.projetoa.api.input.BairroInput;
import br.gov.ce.sps.projetoa.domain.model.Bairro;
import br.gov.ce.sps.projetoa.domain.service.bairro.CadastroBairroService;
import br.gov.ce.sps.projetoa.domain.service.bairro.GetBairroService;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/municipios/{municipioId}/bairros")
@RequiredArgsConstructor
public class BairroController {
    
    private final GetBairroService getBairroService;
    private final CadastroBairroService cadastroBairroService;
    private final GenericAssembler genericAssembler;
    private final GenericDisassembler genericDisassembler;
    
    @GetMapping
    public List<BairroModelBasico> listar(@PathVariable Long municipioId) {
        return genericAssembler.toCollectionModel(
            getBairroService.listarPorMunicipio(municipioId), BairroModelBasico.class);
    }
    
    @GetMapping("/{id}")
    public BairroModelBasico buscar(@PathVariable Long id) {
        return genericAssembler.toModel(
            getBairroService.buscaBairroPorId(id), BairroModelBasico.class);
    }
    
    @PreAuthorize("hasAuthority('" + Permissoes.Inicio.LISTAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BairroModelBasico cadastrar(
            @PathVariable Long municipioId,
            @Valid @RequestBody BairroInput bairroInput) {
        Bairro bairro = genericDisassembler.toDomainObject(bairroInput, Bairro.class);
        Bairro bairroSalvo = cadastroBairroService.cadastrar(bairro, municipioId);
        return genericAssembler.toModel(bairroSalvo, BairroModelBasico.class);
    }
}

