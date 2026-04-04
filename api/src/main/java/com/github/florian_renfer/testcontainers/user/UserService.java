package com.github.florian_renfer.testcontainers.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService {

  /** Repository managing persistent {@link User} entitites. */
  private final UserRepository userRepository;

  public User save(@Valid @NotNull User user) {
    return userRepository.save(user);
  }
}
