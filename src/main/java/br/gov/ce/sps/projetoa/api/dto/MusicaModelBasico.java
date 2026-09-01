package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MusicaModelBasico {
    private Long id;
    private UUID codigo;
    private String titulo;
    private String autor;
    private String interpreteReferencia;
    private String tomPadrao;
    private String categoriaLiturgica;
    private String categoriaLiturgicaRotulo;
    private String letra;
    private String cifra;
    private String linkReferencia;
    private String observacao;
    private Boolean ativo;
}
