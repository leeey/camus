package project.kamus.infra.r2dbc.model.member;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import project.kamus.infra.r2dbc.model.R2dbcAuditEntity;

@Builder(toBuilder = true)
@Getter
@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor
public class MemberEntity extends R2dbcAuditEntity {

  @Id
  private BigInteger id;

  private String username;

  private String phone;
}
