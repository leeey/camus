package project.camus.hexagonal.web.task.delegator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;
import project.camus.hexagonal.usecase.task.dto.request.CreateTaskRequestUseCaseDto;
import project.camus.hexagonal.web.task.controller.dto.TaskDto;
import project.camus.hexagonal.web.task.controller.request.CreateTaskRequest;

@Mapper
public interface TaskWebMapper {

    TaskWebMapper INSTANCE = Mappers.getMapper(TaskWebMapper.class);

    @Mapping(target = "task", source = "request")
    CreateTaskRequestUseCaseDto toDto(CreateTaskRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "archived", ignore = true)
    @Mapping(target = "priority", source = "request.priorityType.priority")
    TaskUseCaseDto map(CreateTaskRequest request);

    @Mapping(target = "priorityType", expression = "java(TaskPriorityType.findByPriority(dto.getPriority()))")
    TaskDto map(TaskUseCaseDto dto);
}
