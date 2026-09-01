package br.gov.ce.sps.projetoa.domain.service.local;

import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.repository.LocalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletaLocalServiceTest {

    @Mock
    private LocalRepository localRepository;
    @Mock
    private CelebracaoRepository celebracaoRepository;
    @Mock
    private GetLocalService getLocalService;

    private DeletaLocalService service;

    @BeforeEach
    void setUp() {
        service = new DeletaLocalService(localRepository, celebracaoRepository, getLocalService);
    }

    @Test
    void inativaQuandoHaCelebracao() {
        UUID codigo = UUID.randomUUID();
        Local local = new Local();
        local.setId(1L);
        local.setCodigo(codigo);
        local.setAtivo(true);

        when(getLocalService.findByCode(codigo)).thenReturn(local);
        when(celebracaoRepository.existsByLocal_Id(1L)).thenReturn(true);

        service.deletar(codigo);

        assertThat(local.getAtivo()).isFalse();
        verify(localRepository).save(local);
        verify(localRepository, never()).delete(local);
    }
}
