package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class DashboardCelebracaoResumoModel {

    private UUID celebracaoCodigo;
    private UUID escalaCodigo;
    private String titulo;
    private LocalDate data;
    private LocalTime horaInicio;
    private String localNome;
    private int quantidadeMusicos;
    private boolean repertorioCompleto;
    private boolean escalaPublicada;
}
