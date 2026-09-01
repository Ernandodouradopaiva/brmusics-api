package br.gov.ce.sps.projetoa.api.dto;

import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.RepertorioStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RepertorioMensalItemModel {

    private UUID celebracaoCodigo;
    private String titulo;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String localNome;
    private String diaSemana;
    private CelebracaoStatus celebracaoStatus;
    private UUID repertorioCodigo;
    private RepertorioStatus repertorioStatus;
    private String observacao;
    private int quantidadeItens;
    private List<RepertorioItemModel> itens = new ArrayList<>();
}
