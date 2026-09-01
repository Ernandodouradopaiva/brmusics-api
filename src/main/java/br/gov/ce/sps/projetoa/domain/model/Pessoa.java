package br.gov.ce.sps.projetoa.domain.model;

import br.gov.ce.sps.projetoa.domain.model.generic.AbstractEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.br.CPF;

import java.time.OffsetDateTime;

@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@MappedSuperclass
public class Pessoa extends AbstractEntity {
    
	private String nome;

    private OffsetDateTime dataNascimento;

    @CPF
    private String cpf;

    @Embedded
    private Endereco endereco;

    @Embedded
    private Contato contato;

    public OffsetDateTime getDataDeNascimento() {
        return dataNascimento;
    }

}
