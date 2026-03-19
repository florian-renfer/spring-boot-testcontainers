package com.lumaserv.testcontainers.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumaserv.testcontainers.support.AbstractMariaDbIntegrationTest;
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
class UserControllerIntegrationTest extends AbstractMariaDbIntegrationTest {

  @Autowired private TestRestTemplate testRestTemplate;

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void shouldCreateUserAndExposeItViaFindAll() {
    User requestUser = user("Max", "Mustermann", OffsetDateTime.parse("1994-08-17T10:15:30Z"));

    ResponseEntity<User> created =
        testRestTemplate.postForEntity("/api/users", requestUser, User.class);

    assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(created.getBody()).isNotNull();
    assertThat(created.getBody().getId()).isNotNull();
    assertThat(created.getBody().getCreatedAt()).isNotNull();
    assertThat(created.getBody().getUpdatedAt()).isNotNull();
    assertThat(created.getBody().getFirstName()).isEqualTo("Max");
    assertThat(created.getBody().getLastName()).isEqualTo("Mustermann");

    ResponseEntity<User[]> listed = testRestTemplate.getForEntity("/api/users", User[].class);

    assertThat(listed.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(listed.getBody()).isNotNull();
    assertThat(listed.getBody()).hasSize(1);
    assertThat(listed.getBody()[0].getId()).isEqualTo(created.getBody().getId());
    assertThat(listed.getBody()[0].getDateOfBirth())
        .isEqualTo(OffsetDateTime.parse("1994-08-17T10:15:30Z"));
    assertThat(userRepository.findAll()).hasSize(1);
  }

  @Test
  void shouldRejectInvalidUserWithoutPersistingIt() {
    User invalidUser = new User();
    invalidUser.setFirstName("Max");

    ResponseEntity<String> response =
        testRestTemplate.postForEntity("/api/users", invalidUser, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(userRepository.count()).isZero();
  }

  private static User user(String firstName, String lastName, OffsetDateTime dateOfBirth) {
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setDateOfBirth(dateOfBirth);
    return user;
  }
}
