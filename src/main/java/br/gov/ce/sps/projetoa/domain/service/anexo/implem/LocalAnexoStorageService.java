package br.gov.ce.sps.projetoa.domain.service.anexo.implem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import br.gov.ce.sps.projetoa.domain.service.anexo.AnexoStorageService;
import br.gov.ce.sps.projetoa.infrastructure.service.storage.LocalStorageEnabledCondition;
import br.gov.ce.sps.projetoa.infrastructure.service.storage.StorageException;
import jakarta.annotation.PostConstruct;

@Service
@Conditional(LocalStorageEnabledCondition.class)
public class LocalAnexoStorageService implements AnexoStorageService {

	@Value("${sps.brmusics.storage.local.anexos}")
	private String diretorioAnexosStr;

	private Path diretorioAnexos;

	@PostConstruct
	void init() {
		this.diretorioAnexos = Paths.get(diretorioAnexosStr).toAbsolutePath().normalize();
		try {
			Files.createDirectories(diretorioAnexos);
		} catch (IOException e) {
			throw new StorageException("Não foi possível criar o diretório de anexos: " + e.getMessage(), e);
		}
	}

	@Override
	public String armazenarCaminho(NovoAnexo novoAnexo, String caminhoRelativo) {
		try {
			String nomeArquivo = Objects.requireNonNull(novoAnexo.getNomeArquivo(), "nomeArquivo").strip();
			String caminhoRelativoTotal = joinPath(caminhoRelativo, nomeArquivo);
			Path diretorioPath = getAbsolutPath(caminhoRelativo);
			Files.createDirectories(diretorioPath);
			Path arquivoPath = diretorioPath.resolve(nomeArquivo).normalize();
			if (!arquivoPath.startsWith(diretorioAnexos.normalize())) {
				throw new StorageException("Caminho do arquivo inválido.");
			}
			try (OutputStream out = Files.newOutputStream(arquivoPath)) {
				FileCopyUtils.copy(
						Objects.requireNonNull(novoAnexo.getInputStream(), "inputStream"),
						Objects.requireNonNull(out, "out"));
			}
			return caminhoRelativoTotal;
		} catch (Exception e) {
			throw new StorageException("Não foi possível armazenar o arquivo " + e.getMessage(), e);
		}
	}

	@Override
	public InputStream recuperar(String nomeArquivo) {
		try {
			Path arquivoPath = getAbsolutPath(nomeArquivo);
			return Files.newInputStream(arquivoPath);
		} catch (Exception e) {
			throw new StorageException("Não foi possível recuperar arquivo " + e.getMessage(), e);
		}
	}

	@Override
	public void removerCaminho(String nomeAnexo, String caminhoRelativo) {
		try {
			Path path = diretorioAnexos.resolve(Path.of(caminhoRelativo)).resolve(nomeAnexo).normalize();
			if (!path.startsWith(diretorioAnexos.normalize())) {
				throw new StorageException("Caminho inválido.");
			}
			Files.deleteIfExists(path);
		} catch (IOException e) {
			throw new StorageException("Não foi possível remover o arquivo: " + e.getMessage(), e);
		}
	}

	@Override
	public void removerRelativoTotal(String caminhoRelativoTotal) {
		removerArquivoRelativoTotal(caminhoRelativoTotal);
	}

	@Override
	public void limparPasta(String caminho) {
		try {
			Path diretorioPath = getAbsolutPath(caminho);
			if (!diretorioPath.startsWith(diretorioAnexos.normalize())) {
				throw new StorageException("Caminho inválido.");
			}
			if (Files.isDirectory(diretorioPath)) {
				FileUtils.deleteDirectory(diretorioPath.toFile());
			}
		} catch (Exception e) {
			throw new StorageException("Não foi possível excluir pasta " + e.getMessage(), e);
		} finally {
			limparDiretoriosVazios();
		}
	}

	private Path getAbsolutPath(String nomeArquivo) {
		return diretorioAnexos.resolve(Path.of(nomeArquivo)).normalize();
	}

	public void removerArquivoRelativoTotal(String caminhoRelativo) {
		try {
			Path path = diretorioAnexos.resolve(Path.of(caminhoRelativo)).normalize();
			if (!path.startsWith(diretorioAnexos.normalize())) {
				throw new StorageException("Caminho inválido.");
			}
			Files.deleteIfExists(path);
		} catch (IOException e) {
			throw new StorageException("Não foi possível remover o arquivo: " + e.getMessage(), e);
		}
	}

	private void limparDiretoriosVazios() {
		if (!Files.isDirectory(diretorioAnexos)) {
			return;
		}
		try {
			Path raiz = diretorioAnexos.normalize();
			Files.walk(raiz)
					.sorted(Comparator.reverseOrder())
					.filter(p -> !p.equals(raiz))
					.map(Path::toFile)
					.filter(File::isDirectory)
					.forEach(f -> {
						String[] list = f.list();
						if (list != null && list.length == 0) {
							f.delete();
						}
					});
		} catch (Exception e) {
			throw new StorageException("Não foi possível remover diretórios vazios: " + e.getMessage(), e);
		}
	}

	private static String joinPath(String pasta, String nomeArquivo) {
		String dir = pasta == null ? "" : pasta.strip().replaceAll("/+$", "");
		if (dir.isEmpty()) {
			return nomeArquivo;
		}
		return dir + "/" + nomeArquivo;
	}
}
