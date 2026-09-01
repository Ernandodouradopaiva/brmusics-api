package br.gov.ce.sps.projetoa.domain.model.generic;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
	private Long id;

	private UUID codigo;

	@CreationTimestamp
	@Column(name = "data_cadastro")
	private OffsetDateTime dataCadastro;

	@UpdateTimestamp
	@Column(name = "data_atualizacao")
	private OffsetDateTime dataAtualizacao;

	@PrePersist
	private void gerarCodigo() {
		normalizarCamposTexto();
		setCodigo(UUID.randomUUID());
	}

	@jakarta.persistence.PreUpdate
	private void antesDeAtualizar() {
		normalizarCamposTexto();
	}

	private void normalizarCamposTexto() {
		normalizarObjeto(this);
	}

	private static void normalizarObjeto(Object target) {
		if (target == null) {
			return;
		}
		Class<?> current = target.getClass();
		while (current != null && current != Object.class) {
			for (Field field : current.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				field.setAccessible(true);
				try {
					Object value = field.get(target);
					if (value == null) {
						continue;
					}
					if (value instanceof String texto) {
						if ("senha".equals(field.getName())) {
							continue;
						}
						if ("descricao".equals(field.getName())
								|| "observacao".equals(field.getName())
								|| "telefone".equals(field.getName())
								|| "whatsapp".equals(field.getName())
								|| "endereco".equals(field.getName())
								|| "letra".equals(field.getName())
								|| "cifra".equals(field.getName())
								|| "linkReferencia".equals(field.getName())
								|| "caminhoRelativo".equals(field.getName())
								|| "nomeOriginal".equals(field.getName())
								|| "tomPadrao".equals(field.getName())
								|| "tom".equals(field.getName())
								|| "mensagem".equals(field.getName())
								|| "erro".equals(field.getName())
								|| "providerMessageId".equals(field.getName())
								|| "chaveIdempotencia".equals(field.getName())) {
							continue;
						}
						if ("chave".equals(field.getName())
								|| "modulo".equals(field.getName())
								|| "recurso".equals(field.getName())
								|| "acao".equals(field.getName())) {
							continue;
						}
						if ("email".equals(field.getName())) {
							field.set(target, texto.toLowerCase(Locale.ROOT));
							continue;
						}
						if ("fotoCaminhoRelativo".equals(field.getName())) {
							continue;
						}
						if ("fotoContentType".equals(field.getName())
								|| "contentType".equals(field.getName())) {
							field.set(target, texto.toLowerCase(Locale.ROOT));
							continue;
						}
						if ("geometria".equals(field.getName())) {
							continue;
						}
						field.set(target, texto.toUpperCase(Locale.ROOT));
						continue;
					}
					if (field.getType().isAnnotationPresent(Embeddable.class)) {
						normalizarObjeto(value);
					}
				} catch (IllegalAccessException e) {
					throw new IllegalStateException("Erro ao normalizar campos textuais da entidade", e);
				}
			}
			current = current.getSuperclass();
		}
	}
}
