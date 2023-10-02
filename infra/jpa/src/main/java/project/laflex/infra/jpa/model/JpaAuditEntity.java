package project.laflex.infra.jpa.model;

import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@MappedSuperclass
public class JpaAuditEntity {

  @CreatedDate
  private LocalDateTime createdAt;

  @CreatedBy
  private String createdBy;

  @LastModifiedDate
  private LocalDateTime lastModifiedAt;

  @LastModifiedBy
  private String lastModifiedBy;

}
