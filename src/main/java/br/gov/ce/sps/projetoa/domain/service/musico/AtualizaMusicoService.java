package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.api.input.MusicoInput;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtualizaMusicoService {

    private final MusicoRepository musicoRepository;
    private final CadastroMusicoService cadastroMusicoService;

    @Transactional
    public Musico atualiza(Musico musico, MusicoInput input) {
        if (musico.getCodigo() == null || musico.getId() == null) {
            throw new RuntimeException("Músico não encontrado");
        }
        cadastroMusicoService.aplicarDados(musico, input, musico.getId());
        return musicoRepository.save(musico);
    }

    @Transactional
    public Musico atualizarAtivo(Musico musico, boolean ativo) {
        musico.setAtivo(ativo);
        return musicoRepository.save(musico);
    }
}
