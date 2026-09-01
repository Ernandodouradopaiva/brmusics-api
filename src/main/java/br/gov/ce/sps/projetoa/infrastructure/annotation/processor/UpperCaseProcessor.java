package br.gov.ce.sps.projetoa.infrastructure.annotation.processor;

import java.lang.reflect.Field;

import br.gov.ce.sps.projetoa.infrastructure.annotation.UpperCase;

public class UpperCaseProcessor {

	public static void process(Object obj) throws IllegalAccessException {
		Class<?> clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(UpperCase.class)) {
				field.setAccessible(true);
				Object value = field.get(obj);
				if (value instanceof String) {
					String originalValue = (String) value;
					String upperCaseValue = originalValue.toUpperCase();
					field.set(obj, upperCaseValue);
				}
			}
		}
	}
}