package project.camus.hexagonal.web.task.delegator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import project.camus.hexagonal.domain.enums.TaskPriorityType;
import project.camus.hexagonal.usecase.task.dto.TaskUseCaseDto;
import project.camus.hexagonal.web.task.controller.response.ArchiveTaskResponse;

@Mapper(imports = {TaskPriorityType.class})
public interface TaskArchiveMapper extends TaskValueMapper {

    TaskArchiveMapper INSTANCE = Mappers.getMapper(TaskArchiveMapper.class);

    @Mapping(target = "task", source = "dto")
    ArchiveTaskResponse toResponse(TaskUseCaseDto dto);
}
