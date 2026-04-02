package com.github.florian_renfer.testcontainers.user;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<User>> findAll() {
    return ResponseEntity.ok(userRepository.findAll());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public User save(@RequestBody @Valid User user) {
    return userRepository.save(user);
  }
}
