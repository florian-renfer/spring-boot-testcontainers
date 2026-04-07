package com.github.florian_renfer.testcontainers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
public class TestcontainersApplication {

  public static void main(String[] args) {
    SpringApplication.run(TestcontainersApplication.class, args);
  }
}
