package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CopiarEscalaInput {

    @NotNull
    private UUID origemCelebracaoCodigo;

    @NotNull
    private UUID destinoCelebracaoCodigo;
}
