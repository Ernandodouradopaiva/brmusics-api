package br.gov.ce.sps.projetoa.infrastructure.service.storage;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * Ativa beans MinIO quando o modo não é {@code local} e {@code minio.url} está configurado.
 */
public class MinioStorageEnabledCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Environment env = context.getEnvironment();
		String type = env.getProperty("storage.type", "minio");
		if ("local".equalsIgnoreCase(type)) {
			return false;
		}
		return StringUtils.hasText(env.getProperty("minio.url"));
	}
}
