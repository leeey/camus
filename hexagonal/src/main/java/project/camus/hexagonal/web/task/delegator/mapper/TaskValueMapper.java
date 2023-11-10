package project.camus.hexagonal.web.task.delegator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;
import project.camus.hexagonal.web.task.controller.dto.TaskDto;
import project.camus.hexagonal.web.task.controller.request.CreateTaskRequest;

@Mapper
public interface TaskValueMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "archived", ignore = true)
    @Mapping(target = "priority", source = "request.priorityType.priority")
    TaskUseCaseDto map(CreateTaskRequest request);

    @Mapping(target = "priorityType", expression = "java(TaskPriorityType.findByPriority(dto.getPriority()))")
    TaskDto map(TaskUseCaseDto dto);
}
