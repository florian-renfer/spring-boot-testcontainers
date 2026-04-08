package com.github.florian_renfer.testcontainers.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.florian_renfer.testcontainers.config.security.SecurityConfig;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerWebMvcTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private UserRepository userRepository;

  @Test
  void shouldRejectApiRequestsWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized());

    then(userRepository).should(never()).findAll();
  }

  @Test
  void shouldNotPersistPostRequestsWithoutAuthentication() throws Exception {
    mockMvc
        .perform(
            post("/api/users")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsBytes(
                        user("Max", "Mustermann", OffsetDateTime.parse("1994-08-17T10:15:30Z")))))
        .andExpect(status().isUnauthorized());

    then(userRepository).should(never()).save(any(User.class));
  }

  @Test
  void shouldRedirectToGithubWhenStartingOauth2Login() throws Exception {
    mockMvc
        .perform(get("/oauth2/authorization/github"))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            header()
                .string(
                    "Location",
                    org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.startsWith(
                            "https://github.com/login/oauth/authorize"),
                        org.hamcrest.Matchers.containsString("client_id=test-client"))));
  }

  private static User user(String firstName, String lastName, OffsetDateTime dateOfBirth) {
    return User.builder().firstName(firstName).lastName(lastName).dateOfBirth(dateOfBirth).build();
  }
}
