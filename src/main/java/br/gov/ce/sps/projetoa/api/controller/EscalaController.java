package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.dto.EscalaMensalItemModel;
import br.gov.ce.sps.projetoa.api.dto.EscalaModel;
import br.gov.ce.sps.projetoa.api.dto.EscalaPublicacaoModel;
import br.gov.ce.sps.projetoa.api.dto.EscalaPublicacaoPreviaModel;
import br.gov.ce.sps.projetoa.api.input.CopiarEscalaInput;
import br.gov.ce.sps.projetoa.api.input.DuplicarEscalaAnteriorInput;
import br.gov.ce.sps.projetoa.api.input.EscalaInput;
import br.gov.ce.sps.projetoa.api.input.PublicarEscalaMensalInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.service.escala.AtualizaEscalaService;
import br.gov.ce.sps.projetoa.domain.service.escala.CadastroEscalaService;
import br.gov.ce.sps.projetoa.domain.service.escala.CopiarEscalaService;
import br.gov.ce.sps.projetoa.domain.service.escala.DeletaEscalaService;
import br.gov.ce.sps.projetoa.domain.service.escala.DuplicarEscalaAnteriorService;
import br.gov.ce.sps.projetoa.domain.service.escala.GetEscalaService;
import br.gov.ce.sps.projetoa.domain.service.escala.ListEscalaMensalService;
import br.gov.ce.sps.projetoa.domain.service.escala.PublicarEscalaMensalService;
import br.gov.ce.sps.projetoa.domain.service.escala.PublicarEscalaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/escalas")
@RequiredArgsConstructor
public class EscalaController {

    private final ListEscalaMensalService listEscalaMensalService;
    private final GetEscalaService getEscalaService;
    private final CadastroEscalaService cadastroEscalaService;
    private final AtualizaEscalaService atualizaEscalaService;
    private final DeletaEscalaService deletaEscalaService;
    private final CopiarEscalaService copiarEscalaService;
    private final DuplicarEscalaAnteriorService duplicarEscalaAnteriorService;
    private final PublicarEscalaMensalService publicarEscalaMensalService;
    private final PublicarEscalaService publicarEscalaService;

    @PreAuthorize("hasAuthority('" + Permissoes.Escala.LISTAR + "')")
    @GetMapping("/mensal")
    public List<EscalaMensalItemModel> listarMensal(@RequestParam int ano, @RequestParam int mes) {
        return listEscalaMensalService.listar(ano, mes);
    }

    @PreAuthorize("hasAnyAuthority('" + Permissoes.Escala.LISTAR + "', '" + Permissoes.Escala.PUBLICAR + "')")
    @GetMapping("/publicacoes/previa")
    public EscalaPublicacaoPreviaModel previaPublicacao(@RequestParam int ano, @RequestParam int mes) {
        return publicarEscalaMensalService.previa(ano, mes);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Escala.PUBLICAR + "')")
    @PostMapping("/publicacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public EscalaPublicacaoModel publicar(@Valid @RequestBody PublicarEscalaMensalInput input) {
        return publicarEscalaMensalService.publicar(input.getAno(), input.getMes());
    }

    @PreAuthorize("hasAnyAuthority('" + Permissoes.Escala.PUBLICAR + "', '" + Permissoes.Escala.EDITAR + "')")
    @PostMapping("/{codigo}/publicar")
    public EscalaModel publicarUma(@PathVariable UUID codigo) {
        return publicarEscalaService.publicar(codigo);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Escala.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public EscalaModel buscar(@PathVariable UUID codigo) {
        return getEscalaService.buscar(codigo);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Escala.CRIAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EscalaModel adicionar(@Valid @RequestBody EscalaInput input) {
        return cadastroEscalaService.salvar(input);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Escala.EDITAR + "')")
    @PutMapping("/{codigo}")
    public EscalaModel atualizar(@PathVariable UUID codigo, @Valid @RequestBody EscalaInput input) {
        return atualizaEscalaService.atualizar(codigo, input);
    }

    @PreAuthorize("hasAnyAuthority('" + Permissoes.Escala.CRIAR + "', '" + Permissoes.Escala.EDITAR + "')")
    @PostMapping("/copiar")
    public EscalaModel copiar(@Valid @RequestBody CopiarEscalaInput input) {
        return copiarEscalaService.copiar(input);
    }

    @PreAuthorize("hasAnyAuthority('" + Permissoes.Escala.CRIAR + "', '" + Permissoes.Escala.EDITAR + "')")
    @PostMapping("/duplicar-anterior")
    public EscalaModel duplicarAnterior(@Valid @RequestBody DuplicarEscalaAnteriorInput input) {
        return duplicarEscalaAnteriorService.duplicar(input);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Escala.EXCLUIR + "')")
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID codigo) {
        deletaEscalaService.deletar(codigo);
    }
}
