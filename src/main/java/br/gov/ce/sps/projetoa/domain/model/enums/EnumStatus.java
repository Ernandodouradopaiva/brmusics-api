package br.gov.ce.sps.projetoa.domain.model.enums;

import lombok.Getter;

public enum EnumStatus {
	A_ANALISAR("A ANALISAR"),
	EM_ABERTO("EM ABERTO"),
	EM_ANDAMENTO("EM ANDAMENTO"),
	A_VALIDAR("A VALIDAR"),
	FINALIZADA("FINALIZADA"),
	ARQUIVADA("ARQUIVADA"),
	DEVOLVIDA("DEVOLVIDA");
	
	@Getter
	private String descricao;
	
	EnumStatus(String descricao) {
		this.descricao = descricao;
	}
	
	public static EnumStatus findByDescricao(String descricao) {
		for(EnumStatus s: values()) {
			if(s.getDescricao().equals(descricao)) {
				return s;
			}
		}
		return null;
	}
}
