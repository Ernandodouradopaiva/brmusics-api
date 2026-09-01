package br.gov.ce.sps.projetoa.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class InscritoEncaminhadoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InscritoEncaminhadoException(String mensagem) {
		super(mensagem);
	}
}