package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoTipo;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplicarCelebracaoMesServiceTest {

    @Mock
    private GetCelebracaoService getCelebracaoService;
    @Mock
    private CelebracaoRepository celebracaoRepository;

    private ReplicarCelebracaoMesService service;

    @BeforeEach
    void setUp() {
        service = new ReplicarCelebracaoMesService(getCelebracaoService, celebracaoRepository);
    }

    @Test
    void replicaSabadosParaOutroMes() {
        UUID codigo = UUID.randomUUID();
        UUID serie = UUID.randomUUID();
        Local local = new Local();
        local.setId(2L);
        local.setAtivo(true);

        Celebracao origem = new Celebracao();
        origem.setCodigo(codigo);
        origem.setTitulo("MISSA DO SÁBADO");
        origem.setData(LocalDate.of(2026, 9, 5));
        origem.setHoraInicio(LocalTime.of(18, 0));
        origem.setTipo(CelebracaoTipo.FIXA);
        origem.setSerieCodigo(serie);
        origem.setLocal(local);

        when(getCelebracaoService.findByCode(codigo)).thenReturn(origem);
        when(celebracaoRepository.existsByLocal_IdAndDataAndHoraInicio(eq(2L), any(), any())).thenReturn(false);
        when(celebracaoRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        CelebracaoCadastroResultado resultado = service.replicar(codigo, 2026, 10);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Celebracao>> captor = ArgumentCaptor.forClass(List.class);
        verify(celebracaoRepository).saveAll(captor.capture());
        List<Celebracao> geradas = captor.getValue();
        List<LocalDate> esperadas = CadastroCelebracaoService.datasDoDiaSemanaNoMes(DayOfWeek.SATURDAY, 2026, 10);

        assertThat(geradas).hasSize(esperadas.size());
        assertThat(geradas).allMatch(c -> c.getData().getMonthValue() == 10);
        assertThat(geradas).allMatch(c -> c.getSerieCodigo().equals(serie));
        assertThat(resultado.quantidadeGerada()).isEqualTo(esperadas.size());
    }

    @Test
    void rejeitaCelebracaoExtraordinaria() {
        UUID codigo = UUID.randomUUID();
        Celebracao origem = new Celebracao();
        origem.setTipo(CelebracaoTipo.EXTRAORDINARIA);
        origem.setData(LocalDate.of(2026, 9, 5));
        when(getCelebracaoService.findByCode(codigo)).thenReturn(origem);

        assertThatThrownBy(() -> service.replicar(codigo, 2026, 10))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("Somente celebrações fixas");
    }
}
