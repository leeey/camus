package project.camus.hexagonal.port.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import project.camus.common.mapper.CommonMapper;
import project.camus.database.jpa.model.task.TaskEntity;
import project.camus.hexagonal.port.task.dto.TaskPortDto;
import project.camus.hexagonal.port.task.dto.request.CreateTaskRequestPortDto;
import project.camus.hexagonal.port.task.dto.response.FindAllTasksResponsePortDto;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;
import project.camus.hexagonal.usecase.task.dto.response.FindAllTasksResponseUseCaseDto;

@Mapper
public interface TaskPortMapper extends CommonMapper {

    TaskPortMapper INSTANCE = Mappers.getMapper(TaskPortMapper.class);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "id", ignore = true)
    TaskEntity toEntity(CreateTaskRequestPortDto dto);

    TaskUseCaseDto toDto(TaskPortDto dto);

    FindAllTasksResponseUseCaseDto toDto(FindAllTasksResponsePortDto dto);
}
