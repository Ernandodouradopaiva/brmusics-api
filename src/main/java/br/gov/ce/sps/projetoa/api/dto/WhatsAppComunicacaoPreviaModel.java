package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class WhatsAppComunicacaoPreviaModel {

    private UUID publicacaoCodigo;
    private Integer versao;
    private int ano;
    private int mes;
    private String competencia;
    private boolean jaComunicada;
    private List<WhatsAppComunicacaoItemModel> itens = new ArrayList<>();
}
