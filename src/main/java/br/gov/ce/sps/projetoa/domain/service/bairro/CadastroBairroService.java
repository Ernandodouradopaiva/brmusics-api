package br.gov.ce.sps.projetoa.domain.service.bairro;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Bairro;
import br.gov.ce.sps.projetoa.domain.model.Municipio;
import br.gov.ce.sps.projetoa.domain.repository.BairroRepository;
import br.gov.ce.sps.projetoa.domain.service.municipio.GetMunicipioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CadastroBairroService {
    
    private final BairroRepository bairroRepository;
    private final GetMunicipioService getMunicipioService;
    
    public Bairro cadastrar(Bairro bairro, Long municipioId) {
        // 1. Validar que o Município existe (HIERARQUIA)
        Municipio municipio = getMunicipioService.buscarPorId(municipioId);
        bairro.setMunicipio(municipio);
        
        // 2. Validar se bairro já existe no mesmo município
        bairroRepository.findByNomeAndMunicipioId(bairro.getNome(), municipioId)
            .ifPresent(b -> {
                throw new NegocioException(
                    String.format("Já existe um bairro '%s' cadastrado no município %s", 
                        bairro.getNome(), municipio.getNome()));
            });
        
        // 3. Normalizar nome
        bairro.setNome(bairro.getNome().toUpperCase());
        
        return bairroRepository.save(bairro);
    }
}