package br.gov.ce.sps.projetoa.domain.service.bairro;

import br.gov.ce.sps.projetoa.domain.exception.EntidadeNaoEncontradaException;
import br.gov.ce.sps.projetoa.domain.model.Bairro;
import br.gov.ce.sps.projetoa.domain.repository.BairroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBairroService {

    private final BairroRepository bairroRepository;

    public Bairro buscaBairroPorId(Long id) {
        return bairroRepository.findById(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Bairro não encontrado"));
    }
    
    public List<Bairro> listarPorMunicipio(Long municipioId) {
        return bairroRepository.findByMunicipioIdOrderByNomeAsc(municipioId);
    }
}