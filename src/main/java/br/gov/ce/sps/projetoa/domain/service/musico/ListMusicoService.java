package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.domain.filter.MusicoFilter;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import br.gov.ce.sps.projetoa.domain.spec.MusicoSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListMusicoService {

    private final MusicoRepository musicoRepository;

    @Transactional(readOnly = true)
    public Page<Musico> listar(MusicoFilter filtro, Pageable pageable) {
        Page<Musico> pagina = musicoRepository.findAll(MusicoSpec.usandoFiltro(filtro), pageable);
        List<Long> ids = pagina.getContent().stream().map(Musico::getId).toList();
        if (ids.isEmpty()) {
            return pagina;
        }
        Map<Long, Musico> comInstrumentos = musicoRepository.findWithInstrumentosByIdIn(ids).stream()
                .collect(Collectors.toMap(Musico::getId, Function.identity(), (a, b) -> a));
        pagina.getContent().forEach(musico -> {
            Musico carregado = comInstrumentos.get(musico.getId());
            if (carregado != null) {
                musico.setInstrumentos(carregado.getInstrumentos());
            }
        });
        return pagina;
    }
}
