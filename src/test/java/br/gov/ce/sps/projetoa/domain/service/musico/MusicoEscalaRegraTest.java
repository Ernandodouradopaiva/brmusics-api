package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MusicoEscalaRegraTest {

    private final MusicoEscalaRegra regra = new MusicoEscalaRegra();

    @Test
    void musicoInativoNaoPodeEntrarEmNovaEscala() {
        Musico musico = new Musico();
        musico.setAtivo(false);

        assertThatThrownBy(() -> regra.assertPodeEntrarEmNovaEscala(musico))
                .isInstanceOf(NegocioException.class)
                .hasMessage("Músico inativo não pode entrar em novas escalas.");
    }
}
