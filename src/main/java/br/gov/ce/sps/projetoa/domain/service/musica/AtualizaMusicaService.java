package br.gov.ce.sps.projetoa.domain.service.musica;

import br.gov.ce.sps.projetoa.api.input.MusicaInput;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.repository.MusicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtualizaMusicaService {

    private final MusicaRepository musicaRepository;
    private final CadastroMusicaService cadastroMusicaService;

    @Transactional
    public Musica atualiza(Musica musica, MusicaInput input) {
        if (musica.getCodigo() == null || musica.getId() == null) {
            throw new RuntimeException("Música não encontrada");
        }
        cadastroMusicaService.aplicarDados(musica, input);
        return musicaRepository.save(musica);
    }

    @Transactional
    public Musica atualizarAtivo(Musica musica, boolean ativo) {
        musica.setAtivo(ativo);
        return musicaRepository.save(musica);
    }
}
