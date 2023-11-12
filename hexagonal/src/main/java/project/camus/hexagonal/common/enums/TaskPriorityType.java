package project.camus.hexagonal.common.enums;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskPriorityType {

    HIGH(3),
    MID(2),
    LOW(1),
    UNDECIDED(0),
    ;

    private final int priority;

    public static TaskPriorityType findByPriority(Integer priority) {

        return Arrays.stream(TaskPriorityType.values())
            .filter(e -> e.getPriority() == priority)
            .findAny()
            .orElse(UNDECIDED);
    }
}
