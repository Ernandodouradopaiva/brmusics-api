package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RepertorioInput {

    @NotNull
    private UUID celebracaoCodigo;

    @Size(max = 2000)
    private String observacao;

    @Valid
    private List<RepertorioItemInput> itens = new ArrayList<>();
}
