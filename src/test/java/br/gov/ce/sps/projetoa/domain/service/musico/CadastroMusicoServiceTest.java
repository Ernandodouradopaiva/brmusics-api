package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.api.input.MusicoInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import br.gov.ce.sps.projetoa.domain.service.usuario.GetUsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroMusicoServiceTest {

    @Mock
    private MusicoRepository musicoRepository;
    @Mock
    private GetUsuarioService getUsuarioService;
    @Mock
    private br.gov.ce.sps.projetoa.domain.service.instrumento.GetInstrumentoService getInstrumentoService;

    private CadastroMusicoService service;

    @BeforeEach
    void setUp() {
        service = new CadastroMusicoService(musicoRepository, getUsuarioService, getInstrumentoService);
    }

    @Test
    void salvaMusicoSemUsuarioNormalizandoWhatsapp() {
        when(musicoRepository.save(any(Musico.class))).thenAnswer(invocation -> invocation.getArgument(0));
        MusicoInput input = new MusicoInput();
        input.setNome("João Silva");
        input.setWhatsapp("(85) 99999-8888");

        Musico salvo = service.salvar(input);

        assertThat(salvo.getNome()).isEqualTo("João Silva");
        assertThat(salvo.getWhatsapp()).isEqualTo("85999998888");
        assertThat(salvo.getAtivo()).isTrue();
        assertThat(salvo.getUsuario()).isNull();
    }

    @Test
    void rejeitaUsuarioJaVinculadoAOutroMusico() {
        UUID usuarioCodigo = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setCodigo(usuarioCodigo);

        MusicoInput input = new MusicoInput();
        input.setNome("Maria");
        input.setWhatsapp("85988887777");
        input.setUsuarioCodigo(usuarioCodigo);

        when(getUsuarioService.findByCode(usuarioCodigo)).thenReturn(usuario);
        when(musicoRepository.existsByUsuario_Id(10L)).thenReturn(true);

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Este usuário já está vinculado a outro músico.");
    }

    @Test
    void rejeitaWhatsappDuplicado() {
        MusicoInput input = new MusicoInput();
        input.setNome("Pedro");
        input.setWhatsapp("85911112222");
        when(musicoRepository.existsByWhatsapp("85911112222")).thenReturn(true);

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Já existe um músico cadastrado com este WhatsApp.");
    }

    @Test
    void persisteVinculoOpcionalComUsuario() {
        UUID usuarioCodigo = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setCodigo(usuarioCodigo);

        MusicoInput input = new MusicoInput();
        input.setNome("Ana");
        input.setWhatsapp("85900001111");
        input.setUsuarioCodigo(usuarioCodigo);

        when(getUsuarioService.findByCode(usuarioCodigo)).thenReturn(usuario);
        when(musicoRepository.existsByUsuario_Id(7L)).thenReturn(false);
        when(musicoRepository.save(any(Musico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.salvar(input);

        ArgumentCaptor<Musico> captor = ArgumentCaptor.forClass(Musico.class);
        verify(musicoRepository).save(captor.capture());
        assertThat(captor.getValue().getUsuario()).isSameAs(usuario);
    }
}
