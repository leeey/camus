package project.camus.database.r2dbc.model.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import project.camus.database.r2dbc.model.R2dbcAuditEntity;

@SuperBuilder(toBuilder = true)
@Getter
@Table(name = "member")
@NoArgsConstructor
public class MemberEntity extends R2dbcAuditEntity {

  @Id
  private Long id;

  private String username;

  private String phone;
}
