package br.gov.ce.sps.projetoa.domain.service.usuario;

import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletaUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GetUsuarioService getUsuarioService;

    public void deletar(UUID codigo) {
        Usuario usuario = getUsuarioService.findByCode(codigo);
        usuarioRepository.delete(usuario);
    }

}
