package project.kamus.infra.r2dbc.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class R2dbcAuditEntity {

  @CreatedDate
  private LocalDateTime createdAt;

  @CreatedBy
  private String createdBy;

  @LastModifiedDate
  private LocalDateTime lastModifiedAt;

  @LastModifiedBy
  private String lastModifiedBy;
}
