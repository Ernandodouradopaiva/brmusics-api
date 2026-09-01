package br.gov.ce.sps.projetoa.domain.spec;

import br.gov.ce.sps.projetoa.domain.filter.WhatsAppEnvioFilter;
import br.gov.ce.sps.projetoa.domain.model.WhatsAppEnvio;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppEnvioStatus;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public final class WhatsAppEnvioSpec {

    private WhatsAppEnvioSpec() {
    }

    public static Specification<WhatsAppEnvio> usandoFiltro(WhatsAppEnvioFilter filtro) {
        return (root, query, builder) -> {
            if (query != null && !Long.class.equals(query.getResultType()) && !long.class.equals(query.getResultType())) {
                root.fetch("musico", JoinType.LEFT);
                query.distinct(true);
            }

            var predicates = new ArrayList<Predicate>();
            if (filtro == null) {
                return builder.and(predicates.toArray(new Predicate[0]));
            }

            if (filtro.getStatus() != null) {
                predicates.add(builder.equal(root.get("status"), filtro.getStatus()));
            }

            if (filtro.getTipoMensagem() != null) {
                predicates.add(builder.equal(root.get("tipoMensagem"), filtro.getTipoMensagem()));
            }

            if (StringUtils.hasText(filtro.getTelefone())) {
                String digits = filtro.getTelefone().replaceAll("\\D", "");
                if (StringUtils.hasText(digits)) {
                    predicates.add(builder.like(root.get("telefone"), "%" + digits + "%"));
                }
            }

            if (StringUtils.hasText(filtro.getMusico())) {
                String termo = filtro.getMusico().trim().toLowerCase();
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("musico").get("nome")), "%" + termo + "%"),
                        builder.like(builder.lower(root.get("musico").get("nomeArtistico")), "%" + termo + "%")));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<WhatsAppEnvio> somenteErros() {
        WhatsAppEnvioFilter filtro = new WhatsAppEnvioFilter();
        filtro.setStatus(WhatsAppEnvioStatus.ERRO);
        return usandoFiltro(filtro);
    }
}
