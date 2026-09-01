package br.gov.ce.sps.projetoa.core.security.permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class NavigationCatalogRegistry {

    private static final List<NavigationNode> ROOTS = buildRoots();

    private NavigationCatalogRegistry() {}

    public static List<NavigationNode> getRoots() {
        return Collections.unmodifiableList(ROOTS);
    }

    private static List<NavigationNode> buildRoots() {
        List<NavigationNode> roots = new ArrayList<>();
        int o = 0;

        roots.add(recurso("inicio", "Página inicial", o++));
        roots.add(recurso("musico", "Músicos", o++));
        roots.add(recurso("instrumento", "Instrumentos e funções", o++));
        roots.add(recurso("musica", "Músicas", o++));
        roots.add(recurso("local", "Locais", o++));
        roots.add(recurso("celebracao", "Celebrações", o++));
        roots.add(recurso("escala", "Escalas", o++));
        roots.add(recurso("repertorio", "Repertórios", o++));
        roots.add(recurso("whatsapp", "WhatsApp", o++));
        roots.add(grupo("administracao", "Administração", o++, List.of(
                recurso("usuario", "Usuários", 0),
                recurso("grupo", "Grupos", 1),
                recurso("permissao", "Permissões", 2))));
        roots.add(grupo("relatorios", "Relatórios", o++, List.of(
                recurso("relatorio", "Relatórios", 0),
                recurso("relatorio-usuarios", "Usuários", 1))));

        return roots;
    }

    private static NavigationNode grupo(String id, String label, int ordem, List<NavigationNode> filhos) {
        return new NavigationNode(id, label, null, filhos, ordem);
    }

    private static NavigationNode recurso(String recurso, String label, int ordem) {
        return new NavigationNode(recurso, label, recurso, List.of(), ordem);
    }
}
