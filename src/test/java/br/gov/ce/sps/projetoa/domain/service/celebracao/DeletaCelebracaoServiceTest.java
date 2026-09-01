package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.EscalaRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletaCelebracaoServiceTest {

    @Mock
    private CelebracaoRepository celebracaoRepository;
    @Mock
    private EscalaRepository escalaRepository;
    @Mock
    private RepertorioRepository repertorioRepository;
    @Mock
    private GetCelebracaoService getCelebracaoService;

    private DeletaCelebracaoService service;

    @BeforeEach
    void setUp() {
        service = new DeletaCelebracaoService(
                celebracaoRepository, escalaRepository, repertorioRepository, getCelebracaoService);
    }

    @Test
    void bloqueiaQuandoHaRepertorio() {
        UUID codigo = UUID.randomUUID();
        Celebracao celebracao = new Celebracao();
        celebracao.setId(4L);
        celebracao.setStatus(CelebracaoStatus.PUBLICADA);
        when(getCelebracaoService.findByCode(codigo)).thenReturn(celebracao);
        when(escalaRepository.existsByCelebracao_Id(4L)).thenReturn(false);
        when(repertorioRepository.existsByCelebracao_Id(4L)).thenReturn(true);

        assertThatThrownBy(() -> service.deletar(codigo))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Celebração com repertório não pode ser excluída para preservar o histórico.");
        verify(celebracaoRepository, never()).delete(celebracao);
    }
}
