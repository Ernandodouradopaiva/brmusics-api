package br.gov.ce.sps.projetoa.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EnderecoModelBasico {
	private String logradouro;
	private String logradouroNumero;
	private String complemento;
	private String cep;
	private BairroModelBasico bairro;
}