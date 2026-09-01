package br.gov.ce.sps.projetoa.infrastructure.service.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

	private String url;
	private String bucket;
	private String defaultFolder;
	private String accessName;
	private String accessSecret;
}
