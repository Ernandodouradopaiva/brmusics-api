package br.gov.ce.sps.projetoa.core.security.permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PermissionCatalogRegistry {

    private static final List<PermissionDefinition> ALL = buildAll();

    private PermissionCatalogRegistry() {}

    public static List<PermissionDefinition> getAll() {
        return Collections.unmodifiableList(ALL);
    }

    private static List<PermissionDefinition> buildAll() {
        List<PermissionDefinition> list = new ArrayList<>();
        int order = 0;

        readOnlyUi(list, "geral", "inicio", "Página inicial", order);
        order += 20;

        crudUi(list, "administracao", "usuario", "Usuários", order);
        order += 20;
        list.add(def("usuario.alterar-senha", "administracao", "usuario", "alterar-senha", "Alterar senha de usuário", order++));
        order += 10;

        crudUi(list, "administracao", "grupo", "Grupos", order);
        order += 20;
        list.add(def("grupo.gerenciar-permissoes", "administracao", "grupo", "gerenciar-permissoes", "Gerenciar permissões do grupo", order++));
        order += 10;

        readOnlyUi(list, "administracao", "permissao", "Permissões", order);
        order += 20;

        readOnlyUi(list, "relatorios", "relatorio", "Relatórios", order);
        order += 20;
        readOnlyUi(list, "relatorios", "relatorio-usuarios", "Relatório de usuários", order);
        order += 10;
        list.add(def("relatorio-usuarios.gerar", "relatorios", "relatorio-usuarios", "gerar", "Gerar PDF — Relatório de usuários", order++));
        order += 10;

        crudUi(list, "brmusic", "musico", "Músicos", order);
        order += 20;
        crudUi(list, "brmusic", "instrumento", "Instrumentos e funções", order);
        order += 20;
        crudUi(list, "brmusic", "local", "Locais", order);
        order += 20;
        crudUi(list, "brmusic", "celebracao", "Celebrações", order);
        order += 20;
        crudUi(list, "brmusic", "escala", "Escalas", order);
        order += 20;
        list.add(def("escala.publicar", "brmusic", "escala", "publicar", "Publicar — Escalas", order++));
        order += 10;
        crudUi(list, "brmusic", "musica", "Músicas", order);
        order += 20;
        crudUi(list, "brmusic", "repertorio", "Repertórios", order);
        order += 20;
        readOnlyUi(list, "brmusic", "whatsapp", "WhatsApp", order);
        order += 10;
        list.add(def("whatsapp.enviar", "brmusic", "whatsapp", "enviar", "Enviar — WhatsApp", order++));
        list.add(def("whatsapp.reenviar", "brmusic", "whatsapp", "reenviar", "Reenviar — WhatsApp", order++));
        order += 10;
        readOnlyUi(list, "brmusic", "minha-escala", "Minha escala", order);
        order += 20;
        readOnlyUi(list, "brmusic", "meu-repertorio", "Meu repertório", order);

        return list;

    }

    private static void readOnlyUi(List<PermissionDefinition> list, String modulo, String recurso, String label, int baseOrder) {
        int o = baseOrder;
        list.add(def(recurso + ".menu", modulo, recurso, "menu", "Menu — " + label, o++));
        list.add(def(recurso + ".pagina", modulo, recurso, "pagina", "Página — " + label, o++));
        list.add(def(recurso + ".listar", modulo, recurso, "listar", "Listar — " + label, o++));
        list.add(def(recurso + ".visualizar", modulo, recurso, "visualizar", "Visualizar — " + label, o++));
    }

    private static void crudUi(List<PermissionDefinition> list, String modulo, String recurso, String label, int baseOrder) {
        int o = baseOrder;
        list.add(def(recurso + ".menu", modulo, recurso, "menu", "Menu — " + label, o++));
        list.add(def(recurso + ".pagina", modulo, recurso, "pagina", "Página — " + label, o++));
        list.add(def(recurso + ".listar", modulo, recurso, "listar", "Listar — " + label, o++));
        list.add(def(recurso + ".visualizar", modulo, recurso, "visualizar", "Visualizar — " + label, o++));
        list.add(def(recurso + ".criar", modulo, recurso, "criar", "Criar — " + label, o++));
        list.add(def(recurso + ".editar", modulo, recurso, "editar", "Editar — " + label, o++));
        list.add(def(recurso + ".excluir", modulo, recurso, "excluir", "Excluir — " + label, o++));
    }

    private static PermissionDefinition def(
            String chave, String modulo, String recurso, String acao, String descricao, int ordem) {
        return new PermissionDefinition(chave, modulo, recurso, acao, descricao, ordem, true);
    }
}
