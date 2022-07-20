package org.save.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VerificationStep {
  NOT_VERIFIED("NOT_VERIFIED"),
  PENDING("PENDING"),
  VERIFIED("VERIFIED");

  @Getter private final String verificationStep;
}
