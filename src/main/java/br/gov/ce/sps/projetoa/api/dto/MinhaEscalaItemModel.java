package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MinhaEscalaItemModel {

    private UUID escalaCodigo;
    private UUID celebracaoCodigo;
    private String titulo;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String diaSemana;
    private String localNome;
    private String status;
    private String minhaFuncao;
    private UUID repertorioCodigo;
    private List<MinhaEscalaEquipeItemModel> equipe = new ArrayList<>();
}
