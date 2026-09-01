package br.gov.ce.sps.projetoa.domain.service.instrumento;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.repository.InstrumentoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetInstrumentoService {

    private static final String MSG_NAO_ENCONTRADO = "Não existe um cadastro de instrumento com código %s";

    private final InstrumentoRepository instrumentoRepository;

    public Instrumento findByCode(UUID codigo) {
        return instrumentoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException(String.format(MSG_NAO_ENCONTRADO, codigo)));
    }

    public List<Instrumento> findAllByUUID(List<UUID> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<UUID> unicos = new LinkedHashSet<>(codigos);
        List<Instrumento> encontrados = new ArrayList<>();
        for (UUID codigo : unicos) {
            encontrados.add(findByCode(codigo));
        }
        if (encontrados.size() != unicos.size()) {
            throw new NegocioException("Um ou mais instrumentos informados não existem.");
        }
        return encontrados;
    }
}
