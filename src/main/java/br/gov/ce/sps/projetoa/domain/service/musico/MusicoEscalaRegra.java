package br.gov.ce.sps.projetoa.domain.service.musico;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Musico;
import org.springframework.stereotype.Component;

@Component
public class MusicoEscalaRegra {

    public void assertPodeEntrarEmNovaEscala(Musico musico) {
        if (musico == null || !musico.podeSerEscalado()) {
            throw new NegocioException("Músico inativo não pode entrar em novas escalas.");
        }
    }
}
