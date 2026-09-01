package br.gov.ce.sps.projetoa.domain.spec;

import br.gov.ce.sps.projetoa.domain.filter.MusicoFilter;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public final class MusicoSpec {

    private MusicoSpec() {
    }

    public static Specification<Musico> usandoFiltro(MusicoFilter filtro) {
        return (root, query, builder) -> {
            if (query != null && !Long.class.equals(query.getResultType()) && !long.class.equals(query.getResultType())) {
                root.fetch("usuario", JoinType.LEFT);
                query.distinct(true);
            }

            var predicates = new ArrayList<Predicate>();
            if (filtro == null) {
                return builder.and(predicates.toArray(new Predicate[0]));
            }

            if (StringUtils.hasText(filtro.getNome())) {
                String termo = filtro.getNome().trim().toLowerCase();
                predicates.add(builder.like(builder.lower(root.get("nome")), "%" + termo + "%"));
            }

            if (StringUtils.hasText(filtro.getTelefone())) {
                String digits = filtro.getTelefone().replaceAll("\\D", "");
                if (StringUtils.hasText(digits)) {
                    predicates.add(builder.or(
                            builder.like(root.get("telefone"), "%" + digits + "%"),
                            builder.like(root.get("whatsapp"), "%" + digits + "%")));
                }
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
