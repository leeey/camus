package project.camus.hexagonal.web.task.delegator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import project.camus.hexagonal.domain.enums.TaskPriorityType;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;
import project.camus.hexagonal.usecase.task.dto.response.FindAllTasksResponseUseCaseDto;
import project.camus.hexagonal.web.task.controller.dto.TaskDto;
import project.camus.hexagonal.web.task.controller.response.FindAllTasksResponse;

@Mapper(imports = {TaskPriorityType.class})
public interface TaskFindMapper {

    TaskFindMapper INSTANCE = Mappers.getMapper(TaskFindMapper.class);

    @Mapping(target = "tasks", source = "dto.tasks")
    FindAllTasksResponse toResponse(FindAllTasksResponseUseCaseDto dto);

    @Mapping(target = "priorityType", expression = "java(TaskPriorityType.findByPriority(dto.getPriority()))")
    TaskDto map(TaskUseCaseDto dto);
}
