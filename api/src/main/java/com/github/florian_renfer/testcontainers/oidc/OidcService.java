package com.github.florian_renfer.testcontainers.oidc;

import com.github.florian_renfer.testcontainers.user.User;
import com.github.florian_renfer.testcontainers.user.UserService;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class OidcService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

  /** Service providing business logic for {@link User} entities. */
  private final UserService userService;

  /** Utility function converting an {@link OidcUser} entity to an {@link User} entity. */
  Function<OAuth2User, User> oidcUserMapper = OidcUtils::getGitHubUser;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = delegate.loadUser(userRequest);
    userService.save(oidcUserMapper.apply(oauth2User));
    return oauth2User;
  }
}
