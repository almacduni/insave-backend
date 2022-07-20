package org.save.model.dto.auth;

import org.save.model.enums.VerificationStep;

public interface CurrentUserResponse {

  Long getId();

  String getEmail();

  String getUsername();

  String getBio();

  String getAvatarLink();

  String getReferralLink();

  VerificationStep getVerified();
}
