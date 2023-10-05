package project.camus.infra.jpa.config;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class JpaEntityAuditAware implements AuditorAware<String> {

  @NonNull
  @Override
  public Optional<String> getCurrentAuditor() {

    return Optional.of("System");
  }
}
