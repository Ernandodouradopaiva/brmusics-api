package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.dto.CategoriaLiturgicaModel;
import br.gov.ce.sps.projetoa.api.dto.MusicaModelBasico;
import br.gov.ce.sps.projetoa.api.input.AtivoInput;
import br.gov.ce.sps.projetoa.api.input.MusicaInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.filter.MusicaFilter;
import br.gov.ce.sps.projetoa.domain.model.CategoriasLiturgicas;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.service.musica.AtualizaMusicaService;
import br.gov.ce.sps.projetoa.domain.service.musica.CadastroMusicaService;
import br.gov.ce.sps.projetoa.domain.service.musica.DeletaMusicaService;
import br.gov.ce.sps.projetoa.domain.service.musica.GetMusicaService;
import br.gov.ce.sps.projetoa.domain.service.musica.ListMusicaService;
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
@RequestMapping("/musicas")
@RequiredArgsConstructor
public class MusicaController {

    private final ListMusicaService listMusicaService;
    private final GetMusicaService getMusicaService;
    private final CadastroMusicaService cadastroMusicaService;
    private final AtualizaMusicaService atualizaMusicaService;
    private final DeletaMusicaService deletaMusicaService;
    private final GenericAssembler genericAssembler;

    @PreAuthorize("hasAnyAuthority('"
            + Permissoes.Musica.LISTAR + "', '"
            + Permissoes.Repertorio.LISTAR + "', '"
            + Permissoes.Repertorio.VISUALIZAR + "', '"
            + Permissoes.Repertorio.CRIAR + "', '"
            + Permissoes.Repertorio.EDITAR + "')")
    @GetMapping("/categorias")
    public List<CategoriaLiturgicaModel> categorias() {
        return CategoriasLiturgicas.SUGERIDAS.stream().map(codigo -> {
            CategoriaLiturgicaModel item = new CategoriaLiturgicaModel();
            item.setCodigo(codigo);
            item.setRotulo(CategoriasLiturgicas.rotulo(codigo));
            return item;
        }).toList();
    }

    @PreAuthorize("hasAnyAuthority('"
            + Permissoes.Musica.LISTAR + "', '"
            + Permissoes.Repertorio.LISTAR + "', '"
            + Permissoes.Repertorio.VISUALIZAR + "', '"
            + Permissoes.Repertorio.CRIAR + "', '"
            + Permissoes.Repertorio.EDITAR + "')")
    @GetMapping
    public Page<MusicaModelBasico> listar(
            MusicaFilter filtro,
            @PageableDefault(size = 5, sort = "titulo") Pageable pageable) {
        Page<Musica> page = listMusicaService.listar(filtro, pageable);
        List<MusicaModelBasico> dto = Objects.requireNonNull(
                genericAssembler.toCollectionModel(page.getContent(), MusicaModelBasico.class));
        return new PageImpl<>(dto, page.getPageable(), page.getTotalElements());
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Musica.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public MusicaModelBasico buscar(@PathVariable UUID codigo) {
        return genericAssembler.toModel(getMusicaService.findByCode(codigo), MusicaModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Musica.CRIAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MusicaModelBasico adicionar(@Valid @RequestBody MusicaInput input) {
        return genericAssembler.toModel(cadastroMusicaService.salvar(input), MusicaModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Musica.EDITAR + "')")
    @PutMapping("/{codigo}")
    public MusicaModelBasico atualizar(@PathVariable UUID codigo, @Valid @RequestBody MusicaInput input) {
        Musica musica = getMusicaService.findByCode(codigo);
        return genericAssembler.toModel(atualizaMusicaService.atualiza(musica, input), MusicaModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Musica.EDITAR + "')")
    @PutMapping("/{codigo}/ativo")
    public MusicaModelBasico atualizarPropriedadeAtivo(
            @PathVariable UUID codigo,
            @Valid @RequestBody AtivoInput input) {
        Musica musica = getMusicaService.findByCode(codigo);
        return genericAssembler.toModel(atualizaMusicaService.atualizarAtivo(musica, input.ativo()), MusicaModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Musica.EXCLUIR + "')")
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID codigo) {
        deletaMusicaService.deletar(codigo);
    }
}
