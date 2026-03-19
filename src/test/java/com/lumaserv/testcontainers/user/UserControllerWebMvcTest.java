package com.lumaserv.testcontainers.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerWebMvcTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private UserRepository userRepository;

  @Test
  void shouldReturnUsersFromRepository() throws Exception {
    User storedUser = user("Max", "Mustermann", OffsetDateTime.parse("1994-08-17T10:15:30Z"));
    storedUser.setId(UUID.fromString("9e2f8d3d-bf50-4b68-8e88-c8f889757cb1"));
    given(userRepository.findAll()).willReturn(List.of(storedUser));

    mockMvc
        .perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value("9e2f8d3d-bf50-4b68-8e88-c8f889757cb1"))
        .andExpect(jsonPath("$[0].firstName").value("Max"))
        .andExpect(jsonPath("$[0].lastName").value("Mustermann"))
        .andExpect(jsonPath("$[0].dateOfBirth").value("1994-08-17T10:15:30Z"));

    then(userRepository).should().findAll();
  }

  @Test
  void shouldCreateUserWhenPayloadIsValid() throws Exception {
    OffsetDateTime dateOfBirth = OffsetDateTime.parse("1994-08-17T10:15:30Z");
    User requestUser = user("Max", "Mustermann", dateOfBirth);
    User savedUser = user("Max", "Mustermann", dateOfBirth);
    savedUser.setId(UUID.fromString("f402fd7b-6d37-4102-9d2e-5be67b53a3b9"));
    given(userRepository.save(any(User.class))).willReturn(savedUser);

    mockMvc
        .perform(
            post("/api/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestUser)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value("f402fd7b-6d37-4102-9d2e-5be67b53a3b9"))
        .andExpect(jsonPath("$.firstName").value("Max"))
        .andExpect(jsonPath("$.lastName").value("Mustermann"))
        .andExpect(jsonPath("$.dateOfBirth").value("1994-08-17T10:15:30Z"));

    then(userRepository)
        .should()
        .save(
            any(User.class));
  }

  @Test
  void shouldRejectMissingMandatoryFields() throws Exception {
    User invalidUser = new User();
    invalidUser.setFirstName("Max");

    mockMvc
        .perform(
            post("/api/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidUser)))
        .andExpect(status().isBadRequest());

    then(userRepository).should(never()).save(any(User.class));
  }

  @Test
  void shouldRejectFutureDateOfBirth() throws Exception {
    User invalidUser = user("Max", "Mustermann", OffsetDateTime.now().plusDays(1));

    mockMvc
        .perform(
            post("/api/users")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(invalidUser)))
        .andExpect(status().isBadRequest());

    then(userRepository).should(never()).save(any(User.class));
  }

  private static User user(String firstName, String lastName, OffsetDateTime dateOfBirth) {
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setDateOfBirth(dateOfBirth);
    return user;
  }
}
