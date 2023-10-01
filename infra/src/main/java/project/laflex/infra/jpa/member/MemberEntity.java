package project.laflex.infra.jpa.member;

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
import project.laflex.infra.jpa.AuditEntity;

@Builder
@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
public class MemberEntity extends AuditEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private BigInteger id;

  private String username;

  private String phone;
}
