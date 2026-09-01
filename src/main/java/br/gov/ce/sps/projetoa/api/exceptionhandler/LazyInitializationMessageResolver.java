package br.gov.ce.sps.projetoa.api.exceptionhandler;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.LazyInitializationException;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mensagens amigáveis para {@link LazyInitializationException} ({@code open-in-view=false}).
 */
final class LazyInitializationMessageResolver {

    private static final Pattern PROXY_ENTITY =
            Pattern.compile("could not initialize proxy \\[([^#]+)#");

    private static final Map<String, String> MENSAGENS_POR_ENTIDADE = Map.of(
            "Grupo", "Não foi possível carregar as permissões do grupo. Atualize a página e tente novamente.",
            "Usuario", "Não foi possível carregar os vínculos do usuário (grupos). Atualize a página e tente novamente.",
            "Permissao", "Não foi possível carregar as permissões. Atualize a página e tente novamente."
    );

    private LazyInitializationMessageResolver() {
    }

    static String resolve(LazyInitializationException ex) {
        String entity = extrairNomeEntidade(ex.getMessage());
        String especifica = MENSAGENS_POR_ENTIDADE.get(entity);
        if (especifica != null) {
            return especifica;
        }
        return "Não foi possível carregar dados relacionados para montar a resposta. "
                + "Atualize a página e tente novamente; se persistir, contate o suporte.";
    }

    static String resolveFromThrowable(Throwable ex) {
        Throwable root = ExceptionUtils.getRootCause(ex);
        if (root instanceof LazyInitializationException lazy) {
            return resolve(lazy);
        }
        String msg = root != null ? root.getMessage() : ex.getMessage();
        if (msg != null && msg.toLowerCase(Locale.ROOT).contains("could not initialize proxy")) {
            String entity = extrairNomeEntidade(msg);
            String especifica = MENSAGENS_POR_ENTIDADE.get(entity);
            if (especifica != null) {
                return especifica;
            }
            return "Não foi possível carregar dados relacionados para montar a resposta. "
                    + "Atualize a página e tente novamente.";
        }
        return null;
    }

    private static String extrairNomeEntidade(String message) {
        if (message == null) {
            return "entidade";
        }
        Matcher matcher = PROXY_ENTITY.matcher(message);
        if (matcher.find()) {
            String fqcn = matcher.group(1).trim();
            int dot = fqcn.lastIndexOf('.');
            return dot >= 0 ? fqcn.substring(dot + 1) : fqcn;
        }
        return "entidade";
    }
}
