package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EscalaInput {

    @NotNull
    private UUID celebracaoCodigo;

    @Valid
    private List<EscalaMusicoInput> participacoes = new ArrayList<>();
}
