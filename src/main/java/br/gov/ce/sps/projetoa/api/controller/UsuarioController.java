package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.dto.SenhaTemporariaModel;
import br.gov.ce.sps.projetoa.api.dto.UsuarioModelBasico;
import br.gov.ce.sps.projetoa.api.input.AtivoInput;
import br.gov.ce.sps.projetoa.api.input.GruposInput;
import br.gov.ce.sps.projetoa.api.input.RecuperarSenhaInput;
import br.gov.ce.sps.projetoa.api.input.SenhaInput;
import br.gov.ce.sps.projetoa.api.input.UsuarioInput;
import br.gov.ce.sps.projetoa.core.security.Permissoes;
import br.gov.ce.sps.projetoa.domain.filter.UsuarioFilter;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.service.usuario.AtualizaUsuarioService;
import br.gov.ce.sps.projetoa.domain.service.usuario.CadastroUsuarioService;
import br.gov.ce.sps.projetoa.domain.service.usuario.DeletaUsuarioService;
import br.gov.ce.sps.projetoa.domain.service.usuario.GetUsuarioService;
import br.gov.ce.sps.projetoa.domain.service.usuario.ListUsuarioService;
import br.gov.ce.sps.projetoa.domain.service.usuario.SenhaUsuarioService;
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
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final ListUsuarioService listUsuarioService;
    private final GetUsuarioService getUsuarioService;
    private final CadastroUsuarioService cadastroUsuarioService;
    private final AtualizaUsuarioService atualizaUsuarioService;
    private final DeletaUsuarioService deletaUsuarioService;
    private final GenericAssembler genericAssembler;
    private final SenhaUsuarioService senhaUsuarioService;

    @PreAuthorize("hasAuthority('" + Permissoes.Usuario.LISTAR + "')")
    @GetMapping
    public Page<UsuarioModelBasico> listar(UsuarioFilter filtro, @PageableDefault(size = 5) Pageable pageable) {
        Page<Usuario> usuarioPage = listUsuarioService.listar(filtro, pageable);
        List<UsuarioModelBasico> usuarioDTO = Objects.requireNonNull(
                genericAssembler.toCollectionModel(usuarioPage.getContent(), UsuarioModelBasico.class));
        return new PageImpl<>(usuarioDTO, usuarioPage.getPageable(), usuarioPage.getTotalElements());
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Usuario.VISUALIZAR + "')")
    @GetMapping("/{codigo}")
    public UsuarioModelBasico buscar(@PathVariable UUID codigo) {
        Usuario usuario = getUsuarioService.findByCode(codigo);
        return genericAssembler.toModel(usuario, UsuarioModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Usuario.CRIAR + "')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioModelBasico cadastrar(@Valid @RequestBody UsuarioInput usuarioInput) {
        Usuario usuario = cadastroUsuarioService.salvar(usuarioInput);
        return genericAssembler.toModel(usuario, UsuarioModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Usuario.EDITAR + "')")
    @PutMapping("/{usuarioId}/grupos")
    public UsuarioModelBasico atribuirGrupos(@PathVariable Long usuarioId, @Valid @RequestBody GruposInput input) {
        Usuario usuario = cadastroUsuarioService.atribuirGrupos(usuarioId, input);
        return genericAssembler.toModel(usuario, UsuarioModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Usuario.EDITAR + "')")
    @PutMapping("/{codigo}")
    public UsuarioModelBasico atualizar(@PathVariable UUID codigo, @Valid @RequestBody UsuarioInput usuarioInput) {
        Usuario usuario = getUsuarioService.findByCode(codigo);
        Usuario usuarioAtualizado = atualizaUsuarioService.atualiza(usuario, usuarioInput);
        return genericAssembler.toModel(usuarioAtualizado, UsuarioModelBasico.class);
    }

    @PutMapping("/{codigo}/alterar-senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarSenha(@PathVariable UUID codigo, @Valid @RequestBody SenhaInput senhaInput) {
        senhaUsuarioService.alterarSenha(codigo, senhaInput);
    }

    @PutMapping("/recuperar-senha")
    public SenhaTemporariaModel recuperarSenha(@Valid @RequestBody RecuperarSenhaInput recuperarSenhaInput) {
        return senhaUsuarioService.recuperarSenha(recuperarSenhaInput);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Usuario.EDITAR + "')")
    @PutMapping("/{codigo}/ativo")
    public UsuarioModelBasico atualizarPropriedadeAtivo(
            @PathVariable UUID codigo,
            @Valid @RequestBody AtivoInput input) {
        Usuario usuario = getUsuarioService.findByCode(codigo);
        Usuario atualizado = atualizaUsuarioService.atualizarAtivo(usuario, input.ativo());
        return genericAssembler.toModel(atualizado, UsuarioModelBasico.class);
    }

    @PreAuthorize("hasAuthority('" + Permissoes.Usuario.EXCLUIR + "')")
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID codigo) {
        deletaUsuarioService.deletar(codigo);
    }
}
