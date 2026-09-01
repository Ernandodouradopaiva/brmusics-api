package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RepertorioItemModel {

    private UUID codigo;
    private UUID musicaCodigo;
    private String musicaTitulo;
    private String musicaAutor;
    private String tomPadraoMusica;
    private String momentoLiturgico;
    private String momentoLiturgicoRotulo;
    private Integer ordem;
    private String tom;
    private String observacao;
}
