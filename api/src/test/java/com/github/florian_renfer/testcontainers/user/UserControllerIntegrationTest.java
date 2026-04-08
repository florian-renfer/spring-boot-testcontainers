package com.github.florian_renfer.testcontainers.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.florian_renfer.testcontainers.support.BaseIntegrationTest;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired private TestRestTemplate testRestTemplate;

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void shouldRejectUnauthenticatedGetRequests() {
    ResponseEntity<String> response =
        testRestTemplate.getForEntity("/api/users", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(userRepository.count()).isZero();
  }

  @Test
  void shouldRejectUnauthenticatedPostRequestsWithoutPersistingIt() {
    User requestUser = user("Max", "Mustermann", OffsetDateTime.parse("1994-08-17T10:15:30Z"));

    ResponseEntity<String> response =
        testRestTemplate.postForEntity("/api/users", requestUser, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(userRepository.count()).isZero();
  }

  private static User user(String firstName, String lastName, OffsetDateTime dateOfBirth) {
    return User.builder().firstName(firstName).lastName(lastName).dateOfBirth(dateOfBirth).build();
  }
}
