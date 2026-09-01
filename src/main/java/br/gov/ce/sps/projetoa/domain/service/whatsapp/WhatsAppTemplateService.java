package br.gov.ce.sps.projetoa.domain.service.whatsapp;

import br.gov.ce.sps.projetoa.api.assembler.EscalaPublicacaoAssembler;
import br.gov.ce.sps.projetoa.domain.model.CategoriasLiturgicas;
import br.gov.ce.sps.projetoa.domain.model.enums.WhatsAppTipoMensagem;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class WhatsAppTemplateService {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final int MAX_MENSAGEM = 4000;
    private static final String RODAPE = "Acesse o BRMusic para consultar sua escala completa.";

    public String montar(WhatsAppTipoMensagem tipo, WhatsAppMensagemContexto contexto) {
        if (tipo == WhatsAppTipoMensagem.PUBLICACAO_ESCALA || tipo == WhatsAppTipoMensagem.LEMBRETE) {
            if (contexto != null && contexto.escalas() != null && !contexto.escalas().isEmpty()) {
                return montarEscalaMensal(tipo, contexto);
            }
        }
        return montarAlteracao(tipo, contexto);
    }

    public String competencia(int ano, int mes) {
        return EscalaPublicacaoAssembler.competencia(ano, mes);
    }

    String montarAlteracao(WhatsAppTipoMensagem tipo, WhatsAppMensagemContexto contexto) {
        String nome = nome(contexto);
        WhatsAppEscalaMensalBloco bloco = primeiroBloco(contexto);
        StringBuilder sb = new StringBuilder();
        sb.append(tituloAlteracao(tipo)).append("\n\n");
        sb.append("Olá, ").append(nome).append("!\n\n");
        sb.append(corpoAlteracao(tipo, bloco));
        if (incluirBloco(tipo) && bloco != null) {
            sb.append('\n').append(formatarBloco(bloco));
        }
        sb.append("\n\n").append(RODAPE);
        return truncar(sb.toString());
    }

    private static String tituloAlteracao(WhatsAppTipoMensagem tipo) {
        return switch (tipo) {
            case NOVA_ESCALA -> "🎵 BRMusic — Nova escala";
            case REMOCAO_ESCALA -> "🎵 BRMusic — Remoção da escala";
            case ALTERACAO_REPERTORIO -> "🎵 BRMusic — Repertório alterado";
            case CELEBRACAO_CANCELADA -> "🎵 BRMusic — Celebração cancelada";
            default -> "🎵 BRMusic — Alteração da escala";
        };
    }

    private static String corpoAlteracao(WhatsAppTipoMensagem tipo, WhatsAppEscalaMensalBloco bloco) {
        String data = bloco != null && bloco.data() != null ? DATA.format(bloco.data()) : "esta celebração";
        String titulo = bloco != null && StringUtils.hasText(bloco.celebracaoTitulo())
                ? bloco.celebracaoTitulo().trim()
                : "celebração";
        return switch (tipo) {
            case NOVA_ESCALA -> "Você foi adicionado(a) à escala:\n\n";
            case REMOCAO_ESCALA ->
                    "Você foi removido(a) da celebração de " + data + " (" + titulo + ").\n";
            case ALTERACAO_REPERTORIO ->
                    "O repertório da celebração de " + data + " foi alterado:\n\n";
            case CELEBRACAO_CANCELADA ->
                    "A celebração " + titulo + " de " + data + " foi cancelada.\n";
            default -> "Houve alteração na sua escala:\n\n";
        };
    }

    private static boolean incluirBloco(WhatsAppTipoMensagem tipo) {
        return tipo == WhatsAppTipoMensagem.NOVA_ESCALA
                || tipo == WhatsAppTipoMensagem.ALTERACAO_ESCALA
                || tipo == WhatsAppTipoMensagem.ALTERACAO_REPERTORIO;
    }

    private static WhatsAppEscalaMensalBloco primeiroBloco(WhatsAppMensagemContexto contexto) {
        if (contexto == null || contexto.escalas() == null || contexto.escalas().isEmpty()) {
            return null;
        }
        return contexto.escalas().getFirst();
    }

    String montarEscalaMensal(WhatsAppTipoMensagem tipo, WhatsAppMensagemContexto contexto) {
        String nome = nome(contexto);
        String competencia = competenciaDe(contexto);
        String titulo = tipo == WhatsAppTipoMensagem.LEMBRETE
                ? "🎵 BRMusic — Lembrete de " + competencia
                : tipo == WhatsAppTipoMensagem.ALTERACAO_ESCALA
                ? "🎵 BRMusic — Escala atualizada de " + competencia
                : "🎵 BRMusic — Escala de " + competencia;

        StringBuilder sb = new StringBuilder();
        sb.append(titulo).append("\n\n");
        sb.append("Olá, ").append(nome).append("!\n\n");
        sb.append(tipo == WhatsAppTipoMensagem.LEMBRETE
                ? "Lembrete da sua escala deste mês:\n\n"
                : "Confira sua escala deste mês:\n\n");

        List<WhatsAppEscalaMensalBloco> blocos = contexto.escalas();
        for (int i = 0; i < blocos.size(); i++) {
            if (i > 0) {
                sb.append("\n---\n\n");
            }
            sb.append(formatarBloco(blocos.get(i)));
        }
        sb.append("\n\n").append(RODAPE);
        return truncar(sb.toString());
    }

    private static String formatarBloco(WhatsAppEscalaMensalBloco bloco) {
        StringBuilder sb = new StringBuilder();
        sb.append("📅 ");
        if (bloco.data() != null) {
            sb.append(DATA.format(bloco.data()));
            sb.append(" — ").append(diaDaSemana(bloco.data()));
        }
        if (bloco.horaInicio() != null) {
            sb.append(" — ").append(HORA.format(bloco.horaInicio()));
        }
        sb.append('\n');
        sb.append("⛪ ").append(StringUtils.hasText(bloco.celebracaoTitulo()) ? bloco.celebracaoTitulo() : "Celebração");
        if (StringUtils.hasText(bloco.localNome())) {
            sb.append("\n📍 ").append(bloco.localNome().trim());
        }
        sb.append('\n');
        sb.append(emojiFuncao(bloco.funcoes())).append(" Função: ").append(formatarFuncoes(bloco.funcoes()));
        if (bloco.repertorio() != null && !bloco.repertorio().isEmpty()) {
            sb.append("\n\n🎼 Repertório:\n");
            for (WhatsAppRepertorioItemResumo item : bloco.repertorio()) {
                sb.append("• ").append(formatarRepertorio(item)).append('\n');
            }
        }
        return sb.toString().stripTrailing();
    }

    private static String formatarRepertorio(WhatsAppRepertorioItemResumo item) {
        String momento = CategoriasLiturgicas.rotulo(item.momentoLiturgico());
        if (!StringUtils.hasText(momento)) {
            momento = StringUtils.hasText(item.momentoLiturgico()) ? item.momentoLiturgico() : "Música";
        }
        String titulo = StringUtils.hasText(item.musicaTitulo()) ? item.musicaTitulo().trim() : "—";
        String linha = momento + ": " + titulo;
        if (StringUtils.hasText(item.tom())) {
            linha += " — Tom " + item.tom().trim();
        }
        return linha;
    }

    private static String formatarFuncoes(List<String> funcoes) {
        if (funcoes == null || funcoes.isEmpty()) {
            return "—";
        }
        return String.join(", ", funcoes);
    }

    private static String emojiFuncao(List<String> funcoes) {
        if (funcoes == null) {
            return "🎸";
        }
        String joined = String.join(" ", funcoes).toLowerCase(PT_BR);
        if (joined.contains("vocal") || joined.contains("canto") || joined.contains("voz")) {
            return "🎤";
        }
        if (joined.contains("teclado") || joined.contains("piano") || joined.contains("órgão") || joined.contains("orgao")) {
            return "🎹";
        }
        if (joined.contains("bateria")) {
            return "🥁";
        }
        return "🎸";
    }

    private static String diaDaSemana(java.time.LocalDate data) {
        DayOfWeek dia = data.getDayOfWeek();
        String nome = dia.getDisplayName(TextStyle.FULL, PT_BR);
        if (!StringUtils.hasText(nome)) {
            return "";
        }
        return nome.substring(0, 1).toUpperCase(PT_BR) + nome.substring(1);
    }

    private static String nome(WhatsAppMensagemContexto contexto) {
        if (contexto == null || !StringUtils.hasText(contexto.nomeMusico())) {
            return "músico";
        }
        return contexto.nomeMusico().trim();
    }

    private static String competenciaDe(WhatsAppMensagemContexto contexto) {
        if (contexto == null || !StringUtils.hasText(contexto.competencia())) {
            return "a competência atual";
        }
        return contexto.competencia();
    }

    private static String truncar(String texto) {
        if (texto == null || texto.length() <= MAX_MENSAGEM) {
            return texto;
        }
        String aviso = "\n\n(Mensagem resumida.)\n" + RODAPE;
        int limite = MAX_MENSAGEM - aviso.length();
        if (limite < 1) {
            return texto.substring(0, MAX_MENSAGEM);
        }
        return texto.substring(0, limite).stripTrailing() + aviso;
    }
}
