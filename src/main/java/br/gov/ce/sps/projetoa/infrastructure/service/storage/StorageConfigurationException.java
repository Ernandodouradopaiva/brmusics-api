package br.gov.ce.sps.projetoa.infrastructure.service.storage;

/**
 * Falha na configuração do storage (ex.: MinIO indisponível na subida da aplicação).
 */
public class StorageConfigurationException extends RuntimeException {

	public StorageConfigurationException(String message) {
		super(message);
	}

	public StorageConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
