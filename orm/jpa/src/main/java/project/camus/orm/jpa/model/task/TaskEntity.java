package project.camus.orm.jpa.model.task;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.orm.jpa.model.JpaAuditEntity;

@Builder(toBuilder = true)
@Getter
@Entity
@Table(name = "task")
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity extends JpaAuditEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private BigInteger id;

    private String title;

    private String content;

    private Integer priority;

    private boolean archived;
}
