package com.github.florian_renfer.testcontainers.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.florian_renfer.testcontainers.support.BaseRedisIntegrationTest;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.session.store-type=redis",
      "spring.session.redis.namespace=testcontainers:session:it"
    })
class UserSecurityRedisIntegrationTest extends BaseRedisIntegrationTest {

  private static final String SESSION_NAMESPACE = "testcontainers:session:it";

  @Autowired private TestRestTemplate testRestTemplate;

  @LocalServerPort private int port;

  @Autowired private StringRedisTemplate stringRedisTemplate;

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    stringRedisTemplate.execute(
        (RedisCallback<Void>)
            connection -> {
          connection.serverCommands().flushDb();
          return null;
        });
  }

  @Test
  void shouldStoreOauthAuthorizationRequestInRedisSession() throws Exception {
    HttpURLConnection connection =
        (HttpURLConnection)
            URI.create("http://localhost:" + port + "/oauth2/authorization/github")
                .toURL()
                .openConnection();
    connection.setInstanceFollowRedirects(false);

    assertThat(connection.getResponseCode()).isEqualTo(HttpStatus.FOUND.value());
    assertThat(connection.getHeaderField(HttpHeaders.LOCATION)).isNotNull();
    assertThat(connection.getHeaderField(HttpHeaders.LOCATION))
        .startsWith("https://github.com/login/oauth/authorize")
        .contains("client_id=test-client");
    assertThat(connection.getHeaderField(HttpHeaders.SET_COOKIE)).contains("SESSION=");
    assertThat(userRepository.count()).isZero();
    assertThat(redisKeys()).anyMatch(key -> key.startsWith(SESSION_NAMESPACE + ":sessions:"));
  }

  @Test
  void shouldNotCreateRedisSessionForUnauthorizedApiRequest() {
    ResponseEntity<String> response =
        testRestTemplate.getForEntity("/api/users", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(userRepository.count()).isZero();
    assertThat(redisKeys()).isEmpty();
  }

  private Set<String> redisKeys() {
    return stringRedisTemplate.keys(SESSION_NAMESPACE + ":*");
  }
}
