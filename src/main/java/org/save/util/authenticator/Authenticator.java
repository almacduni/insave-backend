package org.save.util.authenticator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class Authenticator {

  public static boolean isUserAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return !(authentication instanceof AnonymousAuthenticationToken);
  }
}
