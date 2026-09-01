package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMusicoService {

    private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de músico com código %s";

    private final MusicoRepository musicoRepository;

    public Musico findByCode(UUID codigo) {
        return musicoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, codigo)));
    }
}
