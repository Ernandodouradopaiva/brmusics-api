package br.gov.ce.sps.projetoa.domain.service.usuario;

import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario findByCode(UUID codigo) {
        return usuarioRepository.findByCodigo(codigo).orElseThrow(
                () -> new EntityNotFoundException("Usuário não encontrada"));
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Usuário não encontrada"));
    }

}
