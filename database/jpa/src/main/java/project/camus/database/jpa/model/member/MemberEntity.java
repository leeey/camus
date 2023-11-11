package project.camus.database.jpa.model.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.camus.database.jpa.model.JpaAuditEntity;

@Builder(toBuilder = true)
@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
public class MemberEntity extends JpaAuditEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Long id;

  private String username;

  private String phone;
}
