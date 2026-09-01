package br.gov.ce.sps.projetoa.domain.service.instrumento;

import br.gov.ce.sps.projetoa.api.input.InstrumentoInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Instrumento;
import br.gov.ce.sps.projetoa.domain.repository.InstrumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastroInstrumentoServiceTest {

    @Mock
    private InstrumentoRepository instrumentoRepository;

    private CadastroInstrumentoService service;

    @BeforeEach
    void setUp() {
        service = new CadastroInstrumentoService(instrumentoRepository);
    }

    @Test
    void salvaComNomeEOrdemAutomatica() {
        when(instrumentoRepository.existsByNomeIgnoreCase("Violão")).thenReturn(false);
        when(instrumentoRepository.maxOrdem()).thenReturn(3);
        when(instrumentoRepository.save(any(Instrumento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InstrumentoInput input = new InstrumentoInput();
        input.setNome("Violão");

        Instrumento salvo = service.salvar(input);

        assertThat(salvo.getNome()).isEqualTo("Violão");
        assertThat(salvo.getAtivo()).isTrue();
        assertThat(salvo.getOrdem()).isEqualTo(4);
    }

    @Test
    void rejeitaNomeDuplicadoIgnorandoCaixa() {
        when(instrumentoRepository.existsByNomeIgnoreCase("Guitarra")).thenReturn(true);
        InstrumentoInput input = new InstrumentoInput();
        input.setNome("Guitarra");

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Já existe um instrumento cadastrado com este nome.");
    }
}
