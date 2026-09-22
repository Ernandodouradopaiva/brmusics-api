package br.gov.ce.sps.projetoa.core.security;

public final class Permissoes {

    private Permissoes() {}

    public static final class Inicio {
        private Inicio() {}
        public static final String MENU = "inicio.menu";
        public static final String PAGINA = "inicio.pagina";
        public static final String LISTAR = "inicio.listar";
        public static final String VISUALIZAR = "inicio.visualizar";
    }

    public static final class Usuario {
        private Usuario() {}
        public static final String MENU = "usuario.menu";
        public static final String PAGINA = "usuario.pagina";
        public static final String LISTAR = "usuario.listar";
        public static final String VISUALIZAR = "usuario.visualizar";
        public static final String CRIAR = "usuario.criar";
        public static final String EDITAR = "usuario.editar";
        public static final String EXCLUIR = "usuario.excluir";
        public static final String ALTERAR_SENHA = "usuario.alterar-senha";
    }

    public static final class Grupo {
        private Grupo() {}
        public static final String MENU = "grupo.menu";
        public static final String PAGINA = "grupo.pagina";
        public static final String LISTAR = "grupo.listar";
        public static final String VISUALIZAR = "grupo.visualizar";
        public static final String CRIAR = "grupo.criar";
        public static final String EDITAR = "grupo.editar";
        public static final String EXCLUIR = "grupo.excluir";
        public static final String GERENCIAR_PERMISSOES = "grupo.gerenciar-permissoes";
    }

    public static final class Permissao {
        private Permissao() {}
        public static final String MENU = "permissao.menu";
        public static final String PAGINA = "permissao.pagina";
        public static final String LISTAR = "permissao.listar";
        public static final String VISUALIZAR = "permissao.visualizar";
    }

    public static final class Relatorio {
        private Relatorio() {}
        public static final String RECURSO = "relatorio";
        public static final String MENU = "relatorio.menu";
        public static final String PAGINA = "relatorio.pagina";
        public static final String LISTAR = "relatorio.listar";
        public static final String VISUALIZAR = "relatorio.visualizar";
    }

    public static final class RelatorioUsuarios {
        private RelatorioUsuarios() {}
        public static final String RECURSO = "relatorio-usuarios";
        public static final String MENU = "relatorio-usuarios.menu";
        public static final String PAGINA = "relatorio-usuarios.pagina";
        public static final String LISTAR = "relatorio-usuarios.listar";
        public static final String VISUALIZAR = "relatorio-usuarios.visualizar";
        public static final String GERAR = "relatorio-usuarios.gerar";
    }

    public static final class RelatorioMusicas {
        private RelatorioMusicas() {}
        public static final String PAGINA = "relatorio-musicas.pagina";
        public static final String GERAR = "relatorio-musicas.gerar";
    }

    public static final class RelatorioMusicos {
        private RelatorioMusicos() {}
        public static final String PAGINA = "relatorio-musicos.pagina";
        public static final String GERAR = "relatorio-musicos.gerar";
    }

    public static final class RelatorioMusicosEscala {
        private RelatorioMusicosEscala() {}
        public static final String PAGINA = "relatorio-musicos-escala.pagina";
        public static final String GERAR = "relatorio-musicos-escala.gerar";
    }

    public static final class RelatorioEscalas {
        private RelatorioEscalas() {}
        public static final String PAGINA = "relatorio-escalas.pagina";
        public static final String GERAR = "relatorio-escalas.gerar";
    }

    public static final class RelatorioRepertorios {
        private RelatorioRepertorios() {}
        public static final String PAGINA = "relatorio-repertorios.pagina";
        public static final String GERAR = "relatorio-repertorios.gerar";
    }

    public static final class RelatorioCelebracoesMes {
        private RelatorioCelebracoesMes() {}
        public static final String PAGINA = "relatorio-celebracoes-mes.pagina";
        public static final String GERAR = "relatorio-celebracoes-mes.gerar";
    }

    public static final class RelatorioMusicosFuncoes {
        private RelatorioMusicosFuncoes() {}
        public static final String PAGINA = "relatorio-musicos-funcoes.pagina";
        public static final String GERAR = "relatorio-musicos-funcoes.gerar";
    }

    public static final class Musico {
        private Musico() {}
        public static final String RECURSO = "musico";
        public static final String MENU = "musico.menu";
        public static final String PAGINA = "musico.pagina";
        public static final String LISTAR = "musico.listar";
        public static final String VISUALIZAR = "musico.visualizar";
        public static final String CRIAR = "musico.criar";
        public static final String EDITAR = "musico.editar";
        public static final String EXCLUIR = "musico.excluir";
    }

