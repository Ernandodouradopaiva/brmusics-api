package br.gov.ce.sps.projetoa.domain.service.municipio;

import br.gov.ce.sps.projetoa.domain.exception.NegocioException;
import br.gov.ce.sps.projetoa.domain.model.Estado;
import br.gov.ce.sps.projetoa.domain.model.Municipio;
import br.gov.ce.sps.projetoa.domain.repository.MunicipioRepository;
import br.gov.ce.sps.projetoa.domain.service.estado.GetEstadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CadastroMunicipioService {
    
    private final MunicipioRepository municipioRepository;
    private final GetEstadoService getEstadoService;
    
    public Municipio cadastrar(Municipio municipio, Long estadoId) {
        // 1. Validar que o Estado existe (HIERARQUIA)
        Estado estado = getEstadoService.buscarPorId(estadoId);
        municipio.setEstado(estado);
        
        // 2. Validar código IBGE único
        municipioRepository.findByCodigoIbge(municipio.getCodigoIbge())
            .ifPresent(m -> {
                throw new NegocioException(
                    "Já existe um município cadastrado com o código IBGE " + municipio.getCodigoIbge());
            });
        
        // 3. Normalizar nome
        municipio.setNome(municipio.getNome().toUpperCase());
        
        return municipioRepository.save(municipio);
    }
}