package project.camus.hexagonal.web.task.delegator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import project.camus.hexagonal.domain.enums.TaskPriorityType;
import project.camus.hexagonal.usecase.task.dto.response.FindAllTasksResponseUseCaseDto;
import project.camus.hexagonal.web.task.controller.response.FindAllTasksResponse;

@Mapper(imports = {TaskPriorityType.class})
public interface TaskFindMapper extends TaskValueMapper {

    TaskFindMapper INSTANCE = Mappers.getMapper(TaskFindMapper.class);

    @Mapping(target = "tasks", source = "dto.tasks")
    FindAllTasksResponse toResponse(FindAllTasksResponseUseCaseDto dto);
}