    public static final class Frequencia {
        private Frequencia() {}
        public static final String RECURSO = "frequencia";
        public static final String MENU = "frequencia.menu";
        public static final String PAGINA = "frequencia.pagina";
        public static final String LISTAR = "frequencia.listar";
        public static final String VISUALIZAR = "frequencia.visualizar";
        public static final String EDITAR = "frequencia.editar";
    }

    public static final class Instrumento {
        private Instrumento() {}
        public static final String RECURSO = "instrumento";
        public static final String MENU = "instrumento.menu";
        public static final String PAGINA = "instrumento.pagina";
        public static final String LISTAR = "instrumento.listar";
        public static final String VISUALIZAR = "instrumento.visualizar";
        public static final String CRIAR = "instrumento.criar";
        public static final String EDITAR = "instrumento.editar";
        public static final String EXCLUIR = "instrumento.excluir";
    }

    public static final class Local {
        private Local() {}
        public static final String RECURSO = "local";
        public static final String MENU = "local.menu";
        public static final String PAGINA = "local.pagina";
        public static final String LISTAR = "local.listar";
        public static final String VISUALIZAR = "local.visualizar";
        public static final String CRIAR = "local.criar";
        public static final String EDITAR = "local.editar";
        public static final String EXCLUIR = "local.excluir";
    }

    public static final class Celebracao {
        private Celebracao() {}
        public static final String RECURSO = "celebracao";
        public static final String MENU = "celebracao.menu";
        public static final String PAGINA = "celebracao.pagina";
        public static final String LISTAR = "celebracao.listar";
        public static final String VISUALIZAR = "celebracao.visualizar";
        public static final String CRIAR = "celebracao.criar";
        public static final String EDITAR = "celebracao.editar";
        public static final String EXCLUIR = "celebracao.excluir";
    }

    public static final class Escala {
        private Escala() {}
        public static final String RECURSO = "escala";
        public static final String MENU = "escala.menu";
        public static final String PAGINA = "escala.pagina";
        public static final String LISTAR = "escala.listar";
        public static final String VISUALIZAR = "escala.visualizar";
        public static final String CRIAR = "escala.criar";
        public static final String EDITAR = "escala.editar";
        public static final String EXCLUIR = "escala.excluir";
        public static final String PUBLICAR = "escala.publicar";
    }

    public static final class Musica {
        private Musica() {}
        public static final String RECURSO = "musica";
        public static final String MENU = "musica.menu";
        public static final String PAGINA = "musica.pagina";
        public static final String LISTAR = "musica.listar";
        public static final String VISUALIZAR = "musica.visualizar";
        public static final String CRIAR = "musica.criar";
        public static final String EDITAR = "musica.editar";
        public static final String EXCLUIR = "musica.excluir";
    }

    public static final class Repertorio {
        private Repertorio() {}
        public static final String RECURSO = "repertorio";
        public static final String MENU = "repertorio.menu";
        public static final String PAGINA = "repertorio.pagina";
        public static final String LISTAR = "repertorio.listar";
        public static final String VISUALIZAR = "repertorio.visualizar";
        public static final String CRIAR = "repertorio.criar";
        public static final String EDITAR = "repertorio.editar";
        public static final String EXCLUIR = "repertorio.excluir";
    }

    public static final class WhatsApp {
        private WhatsApp() {}
        public static final String RECURSO = "whatsapp";
        public static final String MENU = "whatsapp.menu";
        public static final String PAGINA = "whatsapp.pagina";
        public static final String LISTAR = "whatsapp.listar";
        public static final String VISUALIZAR = "whatsapp.visualizar";
        public static final String ENVIAR = "whatsapp.enviar";
        public static final String REENVIAR = "whatsapp.reenviar";
    }

    public static final class MinhaEscala {
        private MinhaEscala() {}
        public static final String RECURSO = "minha-escala";
        public static final String MENU = "minha-escala.menu";
        public static final String PAGINA = "minha-escala.pagina";
        public static final String LISTAR = "minha-escala.listar";
        public static final String VISUALIZAR = "minha-escala.visualizar";
    }

    public static final class MeuRepertorio {
        private MeuRepertorio() {}
        public static final String RECURSO = "meu-repertorio";
        public static final String MENU = "meu-repertorio.menu";
        public static final String PAGINA = "meu-repertorio.pagina";
        public static final String LISTAR = "meu-repertorio.listar";
        public static final String VISUALIZAR = "meu-repertorio.visualizar";
    }

}