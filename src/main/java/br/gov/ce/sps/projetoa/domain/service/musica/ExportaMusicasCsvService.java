package br.gov.ce.sps.projetoa.domain.service.musica;

import br.gov.ce.sps.projetoa.domain.model.CategoriasLiturgicas;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import br.gov.ce.sps.projetoa.domain.repository.MusicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportaMusicasCsvService {

    private static final byte[] UTF8_BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final String SEPARADOR = ";";
    private static final String QUEBRA = "\r\n";

    private final MusicaRepository musicaRepository;

    @Transactional(readOnly = true)
    public byte[] exportar() {
        List<Musica> musicas = musicaRepository.findAll(Sort.by(Sort.Direction.ASC, "titulo"));
        StringBuilder csv = new StringBuilder();
        csv.append(cabecalho()).append(QUEBRA);
        for (Musica musica : musicas) {
            csv.append(linha(musica)).append(QUEBRA);
        }
        byte[] corpo = csv.toString().getBytes(StandardCharsets.UTF_8);
        byte[] saida = new byte[UTF8_BOM.length + corpo.length];
        System.arraycopy(UTF8_BOM, 0, saida, 0, UTF8_BOM.length);
        System.arraycopy(corpo, 0, saida, UTF8_BOM.length, corpo.length);
        return saida;
    }

    static String cabecalho() {
        return String.join(SEPARADOR,
                "Titulo",
                "Autor",
                "Interprete",
                "Tom",
                "Categoria",
                "Categoria rotulo",
                "Link da letra",
                "Link da cifra",
                "Link de referencia",
                "Observacao",
                "Ativo");
    }

    static String linha(Musica musica) {
        String categoria = musica.getCategoriaLiturgica();
        return String.join(SEPARADOR,
                campo(musica.getTitulo()),
                campo(musica.getAutor()),
                campo(musica.getInterpreteReferencia()),
                campo(musica.getTomPadrao()),
                campo(categoria),
                campo(categoria != null ? CategoriasLiturgicas.rotulo(categoria) : null),
                campo(musica.getLetra()),
                campo(musica.getCifra()),
                campo(musica.getLinkReferencia()),
                campo(musica.getObservacao()),
                campo(Boolean.TRUE.equals(musica.getAtivo()) ? "Sim" : "Não"));
    }

    static String campo(String valor) {
        if (valor == null) {
            return "";
        }
        boolean precisaAspas = valor.indexOf(';') >= 0
                || valor.indexOf('"') >= 0
                || valor.indexOf('\n') >= 0
                || valor.indexOf('\r') >= 0;
        String escapado = valor.replace("\"", "\"\"");
        return precisaAspas ? "\"" + escapado + "\"" : escapado;
    }
}
