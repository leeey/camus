package project.camus.hexagonal.domain.task.event;

/**
 * task 변경과 같은 트랜잭션에서 도메인 이벤트를 기록하는 out-port.
 */
public interface TaskEventPort {

    void append(TaskDomainEvent event);
}
