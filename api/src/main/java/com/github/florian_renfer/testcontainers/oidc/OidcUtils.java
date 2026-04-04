package com.github.florian_renfer.testcontainers.oidc;

import com.github.florian_renfer.testcontainers.user.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

public class OidcUtils {

  public static User getGitHubUser(OAuth2User oAuth2User) {
    String name = oAuth2User.getAttribute("name");
    Assert.hasText(name, "Name required");

    String[] fullName = name.trim().split("\\s+", 2);
    Assert.isTrue(fullName.length == 2, "Firstname and lastname required");
    Assert.hasText(fullName[0], "Firstname required");
    Assert.hasText(fullName[1], "Lastname required");

    return User.builder().firstName(fullName[0]).lastName(fullName[1]).build();
  }
}
