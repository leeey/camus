package project.camus.database.r2dbc.model;

import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class R2dbcEntityAuditAware implements ReactiveAuditorAware<String> {

  @NonNull
  @Override
  public Mono<String> getCurrentAuditor() {

    return Mono.just("System");
  }
}
