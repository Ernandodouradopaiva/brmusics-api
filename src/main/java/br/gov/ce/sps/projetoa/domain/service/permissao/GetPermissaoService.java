package br.gov.ce.sps.projetoa.domain.service.permissao;

import br.gov.ce.sps.projetoa.domain.model.Permissao;
import br.gov.ce.sps.projetoa.domain.repository.PermissaoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPermissaoService {

    private final PermissaoRepository permissaoRepository;

    public Permissao findByCode(UUID codigo) {
        return permissaoRepository.findByCodigo(codigo).orElseThrow(
                () -> new EntityNotFoundException("Permissão não encontrada"));
    }

    public Permissao findById(Long id) {
        return permissaoRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Permissão não encontrada"));
    }
}