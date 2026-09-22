package br.gov.ce.sps.projetoa.domain.service.repertorio;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Repertorio;
import br.gov.ce.sps.projetoa.domain.model.RepertorioItem;
import br.gov.ce.sps.projetoa.domain.model.enums.RepertorioStatus;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioItemRepository;
import br.gov.ce.sps.projetoa.domain.repository.RepertorioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletaRepertorioServiceTest {

    @Mock
    private GetRepertorioService getRepertorioService;
    @Mock
    private RepertorioRepository repertorioRepository;
    @Mock
    private RepertorioItemRepository repertorioItemRepository;

    private DeletaRepertorioService service;

    @BeforeEach
    void setUp() {
        service = new DeletaRepertorioService(
                getRepertorioService, repertorioRepository, repertorioItemRepository);
    }

    @Test
    void excluiItensERepertorioEmRascunho() {
        UUID codigo = UUID.randomUUID();
        Repertorio repertorio = new Repertorio();
        repertorio.setId(1L);
        repertorio.setCodigo(codigo);
        repertorio.setStatus(RepertorioStatus.RASCUNHO);
        RepertorioItem item = new RepertorioItem();
        item.setAtivo(true);
        List<RepertorioItem> itens = List.of(item);

        when(getRepertorioService.findByCode(codigo)).thenReturn(repertorio);
        when(repertorioItemRepository.findByRepertorio_Id(1L)).thenReturn(itens);

        service.deletar(codigo);

        verify(repertorioItemRepository).deleteAll(itens);
        verify(repertorioRepository).delete(repertorio);
    }

    @Test
    void rejeitaExcluirPublicado() {
        UUID codigo = UUID.randomUUID();
        Repertorio repertorio = new Repertorio();
        repertorio.setStatus(RepertorioStatus.PUBLICADA);
        when(getRepertorioService.findByCode(codigo)).thenReturn(repertorio);

        assertThatThrownBy(() -> service.deletar(codigo))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Repertório publicado não pode ser excluído.");
    }
}
