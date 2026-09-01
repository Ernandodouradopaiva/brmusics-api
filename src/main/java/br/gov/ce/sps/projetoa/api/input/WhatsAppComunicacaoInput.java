package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WhatsAppComunicacaoInput(
        @NotNull(message = "Informe o ano.") Integer ano,
        @NotNull(message = "Informe o mês.") @Min(1) @Max(12) Integer mes) {
}
