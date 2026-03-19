package com.github.florian_renfer.testcontainers.support;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.mariadb.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractMariaDbIntegrationTest {

  @Container @ServiceConnection
  static final MariaDBContainer mariaDbContainer =
      new MariaDBContainer("mariadb:11.4") {
        @Override
        public void stop() {
          // Keep the shared container alive for the full test run so all DB-backed contexts reuse it.
        }
      };
}
