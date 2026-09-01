package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RepertorioItemInput {

    private UUID codigo;

    @NotNull
    private UUID musicaCodigo;

    @NotBlank
    @Size(max = 40)
    private String momentoLiturgico;

    private Integer ordem;

    @Size(max = 20)
    private String tom;

    @Size(max = 2000)
    private String observacao;
}
