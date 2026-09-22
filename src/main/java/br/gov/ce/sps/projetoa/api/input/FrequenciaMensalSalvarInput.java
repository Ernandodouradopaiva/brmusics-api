package br.gov.ce.sps.projetoa.api.input;

import br.gov.ce.sps.projetoa.domain.model.enums.FrequenciaStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record FrequenciaMensalSalvarInput(
        @NotNull @Min(2000) @Max(2100) Integer ano,
        @NotNull @Min(1) @Max(12) Integer mes,
        @NotEmpty @Valid List<Item> itens) {

    public record Item(
            @NotNull UUID musicoCodigo,
            FrequenciaStatus semana1,
            FrequenciaStatus semana2,
            FrequenciaStatus semana3,
            FrequenciaStatus semana4) {}
}
