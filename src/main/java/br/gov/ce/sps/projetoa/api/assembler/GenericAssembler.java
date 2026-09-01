package br.gov.ce.sps.projetoa.api.assembler;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapeia entidades JPA para DTOs com {@code open-in-view=false}.
 */
@Component
@RequiredArgsConstructor
public class GenericAssembler {

    private final ModelMapper modelMapper;

    public <T, U> U toModel(T entity, Class<U> modelClass) {
        return mapSingle(entity, modelClass);
    }

    public <T, U> List<U> toCollectionModel(List<T> entities, Class<U> modelClass) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream().map(entity -> mapSingle(entity, modelClass)).collect(Collectors.toList());
    }

    public <T, U> Set<U> toCollectionModelSet(Set<T> entities, Class<U> modelClass) {
        if (entities == null) {
            return Set.of();
        }
        return entities.stream().map(entity -> mapSingle(entity, modelClass)).collect(Collectors.toSet());
    }

    public <T, U> U toEntity(T input, Class<U> entityClass) {
        return modelMapper.map(input, entityClass);
    }

    private <T, U> U mapSingle(T entity, Class<U> modelClass) {
        if (entity == null) {
            return null;
        }
        DtoMappingLazyInit.initializeForMapping(entity);
        return modelMapper.map(entity, modelClass);
    }
}
