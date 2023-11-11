package project.camus.hexagonal.common.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface CommonMapper {

}