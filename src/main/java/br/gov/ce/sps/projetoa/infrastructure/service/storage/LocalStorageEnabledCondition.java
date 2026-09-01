package br.gov.ce.sps.projetoa.infrastructure.service.storage;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * Ativa storage local quando {@code storage.type=local}, ou quando o modo é MinIO mas
 * {@code minio.url} não está definido (fallback para desenvolvimento sem MinIO).
 */
public class LocalStorageEnabledCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Environment env = context.getEnvironment();
		String type = env.getProperty("storage.type", "minio");
		if ("local".equalsIgnoreCase(type)) {
			return true;
		}
		return !StringUtils.hasText(env.getProperty("minio.url"));
	}
}
