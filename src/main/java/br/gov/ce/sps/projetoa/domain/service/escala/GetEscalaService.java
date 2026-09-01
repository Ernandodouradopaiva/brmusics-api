package br.gov.ce.sps.projetoa.domain.service.escala;

import br.gov.ce.sps.projetoa.api.dto.EscalaModel;
import br.gov.ce.sps.projetoa.domain.model.Escala;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetEscalaService {

    private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de escala com código %s";

    private final EscalaRepository escalaRepository;
    private final CadastroEscalaService cadastroEscalaService;

    public Escala findByCode(UUID codigo) {
        return escalaRepository.findComCelebracaoByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, codigo)));
    }

    @Transactional(readOnly = true)
    public EscalaModel buscar(UUID codigo) {
        Escala escala = findByCode(codigo);
        return cadastroEscalaService.toModel(escala, cadastroEscalaService.alertasConflito(escala));
    }
}
