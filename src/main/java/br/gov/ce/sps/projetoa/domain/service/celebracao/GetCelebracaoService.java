package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCelebracaoService {

    private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de celebração com código %s";

    private final CelebracaoRepository celebracaoRepository;

    public Celebracao findByCode(UUID codigo) {
        return celebracaoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, codigo)));
    }
}
