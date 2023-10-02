package project.laflex.infra.r2dbc.config;

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
