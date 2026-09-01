package br.gov.ce.sps.projetoa.domain.service;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Bairro;
import br.gov.ce.sps.projetoa.domain.model.Endereco;
import br.gov.ce.sps.projetoa.domain.service.bairro.GetBairroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AtualizaEnderecoService {

    private final GetBairroService getBairroService;

    public <T> void atualizarBairroSeNecessario(T entidade, Function<T, Endereco> getEnderecoFunction, Long bairroId) {
        if (bairroId == null) {
            return;
        }
        
        Endereco endereco = getEnderecoFunction.apply(entidade);
        if (endereco == null) {
            throw new NegocioException("Endereço não encontrado");
        }
        
        // Verificar se o bairro realmente mudou
        boolean bairroMudou = endereco.getBairro() == null 
                || !endereco.getBairro().getId().equals(bairroId);
        
        if (bairroMudou) {
            Bairro bairro = getBairroService.buscaBairroPorId(bairroId);
            endereco.setBairro(bairro);
        }
    }
}