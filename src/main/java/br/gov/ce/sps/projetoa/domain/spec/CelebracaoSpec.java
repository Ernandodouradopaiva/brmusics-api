package br.gov.ce.sps.projetoa.domain.spec;

import br.gov.ce.sps.projetoa.domain.filter.CelebracaoFilter;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public final class CelebracaoSpec {

    private CelebracaoSpec() {
    }

    public static Specification<Celebracao> usandoFiltro(CelebracaoFilter filtro) {
        return (root, query, builder) -> {
            if (query != null && !Long.class.equals(query.getResultType()) && !long.class.equals(query.getResultType())) {
                root.fetch("local", JoinType.LEFT);
                query.distinct(true);
            }

            var predicates = new ArrayList<Predicate>();
            if (filtro == null) {
                return builder.and(predicates.toArray(new Predicate[0]));
            }

            if (StringUtils.hasText(filtro.getTitulo())) {
                String termo = filtro.getTitulo().trim().toLowerCase();
                predicates.add(builder.like(builder.lower(root.get("titulo")), "%" + termo + "%"));
            }

            if (filtro.getAno() != null && filtro.getMes() != null) {
                LocalDate inicio = LocalDate.of(filtro.getAno(), filtro.getMes(), 1);
                LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
                predicates.add(builder.between(root.get("data"), inicio, fim));
            } else if (filtro.getAno() != null) {
                LocalDate inicio = LocalDate.of(filtro.getAno(), 1, 1);
                LocalDate fim = LocalDate.of(filtro.getAno(), 12, 31);
                predicates.add(builder.between(root.get("data"), inicio, fim));
            } else if (filtro.getMes() != null) {
                predicates.add(builder.equal(
                        builder.function("date_part", Double.class, builder.literal("month"), root.get("data")),
                        filtro.getMes().doubleValue()));
            }

            if (filtro.getLocalCodigo() != null) {
                predicates.add(builder.equal(root.get("local").get("codigo"), filtro.getLocalCodigo()));
            }

            if (filtro.getStatus() != null) {
                predicates.add(builder.equal(root.get("status"), filtro.getStatus()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
