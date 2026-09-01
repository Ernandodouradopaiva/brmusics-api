package br.gov.ce.sps.projetoa.domain.service.celebracao;

import br.gov.ce.sps.projetoa.api.input.CelebracaoInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Celebracao;
import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.model.enums.CelebracaoStatus;
import br.gov.ce.sps.projetoa.domain.repository.CelebracaoRepository;
import br.gov.ce.sps.projetoa.domain.service.local.GetLocalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    void salvaComStatusRascunhoPorPadrao() {
        UUID localCodigo = UUID.randomUUID();
        Local local = new Local();
        local.setId(1L);
        local.setCodigo(localCodigo);
        local.setAtivo(true);

        when(getLocalService.findByCode(localCodigo)).thenReturn(local);
        when(celebracaoRepository.save(any(Celebracao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CelebracaoInput input = new CelebracaoInput();
        input.setLocalCodigo(localCodigo);
        input.setTitulo("Missa Dominical");
        input.setData(LocalDate.of(2026, 9, 6));
        input.setHoraInicio(LocalTime.of(19, 0));

        Celebracao salvo = service.salvar(input);

        assertThat(salvo.getTitulo()).isEqualTo("Missa Dominical");
        assertThat(salvo.getStatus()).isEqualTo(CelebracaoStatus.RASCUNHO);
        assertThat(salvo.getLocal()).isSameAs(local);
    }

    @Test
    void rejeitaLocalInativoEmNovaCelebracao() {
        UUID localCodigo = UUID.randomUUID();
        Local local = new Local();
        local.setId(2L);
        local.setCodigo(localCodigo);
        local.setAtivo(false);
        when(getLocalService.findByCode(localCodigo)).thenReturn(local);

        CelebracaoInput input = new CelebracaoInput();
        input.setLocalCodigo(localCodigo);
        input.setTitulo("Adoração");
        input.setData(LocalDate.of(2026, 9, 6));
        input.setHoraInicio(LocalTime.of(18, 0));

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Local inativo não pode ser usado em novas celebrações.");
    }

    @Test
    void rejeitaHoraFimAnteriorAoInicio() {
        CelebracaoInput input = new CelebracaoInput();
        input.setLocalCodigo(UUID.randomUUID());
        input.setTitulo("Missa");
        input.setData(LocalDate.of(2026, 9, 6));
        input.setHoraInicio(LocalTime.of(19, 0));
        input.setHoraFim(LocalTime.of(18, 0));

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("O horário de término deve ser posterior ao horário de início.");
    }
}
