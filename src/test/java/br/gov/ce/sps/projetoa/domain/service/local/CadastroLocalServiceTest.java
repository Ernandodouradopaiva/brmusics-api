package br.gov.ce.sps.projetoa.domain.service.local;

import br.gov.ce.sps.projetoa.api.input.LocalInput;
import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Local;
import br.gov.ce.sps.projetoa.domain.repository.LocalRepository;
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
class CadastroLocalServiceTest {

    @Mock
    private LocalRepository localRepository;

    private CadastroLocalService service;

    @BeforeEach
    void setUp() {
        service = new CadastroLocalService(localRepository);
    }

    @Test
    void salvaLocalAtivo() {
        when(localRepository.existsByNomeIgnoreCase("Matriz")).thenReturn(false);
        when(localRepository.save(any(Local.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalInput input = new LocalInput();
        input.setNome("Matriz");
        input.setCidade("Fortaleza");

        Local salvo = service.salvar(input);

        assertThat(salvo.getNome()).isEqualTo("Matriz");
        assertThat(salvo.getCidade()).isEqualTo("Fortaleza");
        assertThat(salvo.getAtivo()).isTrue();
    }

    @Test
    void rejeitaNomeDuplicado() {
        when(localRepository.existsByNomeIgnoreCase("Capela")).thenReturn(true);
        LocalInput input = new LocalInput();
        input.setNome("Capela");

        assertThatThrownBy(() -> service.salvar(input))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Já existe um local cadastrado com este nome.");
    }
}
