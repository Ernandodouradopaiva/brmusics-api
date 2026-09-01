package br.gov.ce.sps.projetoa.domain.service.musica;

import br.gov.ce.sps.projetoa.api.input.MusicaInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.CategoriasLiturgicas;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.repository.MusicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CadastroMusicaService {

    private final MusicaRepository musicaRepository;

    @Transactional
    public Musica salvar(MusicaInput input) {
        Musica musica = new Musica();
        aplicarDados(musica, input);
        if (musica.getAtivo() == null) {
            musica.setAtivo(Boolean.TRUE);
        }
        return musicaRepository.save(musica);
    }

    void aplicarDados(Musica musica, MusicaInput input) {
        if (!StringUtils.hasText(input.getTitulo())) {
            throw new NegocioException("Informe o título da música.");
        }
        musica.setTitulo(input.getTitulo().trim());
        musica.setAutor(blankToNull(input.getAutor()));
        musica.setInterpreteReferencia(blankToNull(input.getInterpreteReferencia()));
        musica.setTomPadrao(blankToNull(input.getTomPadrao()));
        musica.setCategoriaLiturgica(CategoriasLiturgicas.normalizar(input.getCategoriaLiturgica()));
        musica.setLetra(blankToNull(input.getLetra()));
        musica.setCifra(blankToNull(input.getCifra()));
        musica.setLinkReferencia(normalizarLink(input.getLinkReferencia()));
        musica.setObservacao(blankToNull(input.getObservacao()));
        if (input.getAtivo() != null) {
            musica.setAtivo(input.getAtivo());
        }
    }

    private static String normalizarLink(String valor) {
        String link = blankToNull(valor);
        if (link == null) {
            return null;
        }
        String lower = link.toLowerCase();
        if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
            throw new NegocioException("O link de referência deve começar com http:// ou https://.");
        }
        return link;
    }

    private static String blankToNull(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        return valor.trim();
    }
}
