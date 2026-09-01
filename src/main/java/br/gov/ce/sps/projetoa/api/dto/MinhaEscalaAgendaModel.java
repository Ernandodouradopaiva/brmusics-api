package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MinhaEscalaAgendaModel {

    private String nomeMusico;
    private MinhaEscalaItemModel proxima;
    private List<MinhaEscalaItemModel> proximas = new ArrayList<>();
    private List<MinhaEscalaItemModel> historico = new ArrayList<>();
}
