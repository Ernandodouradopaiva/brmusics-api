package br.gov.ce.sps.projetoa.domain.spec;

import br.gov.ce.sps.projetoa.domain.filter.MusicaFilter;
import br.gov.ce.sps.projetoa.domain.model.Musica;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public final class MusicaSpec {

    private MusicaSpec() {
    }

    public static Specification<Musica> usandoFiltro(MusicaFilter filtro) {
        return (root, query, builder) -> {
            var predicates = new ArrayList<Predicate>();
            if (filtro == null) {
                return builder.and(predicates.toArray(new Predicate[0]));
            }

            if (StringUtils.hasText(filtro.getTitulo())) {
                String termo = filtro.getTitulo().trim().toLowerCase();
                predicates.add(builder.like(builder.lower(root.get("titulo")), "%" + termo + "%"));
            }

            if (StringUtils.hasText(filtro.getAutor())) {
                String termo = filtro.getAutor().trim().toLowerCase();
                predicates.add(builder.like(builder.lower(root.get("autor")), "%" + termo + "%"));
            }

            if (StringUtils.hasText(filtro.getCategoria())) {
                predicates.add(builder.equal(root.get("categoriaLiturgica"), filtro.getCategoria().trim().toUpperCase()));
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
