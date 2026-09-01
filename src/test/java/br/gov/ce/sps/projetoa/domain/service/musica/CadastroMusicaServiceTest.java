package br.gov.ce.sps.projetoa.domain.service.musica;

import br.gov.ce.sps.projetoa.api.input.MusicaInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.CategoriasLiturgicas;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.repository.MusicaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroMusicaServiceTest {

    @Mock
    private MusicaRepository musicaRepository;

    private CadastroMusicaService service;

    @BeforeEach
    void setUp() {
        service = new CadastroMusicaService(musicaRepository);
    }

    @Test
    void salvaComCategoriaSugeridaEAtivo() {
        when(musicaRepository.save(any(Musica.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MusicaInput input = new MusicaInput();
        input.setTitulo("Santo");
        input.setAutor("Comunidade");
        input.setCategoriaLiturgica("santo");
        input.setTomPadrao("G");
        input.setLinkReferencia("https://exemplo.test/santo");

        Musica salvo = service.salvar(input);

        assertThat(salvo.getTitulo()).isEqualTo("Santo");
        assertThat(salvo.getCategoriaLiturgica()).isEqualTo(CategoriasLiturgicas.SANTO);
        assertThat(salvo.getAtivo()).isTrue();
        assertThat(salvo.getLinkReferencia()).isEqualTo("https://exemplo.test/santo");
    }

    @Test
    void aceitaCategoriaForaDaListaSugerida() {
        when(musicaRepository.save(any(Musica.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MusicaInput input = new MusicaInput();
        input.setTitulo("Hino mariano");
        input.setCategoriaLiturgica("mariano");

        Musica salvo = service.salvar(input);

        assertThat(salvo.getCategoriaLiturgica()).isEqualTo("MARIANO");
    }

    @Test
    void rejeitaLinkSemProtocolo() {
        MusicaInput input = new MusicaInput();
        input.setTitulo("Glória");
        input.setLinkReferencia("youtube.com/watch");

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("O link de referência deve começar com http:// ou https://.");
    }

    @Test
    void rejeitaTituloVazio() {
        MusicaInput input = new MusicaInput();
        input.setTitulo("   ");

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Informe o título da música.");
    }
}
