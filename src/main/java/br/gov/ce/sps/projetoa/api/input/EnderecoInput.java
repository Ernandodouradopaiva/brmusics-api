package br.gov.ce.sps.projetoa.api.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoInput {

	@NotBlank(message = "Logradouro é obrigatório")
	@Size(max = 255, message = "Logradouro deve ter no máximo 255 caracteres")
	private String logradouro;
	
	@Size(max = 20, message = "Número do logradouro deve ter no máximo 20 caracteres")
	private String logradouroNumero;
	
	@Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
	private String complemento;
	
	@Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP deve estar no formato 00000-000")
	@NotBlank(message = "CEP é obrigatório")
	private String cep;
	
	@NotNull(message = "Bairro é obrigatório")
	@Valid
	private GenericIdInput bairro;
}