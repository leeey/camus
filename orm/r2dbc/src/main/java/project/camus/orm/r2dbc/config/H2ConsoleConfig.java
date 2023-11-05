package project.camus.orm.r2dbc.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
public class H2ConsoleConfig {

  @Value("${h2.console.port}")
  private Integer port;

  private Server webServer;

  @EventListener(ContextRefreshedEvent.class)
  public void start() throws java.sql.SQLException {
    log.info("started h2 console at port {}.", port);
    webServer = Server.createWebServer("-webPort", port.toString()).start();
  }

  @EventListener(ContextClosedEvent.class)
  public void stop() {
    log.info("stopped h2 console at port {}.", port);
    webServer.stop();
  }
}
