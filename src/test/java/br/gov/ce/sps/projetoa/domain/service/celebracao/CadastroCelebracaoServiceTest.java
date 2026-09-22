package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.api.input.CelebracaoInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoTipo;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.service.local.GetLocalService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroCelebracaoServiceTest {

    @Mock
    private CelebracaoRepository celebracaoRepository;
    @Mock
    private GetLocalService getLocalService;

    private CadastroCelebracaoService service;

    @BeforeEach
    void setUp() {
        service = new CadastroCelebracaoService(celebracaoRepository, getLocalService);
    }

    @Test
    void salvaExtraordinariaComStatusRascunhoPorPadrao() {
        UUID localCodigo = UUID.randomUUID();
        Local local = localAtivo(localCodigo, 1L);

        when(getLocalService.findByCode(localCodigo)).thenReturn(local);
        when(celebracaoRepository.save(any(Celebracao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CelebracaoInput input = baseInput(localCodigo);
        CelebracaoCadastroResultado resultado = service.salvar(input);

        assertThat(resultado.referencia().getTitulo()).isEqualTo("Missa Dominical");
        assertThat(resultado.referencia().getStatus()).isEqualTo(CelebracaoStatus.RASCUNHO);
        assertThat(resultado.referencia().getTipo()).isEqualTo(CelebracaoTipo.EXTRAORDINARIA);
        assertThat(resultado.quantidadeGerada()).isEqualTo(1);
        assertThat(resultado.serieCodigo()).isNull();
        verify(celebracaoRepository, never()).saveAll(anyList());
    }

    @Test
    void geraSerieFixaParaTodosOsSabadosDoMes() {
        UUID localCodigo = UUID.randomUUID();
        Local local = localAtivo(localCodigo, 1L);
        when(getLocalService.findByCode(localCodigo)).thenReturn(local);
        when(celebracaoRepository.existsByLocal_IdAndDataAndHoraInicio(eq(1L), any(), any())).thenReturn(false);
        when(celebracaoRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        CelebracaoInput input = baseInput(localCodigo);
        input.setData(LocalDate.of(2026, 9, 5)); // sábado
        input.setHoraInicio(LocalTime.of(18, 0));
        input.setTipo(CelebracaoTipo.FIXA);

        CelebracaoCadastroResultado resultado = service.salvar(input);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Celebracao>> captor = ArgumentCaptor.forClass(List.class);
        verify(celebracaoRepository).saveAll(captor.capture());
        List<Celebracao> geradas = captor.getValue();

        List<LocalDate> esperadas = CadastroCelebracaoService.datasDoDiaSemanaNoMes(DayOfWeek.SATURDAY, 2026, 9);
        assertThat(geradas).hasSize(esperadas.size());
        assertThat(geradas).allMatch(c -> c.getTipo() == CelebracaoTipo.FIXA);
        assertThat(geradas).allMatch(c -> c.getSerieCodigo() != null);
        assertThat(geradas).allMatch(c -> c.getSerieCodigo().equals(resultado.serieCodigo()));
        assertThat(geradas).allMatch(c -> c.getData().getDayOfWeek() == DayOfWeek.SATURDAY);
        assertThat(geradas).allMatch(c -> c.getData().getMonthValue() == 9);
        assertThat(resultado.quantidadeGerada()).isEqualTo(esperadas.size());
        assertThat(resultado.referencia().getData()).isEqualTo(LocalDate.of(2026, 9, 5));
    }

    @Test
    void rejeitaLocalInativoEmNovaCelebracao() {
        UUID localCodigo = UUID.randomUUID();
        Local local = new Local();
        local.setId(2L);
        local.setCodigo(localCodigo);
        local.setAtivo(false);
        when(getLocalService.findByCode(localCodigo)).thenReturn(local);

        CelebracaoInput input = baseInput(localCodigo);
        input.setTitulo("Adoração");
        input.setHoraInicio(LocalTime.of(18, 0));

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Local inativo não pode ser usado em novas celebrações.");
    }

    @Test
    void rejeitaHoraFimAnteriorAoInicio() {
        CelebracaoInput input = baseInput(UUID.randomUUID());
        input.setTitulo("Missa");
        input.setHoraFim(LocalTime.of(18, 0));

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("O horário de término deve ser posterior ao horário de início.");
    }

    private static CelebracaoInput baseInput(UUID localCodigo) {
        CelebracaoInput input = new CelebracaoInput();
        input.setLocalCodigo(localCodigo);
        input.setTitulo("Missa Dominical");
        input.setData(LocalDate.of(2026, 9, 6));
        input.setHoraInicio(LocalTime.of(19, 0));
        return input;
    }

    private static Local localAtivo(UUID codigo, Long id) {
        Local local = new Local();
        local.setId(id);
        local.setCodigo(codigo);
        local.setAtivo(true);
        return local;
    }
}
