package br.gov.ce.sps.projetoa.domain.service.local;

import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.repository.LocalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetLocalService {

    private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de local com código %s";

    private final LocalRepository localRepository;

    public Local findByCode(UUID codigo) {
        return localRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, codigo)));
    }
}
