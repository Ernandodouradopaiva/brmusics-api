package br.gov.ce.sps.projetoa.infrastructure.service.storage;

import java.io.InputStream;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import br.gov.ce.sps.projetoa.domain.service.anexo.AnexoStorageService;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Conditional(MinioStorageEnabledCondition.class)
public class MinioAnexoStorageService implements AnexoStorageService {

	private final MinioClient minioClient;
	private final MinioProperties props;

	@Override
	public String armazenarCaminho(NovoAnexo novoAnexo, String caminhoRelativo) {
		String objectName = null;
		try {
			objectName = objectName(caminhoRelativo, novoAnexo.getNomeArquivo());
			minioClient.putObject(
					PutObjectArgs.builder()
							.bucket(props.getBucket())
							.object(objectName)
							.stream(novoAnexo.getInputStream(), -1L, 10L * 1024 * 1024)
							.build());
			log.info("[MINIO] Arquivo salvo: bucket={}, object={}", props.getBucket(), objectName);
			return objectName;
		} catch (Exception e) {
			log.error(
					"[MINIO] Falha ao salvar arquivo. endpoint={}, bucket={}, object={}, motivo={}",
					props.getUrl(),
					props.getBucket(),
					objectName,
					e.getMessage(),
					e);
			throw new StorageException("Não foi possível armazenar arquivo no MinIO.", e);
		}
	}

	@Override
	public InputStream recuperar(String nomeArquivo) {
		String objectName = null;
		try {
			objectName = withDefaultFolder(nomeArquivo);
			return minioClient.getObject(
					GetObjectArgs.builder()
							.bucket(props.getBucket())
							.object(objectName)
							.build());
		} catch (Exception e) {
			log.error(
					"[MINIO] Falha ao recuperar arquivo. endpoint={}, bucket={}, object={}, motivo={}",
					props.getUrl(),
					props.getBucket(),
					objectName,
					e.getMessage(),
					e);
			throw new StorageException("Não foi possível recuperar arquivo no MinIO.", e);
		}
	}

	@Override
	public void removerCaminho(String nomeAnexo, String caminhoRelativo) {
		removerRelativoTotal(objectName(caminhoRelativo, nomeAnexo));
	}

	@Override
	public void removerRelativoTotal(String caminhoRelativoTotal) {
		String objectName = null;
		try {
			objectName = withDefaultFolder(caminhoRelativoTotal);
			minioClient.removeObject(
					RemoveObjectArgs.builder()
							.bucket(props.getBucket())
							.object(objectName)
							.build());
			log.debug("[MINIO] Arquivo removido: {}", objectName);
		} catch (Exception e) {
			log.error(
					"[MINIO] Falha ao remover arquivo. endpoint={}, bucket={}, object={}, motivo={}",
					props.getUrl(),
					props.getBucket(),
					objectName,
					e.getMessage(),
					e);
			throw new StorageException("Não foi possível excluir arquivo no MinIO.", e);
		}
	}

	@Override
	public void limparPasta(String caminho) {
		try {
			String prefix = withDefaultFolder(normalizePrefix(caminho));
			Iterable<Result<Item>> objects = minioClient.listObjects(
					ListObjectsArgs.builder()
							.bucket(props.getBucket())
							.prefix(prefix)
							.recursive(true)
							.build());
			for (Result<Item> obj : objects) {
				String key = obj.get().objectName();
				minioClient.removeObject(
						RemoveObjectArgs.builder()
								.bucket(props.getBucket())
								.object(key)
								.build());
			}
			log.debug("[MINIO] Pasta limpa: {}", prefix);
		} catch (Exception e) {
			throw new StorageException("Não foi possível limpar pasta no MinIO.", e);
		}
	}

	private String objectName(String caminhoRelativo, String nomeArquivo) {
		String rel = normalizePrefix(caminhoRelativo);
		String file = nomeArquivo == null ? "" : nomeArquivo.strip();
		String combined = rel.isEmpty() ? file : rel + "/" + file;
		return withDefaultFolder(combined);
	}

	private String withDefaultFolder(String objectName) {
		String obj = normalizePrefix(objectName);
		String folder = normalizePrefix(props.getDefaultFolder());
		if (folder.isEmpty()) {
			return obj;
		}
		if (obj.startsWith(folder + "/") || obj.equals(folder)) {
			return obj;
		}
		return folder + "/" + obj;
	}

	private static String normalizePrefix(String value) {
		if (value == null) {
			return "";
		}
		String v = value.strip();
		while (v.startsWith("/")) {
			v = v.substring(1);
		}
		while (v.endsWith("/")) {
			v = v.substring(0, v.length() - 1);
		}
		return v;
	}
}
