package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DashboardCoordenadorModel {

    private DashboardCelebracaoResumoModel proximaCelebracao;
    private int musicosEscaladosProxima;
    private int escalasRascunho;
    private int whatsappPendentes;
    private int whatsappErros;
    private int repertoriosIncompletos;
    private int confirmacoesPendentes;
    private List<String> alertas = new ArrayList<>();
    private List<DashboardCelebracaoResumoModel> proximasCelebracoes = new ArrayList<>();
}
