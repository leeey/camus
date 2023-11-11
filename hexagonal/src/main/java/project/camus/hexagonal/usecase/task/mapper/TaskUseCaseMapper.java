package project.camus.hexagonal.usecase.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import project.camus.hexagonal.domain.enums.TaskPriorityType;
import project.camus.hexagonal.port.task.dto.request.CreateTaskRequestPortDto;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;
import project.camus.hexagonal.usecase.task.dto.response.FindAllTasksResponseUseCaseDto;
import project.camus.hexagonal.web.task.controller.dto.TaskDto;
import project.camus.hexagonal.web.task.controller.response.ArchiveTaskResponse;
import project.camus.hexagonal.web.task.controller.response.CreateTaskResponse;
import project.camus.hexagonal.web.task.controller.response.FindAllTasksResponse;

@Mapper(imports = {TaskPriorityType.class})
public interface TaskUseCaseMapper {

    TaskUseCaseMapper INSTANCE = Mappers.getMapper(TaskUseCaseMapper.class);

    CreateTaskRequestPortDto toPortDto(TaskUseCaseDto dto);

    @Mapping(target = "task", source = "dto")
    CreateTaskResponse toCreateResponse(TaskUseCaseDto dto);

    @Mapping(target = "tasks", source = "dto.tasks")
    FindAllTasksResponse toFindAllResponse(FindAllTasksResponseUseCaseDto dto);

    @Mapping(target = "task", source = "dto")
    ArchiveTaskResponse toArchiveResponse(TaskUseCaseDto dto);

    @Mapping(target = "priorityType", expression = "java(TaskPriorityType.findByPriority(dto.getPriority()))")
    TaskDto map(TaskUseCaseDto dto);
}
