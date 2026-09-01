package br.gov.ce.sps.projetoa.api.controller;

import br.gov.ce.sps.projetoa.api.assembler.GenericAssembler;
import br.gov.ce.sps.projetoa.api.disassembler.GenericDisassembler;
import br.gov.ce.sps.projetoa.api.dto.DashboardCoordenadorModel;
import br.gov.ce.sps.projetoa.api.dto.EstadoModelBasico;
import br.gov.ce.sps.projetoa.api.dto.MinhaEscalaAgendaModel;
import br.gov.ce.sps.projetoa.domain.model.Estado;
import br.gov.ce.sps.projetoa.domain.service.escala.MinhaEscalaService;
import br.gov.ce.sps.projetoa.domain.service.estado.CadastroEstadoService;
import br.gov.ce.sps.projetoa.domain.service.estado.GetEstadoService;
import br.gov.ce.sps.projetoa.domain.service.inicio.InicioDashboardService;
import br.gov.ce.sps.projetoa.domain.service.repertorio.MeuRepertorioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Slice HTTP sem {@code ProjetoAApplication} ({@code @EnableJpaRepositories})
 * para validar 401/403/@PreAuthorize sem o núcleo JWT/cookie.
 */
@WebMvcTest(controllers = {
        InicioController.class,
        MinhaEscalaController.class,
        MeuRepertorioController.class,
        EstadoController.class
})
@Import({
        RecursoSegurancaWebTest.TestSecurityConfig.class,
        InicioController.class,
        MinhaEscalaController.class,
        MeuRepertorioController.class,
        EstadoController.class
})
@TestPropertySource(properties = {
        "SERVER_PORT=0",
        "DB_URL=jdbc:h2:mem:seguranca",
        "DB_USERNAME=sa",
        "DB_PASSWORD=",
        "AUTH_COOKIE_SECURE=false",
        "JWT_SECRET=test-secret-key-for-unit-tests-only",
        "APP_CORS_ALLOWED_ORIGINS=http://localhost:3000",
        "APP_API_INTERNAL_URL=http://localhost:8080",
        "PROJETO_A_STORAGE_LOCAL_ANEXOS=/tmp/anexos",
        "STORAGE_TYPE=local",
        "MINIO_URL=",
        "MINIO_BUCKET=test",
        "MINIO_DEFAULT_FOLDER=test",
        "MINIO_ACCESS_NAME=test",
        "MINIO_ACCESS_SECRET=test",
        "SPRINGDOC_SWAGGER_UI_ENABLED=false"
})
class RecursoSegurancaWebTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            OAuth2ResourceServerAutoConfiguration.class,
            OAuth2ClientAutoConfiguration.class
    })
    static class SliceApplication {
    }

    @EnableWebSecurity
    @EnableMethodSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((request, response, exception) ->
                                    response.sendError(401))
                            .accessDeniedHandler((request, response, exception) ->
                                    response.sendError(403)));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InicioDashboardService inicioDashboardService;
    @MockitoBean
    private MinhaEscalaService minhaEscalaService;
    @MockitoBean
    private MeuRepertorioService meuRepertorioService;
    @MockitoBean
    private GetEstadoService getEstadoService;
    @MockitoBean
    private CadastroEstadoService cadastroEstadoService;
    @MockitoBean
    private GenericAssembler genericAssembler;
    @MockitoBean
    private GenericDisassembler genericDisassembler;

    @Test
    @WithAnonymousUser
    void dashboardSemAutenticacaoRetorna401() throws Exception {
        mockMvc.perform(get("/inicio/dashboard")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "minha-escala.listar")
    void dashboardSemPermissaoRetorna403() throws Exception {
        mockMvc.perform(get("/inicio/dashboard")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "inicio.listar")
    void dashboardComPermissaoRetorna200() throws Exception {
        when(inicioDashboardService.montar()).thenReturn(new DashboardCoordenadorModel());
        mockMvc.perform(get("/inicio/dashboard")).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void minhaEscalaSemAutenticacaoRetorna401() throws Exception {
        mockMvc.perform(get("/minha-escala")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "escala.listar")
    void minhaEscalaSemPermissaoDaAreaDoMusicoRetorna403() throws Exception {
        mockMvc.perform(get("/minha-escala")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "minha-escala.listar")
    void minhaEscalaComPermissaoRetorna200() throws Exception {
        when(minhaEscalaService.agenda()).thenReturn(new MinhaEscalaAgendaModel());
        mockMvc.perform(get("/minha-escala")).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void meuRepertorioSemAutenticacaoRetorna401() throws Exception {
        mockMvc.perform(get("/meu-repertorio")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "repertorio.listar")
    void meuRepertorioSemPermissaoDaAreaDoMusicoRetorna403() throws Exception {
        mockMvc.perform(get("/meu-repertorio")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "meu-repertorio.listar")
    void meuRepertorioComPermissaoRetorna200() throws Exception {
        when(meuRepertorioService.listar()).thenReturn(List.of());
        mockMvc.perform(get("/meu-repertorio")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "minha-escala.listar")
    void cadastroDeEstadoPorMusicoRetorna403() throws Exception {
        mockMvc.perform(post("/estados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ceará\",\"sigla\":\"CE\",\"ibge\":\"23\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "inicio.listar")
    void cadastroDeEstadoComPermissaoDeCoordenadorRetorna201() throws Exception {
        Estado estado = new Estado();
        when(genericDisassembler.toDomainObject(any(), eq(Estado.class))).thenReturn(estado);
        when(cadastroEstadoService.cadastrar(estado)).thenReturn(estado);
        when(genericAssembler.toModel(estado, EstadoModelBasico.class)).thenReturn(new EstadoModelBasico());

        mockMvc.perform(post("/estados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Ceará\",\"sigla\":\"CE\",\"ibge\":\"23\"}"))
                .andExpect(status().isCreated());
    }
}
