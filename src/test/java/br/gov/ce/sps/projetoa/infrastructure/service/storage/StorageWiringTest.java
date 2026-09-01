package br.gov.ce.sps.projetoa.infrastructure.service.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import br.gov.ce.sps.projetoa.domain.service.anexo.AnexoStorageService;
import br.gov.ce.sps.projetoa.domain.service.anexo.implem.LocalAnexoStorageService;

class StorageWiringTest {

	private final ApplicationContextRunner runner = new ApplicationContextRunner()
			.withBean(MinioProperties.class, () -> {
				MinioProperties p = new MinioProperties();
				p.setUrl("http://localhost:9000");
				p.setBucket("spscloud");
				p.setDefaultFolder("sistemas/projetoA");
				p.setAccessName("minio");
				p.setAccessSecret("minio123");
				return p;
			})
			.withUserConfiguration(
					MinioConfig.class,
					MinioAnexoStorageService.class,
					LocalAnexoStorageService.class)
			.withPropertyValues(
					"minio.url=http://localhost:9000",
					"minio.bucket=spscloud",
					"minio.defaultFolder=sistemas/projetoA",
					"minio.accessName=minio",
					"minio.accessSecret=minio123",
					"sps.projetoa.storage.local.anexos=" + System.getProperty("java.io.tmpdir") + "/projetoA-wiring");

	@Test
	void defaultDeveSerMinio() {
		runner.run(ctx -> {
			assertThat(ctx).hasSingleBean(AnexoStorageService.class);
			assertThat(ctx.getBean(AnexoStorageService.class)).isInstanceOf(MinioAnexoStorageService.class);
			assertThat(ctx).doesNotHaveBean(LocalAnexoStorageService.class);
		});
	}

	@Test
	void quandoStorageTypeLocalDeveUsarLocal() throws Exception {
		var dir = Files.createTempDirectory("projetoA-anexos-test");
		runner.withPropertyValues(
				"storage.type=local",
				"sps.projetoa.storage.local.anexos=" + dir.toAbsolutePath()
		).run(ctx -> {
			assertThat(ctx).hasSingleBean(AnexoStorageService.class);
			assertThat(ctx.getBean(AnexoStorageService.class)).isInstanceOf(LocalAnexoStorageService.class);
			assertThat(ctx).doesNotHaveBean(MinioAnexoStorageService.class);
		});
	}

	@Test
	void quandoMinioUrlVazioDeveUsarLocal() throws Exception {
		var dir = Files.createTempDirectory("projetoA-anexos-fallback");
		runner.withPropertyValues(
				"storage.type=minio",
				"minio.url=",
				"sps.projetoa.storage.local.anexos=" + dir.toAbsolutePath()
		).run(ctx -> {
			assertThat(ctx).hasSingleBean(AnexoStorageService.class);
			assertThat(ctx.getBean(AnexoStorageService.class)).isInstanceOf(LocalAnexoStorageService.class);
		});
	}
}
