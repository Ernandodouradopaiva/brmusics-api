package br.gov.ce.sps.projetoa.domain.service.anexo;

import java.io.InputStream;

import lombok.Builder;
import lombok.Getter;

public interface AnexoStorageService {

	InputStream recuperar(String nomeArquivo);

	/** @return identificador persistido para leitura posterior (chave no MinIO ou caminho relativo local) */
	String armazenarCaminho(NovoAnexo novoAnexo, String caminhoRelativo);

	void removerCaminho(String nomeAnexo, String caminhoRelativo);

	void removerRelativoTotal(String caminhoRelativoTotal);

	void limparPasta(String caminho);

	@Builder
	@Getter
	class NovoAnexo {

		private String nomeArquivo;
		private InputStream inputStream;
	}
}
