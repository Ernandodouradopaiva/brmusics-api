package br.gov.ce.sps.projetoa.domain.service.usuario;

import br.gov.ce.sps.projetoa.domain.filter.UsuarioFilter;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.UsuarioRepository;
import br.gov.ce.sps.projetoa.domain.spec.UsuarioSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos(UsuarioFilter usuarioFilter) {
        return usuarioRepository.findAll(UsuarioSpec.usandoFiltro(usuarioFilter));
    }

    public Page<Usuario> listar(UsuarioFilter usuarioFilter, Pageable pageable) {
        return usuarioRepository.findAll(UsuarioSpec.usandoFiltro(usuarioFilter), pageable);
    }
}
