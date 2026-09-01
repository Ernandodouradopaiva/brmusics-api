package br.gov.ce.sps.projetoa.infrastructure.service.storage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MinioConfig {

	@Bean
	@Conditional(MinioStorageEnabledCondition.class)
	public MinioClient minioClient(MinioProperties props) {
		try {
			log.info("[STORAGE] Inicializando cliente MinIO em {}", props.getUrl());
			return MinioClient.builder()
					.endpoint(props.getUrl())
					.credentials(props.getAccessName(), props.getAccessSecret())
					.build();
		} catch (NoClassDefFoundError e) {
			throw wrapMinioInitFailure(props, e);
		} catch (Exception e) {
			throw wrapMinioInitFailure(props, e);
		}
	}

	private static StorageConfigurationException wrapMinioInitFailure(MinioProperties props, Throwable e) {
		String message = "Não foi possível inicializar o cliente MinIO em "
				+ props.getUrl()
				+ ". Verifique dependências, URL e credenciais.";
		log.error("[STORAGE] {}", message, e);
		return new StorageConfigurationException(message, e);
	}
}
