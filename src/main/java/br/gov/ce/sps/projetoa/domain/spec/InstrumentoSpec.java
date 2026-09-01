package br.gov.ce.sps.projetoa.domain.spec;

import br.gov.ce.sps.projetoa.domain.filter.InstrumentoFilter;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public final class InstrumentoSpec {

    private InstrumentoSpec() {
    }

    public static Specification<Instrumento> usandoFiltro(InstrumentoFilter filtro) {
        return (root, query, builder) -> {
            var predicates = new ArrayList<Predicate>();
            if (filtro == null) {
                return builder.and(predicates.toArray(new Predicate[0]));
            }

            if (StringUtils.hasText(filtro.getNome())) {
                String termo = filtro.getNome().trim().toLowerCase();
                predicates.add(builder.like(builder.lower(root.get("nome")), "%" + termo + "%"));
            }

            if (filtro.getAtivo() != null) {
                if (filtro.getAtivo()) {
                    predicates.add(builder.isTrue(root.get("ativo")));
                } else {
                    predicates.add(builder.isFalse(root.get("ativo")));
                }
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
