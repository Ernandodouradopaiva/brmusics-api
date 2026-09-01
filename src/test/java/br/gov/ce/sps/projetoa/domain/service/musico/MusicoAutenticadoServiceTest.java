package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.core.security.SecurityUtil;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import br.gov.ce.sps.projetoa.domain.model.Usuario;
import br.gov.ce.sps.projetoa.domain.repository.MusicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicoAutenticadoServiceTest {

    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private MusicoRepository musicoRepository;

    private MusicoAutenticadoService service;

    @BeforeEach
    void setUp() {
        service = new MusicoAutenticadoService(securityUtil, musicoRepository);
    }

    @Test
    void resolveMusicoPeloUsuarioAutenticado() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        Musico musico = new Musico();
        musico.setId(3L);
        musico.setNome("João Silva");
        musico.setAtivo(true);
        musico.setUsuario(usuario);
        when(securityUtil.getAuthenticatedUser()).thenReturn(Optional.of(usuario));
        when(musicoRepository.findByUsuario_Id(7L)).thenReturn(Optional.of(musico));

        assertThat(service.exigirMusicoVinculado()).isSameAs(musico);
    }

    @Test
    void rejeitaUsuarioSemMusicoVinculado() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        when(securityUtil.getAuthenticatedUser()).thenReturn(Optional.of(usuario));
        when(musicoRepository.findByUsuario_Id(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.exigirMusicoVinculado())
                .isInstanceOf(NegocioException.class)
                .hasMessage(MusicoAutenticadoService.MSG_SEM_VINCULO);
    }

    @Test
    void rejeitaAusenciaDeAutenticacao() {
        when(securityUtil.getAuthenticatedUser()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.exigirMusicoVinculado())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void rejeitaMusicoInativoMesmoComVinculo() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        Musico musico = new Musico();
        musico.setId(3L);
        musico.setAtivo(false);
        musico.setUsuario(usuario);
        when(securityUtil.getAuthenticatedUser()).thenReturn(Optional.of(usuario));
        when(musicoRepository.findByUsuario_Id(7L)).thenReturn(Optional.of(musico));

        assertThatThrownBy(() -> service.exigirMusicoVinculado())
                .isInstanceOf(NegocioException.class)
                .hasMessage(MusicoAutenticadoService.MSG_SEM_VINCULO);
    }
}
