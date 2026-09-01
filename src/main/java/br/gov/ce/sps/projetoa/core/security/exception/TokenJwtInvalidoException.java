package br.gov.ce.sps.projetoa.core.security.exception;

public class TokenJwtInvalidoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TokenJwtInvalidoException(String mensagem) {
		super(mensagem);
	}

	public TokenJwtInvalidoException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
