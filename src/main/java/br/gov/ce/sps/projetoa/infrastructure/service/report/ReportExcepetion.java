package br.gov.ce.sps.projetoa.infrastructure.service.report;

public class ReportExcepetion extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReportExcepetion(String message) {
		super(message);
	}

	public ReportExcepetion(String message, Throwable cause) {
		super(message, cause);
	}
}