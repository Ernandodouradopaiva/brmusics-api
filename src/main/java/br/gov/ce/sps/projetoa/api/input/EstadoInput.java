package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoInput {

    @NotBlank(message = "Nome do estado é obrigatório")
    private String nome;

    @NotBlank(message = "Sigla do estado é obrigatória")
    private String sigla;

    @NotBlank(message = "Código IBGE do estado é obrigatório")
    private String ibge;
}