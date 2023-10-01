package project.laflex.infra.r2dbc.model.member;

import java.math.BigInteger;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import project.laflex.infra.r2dbc.model.R2dbcAuditEntity;

@Getter
@Table(name = "member")
public class MemberEntity extends R2dbcAuditEntity {

  @Builder
  public MemberEntity(Timestamp createdAt, String createdBy, Timestamp lastModifiedAt, String lastModifiedBy,
      BigInteger id, String username, String phone) {
    super(createdAt, createdBy, lastModifiedAt, lastModifiedBy);
    this.id = id;
    this.username = username;
    this.phone = phone;
  }

  @Id
  private BigInteger id;

  private String username;

  private String phone;
}
