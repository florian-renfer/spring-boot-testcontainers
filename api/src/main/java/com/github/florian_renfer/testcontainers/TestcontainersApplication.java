package com.github.florian_renfer.testcontainers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication
public class TestcontainersApplication {

  public static void main(String[] args) {
    SpringApplication.run(TestcontainersApplication.class, args);
  }
}
