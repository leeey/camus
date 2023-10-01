package project.laflex.infra.r2dbc.model;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@AllArgsConstructor
public class R2dbcAuditEntity {

  @CreatedDate
  private Timestamp createdAt;

  @CreatedBy
  private String createdBy;

  @LastModifiedDate
  private Timestamp lastModifiedAt;

  @LastModifiedBy
  private String lastModifiedBy;
}
