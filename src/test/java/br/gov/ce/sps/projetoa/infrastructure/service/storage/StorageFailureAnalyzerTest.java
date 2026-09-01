package br.gov.ce.sps.projetoa.infrastructure.service.storage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.diagnostics.FailureAnalysis;

class StorageFailureAnalyzerTest {

	private final StorageFailureAnalyzer analyzer = new StorageFailureAnalyzer();

	@Test
	void deveGerarAnaliseClaraParaFalhaDeConfiguracao() {
		var cause = new StorageConfigurationException("MinIO indisponível");

		FailureAnalysis analysis = analyzer.analyze(cause);

		assertThat(analysis).isNotNull();
		assertThat(analysis.getDescription()).contains("MinIO indisponível");
		assertThat(analysis.getAction()).contains("STORAGE_TYPE=local");
	}
}
