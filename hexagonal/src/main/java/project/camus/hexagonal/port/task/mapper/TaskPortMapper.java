package project.camus.hexagonal.port.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import project.camus.hexagonal.port.task.dto.request.CreateTaskRequestPortDto;
import project.camus.hexagonal.port.task.dto.response.CreateTaskResponsePortDto;
import project.camus.hexagonal.port.task.dto.response.FindAllTasksResponsePortDto;
import project.camus.hexagonal.usecase.task.dto.response.CreateTaskResponseUseCaseDto;
import project.camus.hexagonal.usecase.task.dto.response.FindAllTasksResponseUseCaseDto;
import project.camus.orm.jpa.model.task.TaskEntity;

@Mapper
public interface TaskPortMapper {

    TaskPortMapper INSTANCE = Mappers.getMapper(TaskPortMapper.class);

    @Mapping(target = "id", ignore = true)
    TaskEntity toEntity(CreateTaskRequestPortDto dto);

    CreateTaskResponseUseCaseDto toDto(CreateTaskResponsePortDto dto);

    FindAllTasksResponseUseCaseDto toDto(FindAllTasksResponsePortDto dto);
}
