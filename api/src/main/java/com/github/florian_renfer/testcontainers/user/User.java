package com.github.florian_renfer.testcontainers.user;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @NotBlank
  @Length(min = 2, max = 255)
  String firstName;

  @NotBlank
  @Length(min = 2, max = 255)
  String lastName;

  @Nullable @Past OffsetDateTime dateOfBirth;

  @CreationTimestamp
  @Setter(value = AccessLevel.NONE)
  OffsetDateTime createdAt;

  @UpdateTimestamp
  @Setter(value = AccessLevel.NONE)
  OffsetDateTime updatedAt;
}
