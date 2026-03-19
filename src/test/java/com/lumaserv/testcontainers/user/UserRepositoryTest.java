package com.lumaserv.testcontainers.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lumaserv.testcontainers.support.AbstractMariaDbIntegrationTest;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest extends AbstractMariaDbIntegrationTest {

  @Autowired private UserRepository userRepository;

  @Autowired private TestEntityManager testEntityManager;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    testEntityManager.flush();
    testEntityManager.clear();
  }

  @Test
  void shouldPersistGeneratedFields() {
    OffsetDateTime dateOfBirth = OffsetDateTime.parse("1994-08-17T10:15:30Z");
    User saved = userRepository.save(user("Max", "Mustermann", dateOfBirth));

    testEntityManager.flush();
    testEntityManager.clear();

    User persisted = userRepository.findById(saved.getId()).orElseThrow();

    assertThat(persisted.getId()).isNotNull();
    assertThat(persisted.getFirstName()).isEqualTo("Max");
    assertThat(persisted.getLastName()).isEqualTo("Mustermann");
    assertThat(persisted.getDateOfBirth()).isEqualTo(dateOfBirth);
    assertThat(persisted.getCreatedAt()).isNotNull();
    assertThat(persisted.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldFindAllUsersSharingLastName() {
    userRepository.save(user("Max", "Mustermann", OffsetDateTime.parse("1990-01-01T00:00:00Z")));
    userRepository.save(user("Maria", "Mustermann", OffsetDateTime.parse("1992-02-02T00:00:00Z")));
    userRepository.save(user("John", "Doe", OffsetDateTime.parse("1988-03-03T00:00:00Z")));

    testEntityManager.flush();
    testEntityManager.clear();

    List<User> found = userRepository.findByLastName("Mustermann");

    assertThat(found).hasSize(2);
    assertThat(found).extracting(User::getFirstName).containsExactlyInAnyOrder("Max", "Maria");
    assertThat(found).extracting(User::getLastName).containsOnly("Mustermann");
  }

  @Test
  void shouldRejectDuplicateNameAndDateOfBirth() {
    OffsetDateTime dateOfBirth = OffsetDateTime.parse("1994-08-17T10:15:30Z");
    userRepository.save(user("Max", "Mustermann", dateOfBirth));
    testEntityManager.flush();

    assertThatThrownBy(
            () -> {
              userRepository.save(user("Max", "Mustermann", dateOfBirth));
              testEntityManager.flush();
            })
        .isInstanceOf(org.hibernate.exception.ConstraintViolationException.class)
        .hasMessageContaining("uq_users_name_dob");
  }

  private static User user(String firstName, String lastName, OffsetDateTime dateOfBirth) {
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setDateOfBirth(dateOfBirth);
    return user;
  }
}
