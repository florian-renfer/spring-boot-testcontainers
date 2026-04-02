package com.github.florian_renfer.testcontainers.user;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.ListCrudRepository;

public interface UserRepository extends ListCrudRepository<User, UUID> {

  /**
   * Finds users by their last name.
   *
   * @param lastName the last name to search for
   * @return list of users with the specified last name
   */
  List<User> findByLastName(String lastName);
}
