package br.gov.ce.sps.projetoa.domain.service.musica;

import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.repository.MusicaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportaMusicasCsvServiceTest {

    @Mock
    private MusicaRepository musicaRepository;

    private ExportaMusicasCsvService service;

    @BeforeEach
    void setUp() {
        service = new ExportaMusicasCsvService(musicaRepository);
    }

    @Test
    void exportaUtf8BomComPontoEVirgulaELinkDaLetra() {
        Musica musica = new Musica();
        musica.setTitulo("Santo; Louvor");
        musica.setAutor("Comunidade");
        musica.setInterpreteReferencia("Coral");
        musica.setTomPadrao("G");
        musica.setCategoriaLiturgica("SANTO");
        musica.setLetra("https://exemplo.com/letra");
        musica.setCifra("https://exemplo.com/cifra");
        musica.setLinkReferencia("https://youtube.com/watch?v=1");
        musica.setObservacao("Obs \"especial\"");
        musica.setAtivo(true);

        when(musicaRepository.findAll(any(Sort.class))).thenReturn(List.of(musica));

        byte[] bytes = service.exportar();
        assertThat(bytes[0]).isEqualTo((byte) 0xEF);
        assertThat(bytes[1]).isEqualTo((byte) 0xBB);
        assertThat(bytes[2]).isEqualTo((byte) 0xBF);

        String texto = new String(bytes, 3, bytes.length - 3, StandardCharsets.UTF_8);
        String[] linhas = texto.split("\r\n");
        assertThat(linhas[0]).isEqualTo(
                "Titulo;Autor;Interprete;Tom;Categoria;Categoria rotulo;Link da letra;Link da cifra;Link de referencia;Observacao;Ativo");
        assertThat(linhas[1]).contains("\"Santo; Louvor\"");
        assertThat(linhas[1]).contains("https://exemplo.com/letra");
        assertThat(linhas[1]).contains("\"Obs \"\"especial\"\"\"");
        assertThat(linhas[1]).endsWith(";Sim");
    }

    @Test
    void marcaInativaComoNao() {
        Musica musica = new Musica();
        musica.setTitulo("Teste");
        musica.setAtivo(false);
        assertThat(ExportaMusicasCsvService.linha(musica)).endsWith(";Não");
    }
}
