package br.gov.ce.sps.projetoa.infrastructure.service.storage;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Mensagens claras quando o storage (MinIO) impede a subida da aplicação.
 */
public class StorageFailureAnalyzer extends AbstractFailureAnalyzer<StorageConfigurationException> {

	static final String ACAO_SUGERIDA =
			"Verifique MINIO_URL, credenciais e conectividade. "
					+ "Em desenvolvimento sem MinIO, use STORAGE_TYPE=local ou deixe MINIO_URL vazio "
					+ "para fallback em disco local.";

	@Override
	protected FailureAnalysis analyze(Throwable rootFailure, StorageConfigurationException cause) {
		return new FailureAnalysis(cause.getMessage(), ACAO_SUGERIDA, cause);
	}
}
