package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.dto.RepertorioMensalItemModel;
import br.gov.ce.sps.projetoa.api.dto.RepertorioModel;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.service.repertorio.MeuRepertorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meu-repertorio")
@RequiredArgsConstructor
public class MeuRepertorioController {

    private final MeuRepertorioService meuRepertorioService;

    @PreAuthorize("hasAuthority('" + Permissoes.MeuRepertorio.LISTAR + "')")
    @GetMapping
    public List<RepertorioMensalItemModel> listar() {
        return meuRepertorioService.listar();
    }

    @PreAuthorize("hasAuthority('" + Permissoes.MeuRepertorio.VISUALIZAR + "')")
    @GetMapping("/por-celebracao/{celebracaoCodigo}")
    public RepertorioModel buscarPorCelebracao(@PathVariable UUID celebracaoCodigo) {
        return meuRepertorioService.buscarPorCelebracao(celebracaoCodigo);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.MeuRepertorio.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public RepertorioModel buscar(@PathVariable UUID codigo) {
        return meuRepertorioService.buscar(codigo);
    }
}
