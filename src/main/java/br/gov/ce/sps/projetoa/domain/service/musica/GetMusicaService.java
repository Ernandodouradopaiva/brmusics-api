package br.gov.ce.sps.projetoa.domain.service.musica;

import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.repository.MusicaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMusicaService {

    private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de música com código %s";

    private final MusicaRepository musicaRepository;

    public Musica findByCode(UUID codigo) {
        return musicaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, codigo)));
    }
}
