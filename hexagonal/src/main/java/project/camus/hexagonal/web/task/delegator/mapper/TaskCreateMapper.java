package project.camus.hexagonal.web.task.delegator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;
import project.camus.hexagonal.usecase.task.dto.request.CreateTaskRequestUseCaseDto;
import project.camus.hexagonal.usecase.task.dto.response.CreateTaskResponseUseCaseDto;
import project.camus.hexagonal.web.task.controller.request.CreateTaskRequest;
import project.camus.hexagonal.web.task.controller.response.CreateTaskResponse;

@Mapper
public interface TaskCreateMapper {

    TaskCreateMapper INSTANCE = Mappers.getMapper(TaskCreateMapper.class);

    @Mapping(target = "task", source = "request")
    CreateTaskRequestUseCaseDto toDto(CreateTaskRequest request);

    CreateTaskResponse toResponse(CreateTaskResponseUseCaseDto dto);

    default TaskUseCaseDto map(CreateTaskRequest request) {

        return TaskUseCaseDto.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .priority(request.getPriorityType().getPriority())
            .archived(false)
            .build();
    }
}
