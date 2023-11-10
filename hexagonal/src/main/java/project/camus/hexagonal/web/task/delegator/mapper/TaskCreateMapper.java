package project.camus.hexagonal.web.task.delegator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;
import project.camus.hexagonal.usecase.task.dto.request.CreateTaskRequestUseCaseDto;
import project.camus.hexagonal.web.task.controller.request.CreateTaskRequest;
import project.camus.hexagonal.web.task.controller.response.CreateTaskResponse;

@Mapper
public interface TaskCreateMapper extends TaskValueMapper {

    TaskCreateMapper INSTANCE = Mappers.getMapper(TaskCreateMapper.class);

    @Mapping(target = "task", source = "request")
    CreateTaskRequestUseCaseDto toDto(CreateTaskRequest request);

    @Mapping(target = "task", source = "dto")
    CreateTaskResponse toResponse(TaskUseCaseDto dto);
}
