package project.camus.database.redis.model.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private String id;

    private String userId;

    private String title;

    private String content;
}
