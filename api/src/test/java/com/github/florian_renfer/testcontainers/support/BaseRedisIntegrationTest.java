package com.github.florian_renfer.testcontainers.support;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public abstract class BaseRedisIntegrationTest extends BaseIntegrationTest {

  @Container @ServiceConnection
  static final RedisContainer redisContainer =
      new RedisContainer(DockerImageName.parse("redis:8-alpine")) {
        @Override
        public void stop() {
          // Keep the shared container alive for the full test run so all Redis-backed contexts reuse
          // it.
        }
      };
}
