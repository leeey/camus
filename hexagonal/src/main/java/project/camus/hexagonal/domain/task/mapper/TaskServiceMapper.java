package project.camus.hexagonal.domain.task.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import project.camus.common.mapper.CommonMapper;
import project.camus.database.jpa.model.task.TaskEntity;
import project.camus.hexagonal.port.task.dto.TaskPortDto;
import project.camus.hexagonal.port.task.dto.response.FindAllTasksResponsePortDto;

@Mapper
public interface TaskServiceMapper extends CommonMapper {

    TaskServiceMapper INSTANCE = Mappers.getMapper(TaskServiceMapper.class);

    TaskPortDto toPortDto(TaskEntity task);

    @Mapping(target = "tasks", expression = "java(map(page.getContent()))")
    FindAllTasksResponsePortDto toPortDto(Page<TaskEntity> page);

    List<TaskPortDto> map(List<TaskEntity> entities);
}
