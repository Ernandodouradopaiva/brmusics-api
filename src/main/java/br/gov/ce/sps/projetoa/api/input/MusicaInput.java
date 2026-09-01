package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicaInput {

    @NotBlank
    @Size(max = 255)
    private String titulo;

    @Size(max = 255)
    private String autor;

    @Size(max = 255)
    private String interpreteReferencia;

    @Size(max = 20)
    private String tomPadrao;

    @Size(max = 40)
    private String categoriaLiturgica;

    private String letra;

    private String cifra;

    @Size(max = 2000)
    private String linkReferencia;

    @Size(max = 2000)
    private String observacao;

    private Boolean ativo;
}
