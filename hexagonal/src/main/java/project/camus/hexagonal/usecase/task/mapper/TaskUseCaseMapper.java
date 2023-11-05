package project.camus.hexagonal.usecase.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import project.camus.hexagonal.port.task.dto.request.CreateTaskRequestPortDto;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;

@Mapper
public interface TaskUseCaseMapper {

    TaskUseCaseMapper INSTANCE = Mappers.getMapper(TaskUseCaseMapper.class);

    CreateTaskRequestPortDto toPortDto(TaskUseCaseDto dto);
}
