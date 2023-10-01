package project.laflex.infra.jpa;

import jakarta.persistence.MappedSuperclass;
import java.sql.Timestamp;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@MappedSuperclass
public class AuditEntity {

  @CreatedDate
  private Timestamp createdAt;

  @CreatedBy
  private String createdBy;

  @LastModifiedDate
  private Timestamp lastModifiedAt;

  @LastModifiedBy
  private String lastModifiedBy;

}
