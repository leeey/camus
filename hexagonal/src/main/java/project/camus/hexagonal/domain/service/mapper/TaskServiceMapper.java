package project.camus.hexagonal.domain.service.mapper;

import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import project.camus.hexagonal.domain.enums.TaskPriorityType;
import project.camus.hexagonal.port.task.dto.TaskPortDto;
import project.camus.hexagonal.port.task.dto.response.CreateTaskResponsePortDto;
import project.camus.hexagonal.port.task.dto.response.FindAllTasksResponsePortDto;
import project.camus.orm.jpa.model.task.TaskEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface TaskServiceMapper {

    TaskServiceMapper INSTANCE = Mappers.getMapper(TaskServiceMapper.class);

    CreateTaskResponsePortDto toPortDto(TaskEntity task);

    @Mapping(target = "tasks", expression = "java(map(page.getContent()))")
    FindAllTasksResponsePortDto toPortDto(Page<TaskEntity> page);

    List<TaskPortDto> map(List<TaskEntity> entities);
}
