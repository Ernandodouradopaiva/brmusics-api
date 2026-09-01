package br.gov.ce.sps.projetoa.domain.service.musica;

import br.gov.ce.sps.projetoa.domain.filter.MusicaFilter;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.repository.MusicaRepository;
import br.gov.ce.sps.projetoa.domain.spec.MusicaSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListMusicaService {

    private final MusicaRepository musicaRepository;

    public Page<Musica> listar(MusicaFilter filtro, Pageable pageable) {
        return musicaRepository.findAll(MusicaSpec.usandoFiltro(filtro), pageable);
    }
}
