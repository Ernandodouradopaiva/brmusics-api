package br.gov.ce.sps.projetoa.api.dto;

import br.gov.ce.sps.projetoa.domain.model.enums.FrequenciaStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class FrequenciaMensalPreviaModel {

    private int ano;
    private int mes;
    private String competencia;
    private List<FrequenciaMensalItemModel> itens = new ArrayList<>();

    @Getter
    @Setter
    public static class FrequenciaMensalItemModel {
        private UUID codigo;
        private UUID musicoCodigo;
        private String musicoNome;
        private Boolean musicoAtivo;
        private FrequenciaStatus semana1;
        private FrequenciaStatus semana2;
        private FrequenciaStatus semana3;
        private FrequenciaStatus semana4;
    }
}
