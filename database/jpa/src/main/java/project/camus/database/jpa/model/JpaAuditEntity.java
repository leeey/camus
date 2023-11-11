package project.camus.database.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class JpaAuditEntity {

  @Column(updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(updatable = false)
  @CreatedBy
  private String createdBy;

  @LastModifiedDate
  private LocalDateTime lastModifiedAt;

  @LastModifiedBy
  private String lastModifiedBy;

}
