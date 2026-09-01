package br.gov.ce.sps.projetoa.domain.model.enums;

import lombok.Getter;

public enum EtniaEnum {

	NAO_INFORMADO("NÃO INFORMADO"), 
	AMARELO("AMARELO"), 
	BRANCO("BRANCO"), 
	INDIGENA("INDIGENA"), 
	NEGRO("NEGRO"), 
	PARDO("PARDO");

	@Getter
	private String descricao;

	private EtniaEnum(String descricao) {
		this.descricao = descricao;
	}



}